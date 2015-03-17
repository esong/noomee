package com.yksong.noomee.util;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.start.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

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
                                ArrayList<ParseObject> users = (ArrayList<ParseObject>)
                                        parseObject.get("users");
                                if (!users.contains(user)) {
                                    users.add(user);
                                }
                                parseObject.saveInBackground();

                                sendPush(parseObject, users.get(0));
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

    private static void sendPush(ParseObject eatingEvent, ParseObject creator) {
        try {
            final ParseUser user = ParseUser.getCurrentUser();
            Date lastPush = user.getDate("lastPush");

            if (lastPush == null ||
                    (new Date()).getTime() - lastPush.getTime() > 300000) {
                ParseQuery pushQuery = ParseInstallation.getQuery();
                creator.fetchIfNeeded();
                pushQuery.whereEqualTo("fbId", creator.getString("fbId"));

                ParsePush push = new ParsePush();
                push.setQuery(pushQuery);
                push.setMessage(user.get("firstName") + " is joining you at " +
                        ((HashMap)eatingEvent.fetchIfNeeded().get("restaurantObj")).get("name"));
                push.sendInBackground(new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        user.put("lastPush",
                                new GregorianCalendar(
                                        Calendar.getInstance().getTimeZone()).getTime());
                        user.saveInBackground();
                    }
                });
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
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
                                             final APICallback<List<EatingEvent>> callback,
                                             final int skip,
                                             final int limit) {
        FacebookAPI.getMyFriends(new APICallback<List<ParseObject>>() {
            @Override
            public void run(List<ParseObject> parseObjects) {
                ParseQuery<ParseObject> activityQuery = ParseQuery.getQuery("Activity");
                parseObjects.add(user);
                activityQuery.whereContainedIn("user", parseObjects);
                ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
                eventQuery.include("users");
                eventQuery.whereMatchesKeyInQuery("objectId", "eventId", activityQuery);
                eventQuery.orderByDescending("createdAt");
                eventQuery.setSkip(skip);
                eventQuery.setLimit(limit);
                eventQuery.findInBackground()
                        .onSuccessTask(new Continuation<List<ParseObject>, Task<List<EatingEvent>>>() {
                            @Override
                            public Task<List<EatingEvent>> then(Task<List<ParseObject>> task)
                                    throws Exception {
                                return Task.forResult(Hydrator.hydrateEventFriends(task.getResult()));
                            }
                        }).onSuccess(new Continuation<List<EatingEvent>, Void>() {
                    @Override
                    public Void then(Task<List<EatingEvent>> task) throws Exception {
                        callback.run(task.getResult());
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
            }
        });
    }
}
