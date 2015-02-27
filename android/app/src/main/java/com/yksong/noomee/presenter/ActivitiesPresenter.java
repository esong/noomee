package com.yksong.noomee.presenter;

import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.model.FacebookUser;
import com.yksong.noomee.util.APICallback;
import com.yksong.noomee.util.ParseAPI;
import com.yksong.noomee.views.ActivitiesView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by esong on 2015-02-27.
 */
public class ActivitiesPresenter extends AbsPresenter<ActivitiesView> {

    private Request createUserRequest(final String fbId, final List<FacebookUser> users) {
        return new Request(ParseFacebookUtils.getSession(), fbId, null , null,
                new Request.Callback() {
                    @Override
                    public void onCompleted(Response response) {
                        GraphObject graphObject = response.getGraphObject();
                        if (graphObject != null) {
                            String name = (String) graphObject.getProperty("name");
                            if (name != null){
                                FacebookUser user = new FacebookUser();
                                user.id = fbId;
                                user.name = name;
                                users.add(user);
                            }
                        }
                    }
                });
    }

    public void getEventList() {
        ParseAPI.getMyAndFriendsEvents(ParseUser.getCurrentUser(),
                new APICallback<List<ParseObject>>() {
                    @Override
                    public void run(final List<ParseObject> parseObjects) {
                        final List<EatingEvent> result = new ArrayList<EatingEvent>();

                        for (final ParseObject parseObject : parseObjects) {
                            final EatingEvent eatingEvent = new EatingEvent();
                            final ArrayList<ParseObject> userObjects =
                                    (ArrayList<ParseObject>) parseObject.get("users");
                            final List<FacebookUser> users = Collections.synchronizedList(
                                    new ArrayList<FacebookUser>());
                            RequestBatch requestBatch = new RequestBatch();

                            for (int j = 0; j < userObjects.size(); j++) {
                                try {
                                    requestBatch.add(createUserRequest((String)
                                            userObjects.get(j).fetchIfNeeded().get("fbId"), users));
                                } catch (com.parse.ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            requestBatch.addCallback(new RequestBatch.Callback() {
                                @Override
                                public void onBatchCompleted(RequestBatch requests) {
                                    eatingEvent.users = users;
                                    eatingEvent.time = (Date) parseObject.get("scheduledAt");
                                    eatingEvent.location = "Fucking Tomato";

                                    result.add(eatingEvent);

                                    // create the list after all users are loaded.
                                    if (result.size() == parseObjects.size()) {
                                        getView().createList(result);
                                    }
                                }
                            });
                            requestBatch.executeAsync();
                        }
                    }
                }, 20);
    }
}
