package com.yksong.noomee.util;

import android.util.Log;

<<<<<<< HEAD
import com.parse.FindCallback;
=======
import com.parse.GetCallback;
>>>>>>> 1a31e14... Request in ranges
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.yksong.noomee.model.EatingEvent;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

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
                eventQuery.orderByDescending("scheduledAt");
                eventQuery.setSkip(skip);
                eventQuery.setLimit(limit);
<<<<<<< HEAD
                eventQuery.findInBackground(new FindCallback<ParseObject>() {
=======
                eventQuery.findInBackground()
                   .onSuccessTask(new Continuation<List<ParseObject>, Task<List<EatingEvent>>>() {
                       @Override
                       public Task<List<EatingEvent>> then(Task<List<ParseObject>> task)
                               throws Exception {
                           return Task.forResult(Hydrator.hydrateEventFriends(task.getResult()));
                       }
                   }).onSuccess(new Continuation<List<EatingEvent>, Void>() {
>>>>>>> 1a31e14... Request in ranges
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
