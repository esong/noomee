package com.yksong.noomee.util;

import com.parse.ParseObject;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.model.FacebookUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            eatingEvent.createdTime = (Date) parseObject.getCreatedAt();

            Map<Object, Object> restaurantObj = (HashMap)parseObject.get("restaurantObj");

            if (restaurantObj != null) {
                eatingEvent.location = (String) restaurantObj.get("name");
                eatingEvent.restaurantId = (String) restaurantObj.get("id");
            }

            eatingEvent.eventId = parseObject.getObjectId();
            result.add(eatingEvent);
        }

        return result;
    }
}
