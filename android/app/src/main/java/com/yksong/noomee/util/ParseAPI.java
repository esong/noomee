package com.yksong.noomee.util;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.yksong.noomee.model.Restaurant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ParseAPI {
    private static String className = "ParseAPI";

    public static void createEvent(final ParseObject user, int year, int month, int day, int hour,
                                   int minute, Restaurant restaurant) {
        final ParseObject event = new ParseObject("Event");
        event.put("scheduledAt", new GregorianCalendar(year, month, day, hour, minute).getTime());
        final JSONObject restaurantObj = new JSONObject();
        try {
            restaurantObj.put("id", restaurant.id);
            restaurantObj.put("name", restaurant.name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        event.put("restaurantObj", restaurantObj);
        ArrayList<ParseObject> users = new ArrayList<>();
        users.add(user);
        event.put("users", users);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseObject activity = new ParseObject("Activity");
                    activity.put("user", user);
                    activity.put("eventId", event.getObjectId());
                    activity.saveInBackground();
                } else {
                    Log.e(className, "Error saving event!");
                }
            }
        });
    }

    public static void joinEvent(final ParseObject user,
                                 final String eventId) {
        ParseObject activity = new ParseObject("Activity");
        activity.put("user", user);
        activity.put("eventId", eventId);
        activity.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                    eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                ArrayList<ParseObject> users = (ArrayList<ParseObject>)parseObject.get("users");
                                if (!users.contains(user)) {
                                    users.add(user);
                                }
                                parseObject.saveInBackground();
                            } else {
                                Log.e(className, "Error getting event!");
                            }
                        }
                    });
                } else {
                    Log.e(className, "Error joining event!");
                }
            }
        });
    }

    public static void leaveEvent(final ParseObject user,
                                  final String eventId) {
        ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
        eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    ArrayList<ParseObject> users = (ArrayList<ParseObject>)parseObject.get("users");
                    boolean mayExist = true;
                    while (mayExist) {
                        boolean removed = false;
                        for (ParseObject usr : users) {
                            if (user.getObjectId().equals(usr.getObjectId())) {
                                users.remove(usr);
                                removed = true;
                                break;
                            }
                        }
                        mayExist = removed;
                    }
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseQuery<ParseObject> activityQuery = ParseQuery.getQuery("Activity");
                                activityQuery.whereEqualTo("user", user);
                                activityQuery.whereEqualTo("eventId", eventId);
                                activityQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> parseObjects, ParseException e) {
                                        if (e == null) {
                                            for (ParseObject activity : parseObjects) {
                                                activity.deleteInBackground();
                                            }
                                        } else {
                                            Log.e(className, "Error getting event!");
                                        }
                                    }
                                });
                            } else {
                                Log.e(className, "Error saving event!");
                            }
                        }
                    });
                } else {
                    Log.e(className, "Error finding event!");
                }
            }
        });
    }

    public static void removeEvent(final ParseObject user,
                                   final String eventId) {
        ParseQuery<ParseObject> activityQuery = ParseQuery.getQuery("Activity");
        activityQuery.whereEqualTo("eventId", eventId);
        activityQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject activity : parseObjects) {
                        activity.deleteInBackground();
                    }
                    ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                    eventQuery.getInBackground(eventId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                parseObject.deleteInBackground();
                            } else {
                                Log.e(className, "Error getting event!");
                            }
                        }
                    });
                } else {
                    Log.e(className, "Error finding activity!");
                }
            }
        });
    }

    public static void getMyAndFriendsEvents(final ParseObject user,
                                             final APICallback<List<ParseObject>> callback,
                                             final int limit) {
        FacebookAPI.getMyFriends(new APICallback<List<ParseObject>>() {
            @Override
            public void run(List<ParseObject> parseObjects) {
                ParseQuery<ParseObject> activityQuery = ParseQuery.getQuery("Activity");
                parseObjects.add(user);
                activityQuery.whereContainedIn("user", parseObjects);
                ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                eventQuery.whereMatchesKeyInQuery("objectId", "eventId", activityQuery);
                eventQuery.orderByDescending("scheduledAt");
                eventQuery.setLimit(limit);
                eventQuery.include("users");
                eventQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {
                            callback.run(parseObjects);
                        } else {
                            Log.e(className, "Error querying activity!, ", e);
                            callback.run(new ArrayList<ParseObject>());
                        }
                    }
                });
            }
        });
    }
}
