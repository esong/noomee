package com.yksong.noomee.util;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class ParseAPI {
    private static String className = "ParseAPI";

    public static void createEvent(final ParseObject user, int year, int month, int day, int hour, int minute) {
        final ParseObject event = new ParseObject("Event");
        event.put("scheduledAt", new GregorianCalendar(year, month, day, hour, minute).getTime());
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
