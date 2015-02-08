package com.yksong.noomee.util;

import android.support.annotation.Nullable;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.GregorianCalendar;
import java.util.List;

public class ParseAPI {
    public static void createEvent(final ParseObject user, int year, int month, int day, int hour, int minute) {
        final ParseObject event = new ParseObject("Event");
        event.put("scheduledAt", new GregorianCalendar(year, month, day, hour, minute).getTime());

        ParseRelation<ParseObject> usersRelation = event.getRelation("users");
        usersRelation.add(user);

        event.saveInBackground();
    }

    public static void getEventsByUser(ParseObject user,
                                       final ParseAPICallBack<List<ParseObject>> callback) {
        ParseQuery<ParseObject> eventQuery = ParseQuery.getQuery("Event");
        eventQuery.whereEqualTo("users", user);
        eventQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> events, ParseException e) {
                if (e == null) {
                    callback.run(events);
                } else {
                    callback.run(null);
                }
            }
        });
    }

    public static abstract class ParseAPICallBack<T>{
        public abstract void run(@Nullable T parseObj);
    }
}
