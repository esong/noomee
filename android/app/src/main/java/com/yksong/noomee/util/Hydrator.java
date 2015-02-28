package com.yksong.noomee.util;

import com.parse.ParseObject;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.model.FacebookUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by esong on 2015-02-28.
 */
public class Hydrator {

    public static List<EatingEvent> hydrateEventFriends(List<ParseObject> parseObjects)
            throws Exception{
        List<EatingEvent> result = new ArrayList<>();

        for (ParseObject parseObject : parseObjects) {
            final EatingEvent eatingEvent = new EatingEvent();
            eatingEvent.users = new ArrayList<>();
            final ArrayList<ParseObject> userObjects =
                    (ArrayList<ParseObject>) parseObject.get("users");

            for (ParseObject userObject : userObjects) {
                eatingEvent.users.add(new FacebookUser((String)userObject.get("fbId"),
                        userObject.get("firstName") + " " + userObject.get("lastName")));
            }

            eatingEvent.time = (Date) parseObject.get("scheduledAt");
            eatingEvent.location = "Test";
            result.add(eatingEvent);
        }

        return result;
    }
}
