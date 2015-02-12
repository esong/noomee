package com.yksong.noomee.util;

import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FacebookAPI {
    private static String className = "FacebookAPI";

    public static void getMyFriends(final APICallback<List<ParseObject>> callback) {
        Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                if (users != null) {
                    List<String> friendsList = new ArrayList<String>();
                    for (GraphUser user : users) {
                        friendsList.add(user.getId());
                    }

                    ParseQuery<ParseObject> friendQuery = ParseQuery.getQuery("_User");
                    friendQuery.whereContainedIn("fbId", friendsList);

                    friendQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> parseObjects, ParseException e) {
                            if (e == null) {
                                callback.run(parseObjects);
                            } else {
                                Log.e(className, "Could not find my friends.");
                                callback.run(new ArrayList<ParseObject>());
                            }
                        }
                    });
                }
            }
        }).executeAsync();
    }
}
