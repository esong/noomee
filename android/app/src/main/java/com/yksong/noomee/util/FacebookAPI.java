package com.yksong.noomee.util;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FacebookAPI {
    public static void getFriends() {
        Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                if (users != null) {
                    List<String> friendsList = new ArrayList<String>();
                    for (GraphUser user : users) {
                        friendsList.add(user.getId());
                    }

                    // Construct a ParseUser query that will find friends whose
                    // facebook IDs are contained in the current user's friend list.
                    ParseQuery friendQuery = ParseQuery.getQuery("User");
                    friendQuery.whereContainedIn("fbId", friendsList);

                    // findObjects will return a list of ParseUsers that are friends with
                    // the current user
                    List<ParseObject> friendUsers = friendQuery.find();
                }
            }
        });
    }
}
