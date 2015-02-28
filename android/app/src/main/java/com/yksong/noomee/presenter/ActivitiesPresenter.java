package com.yksong.noomee.presenter;

import com.parse.ParseUser;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.util.APICallback;
import com.yksong.noomee.util.ParseAPI;
import com.yksong.noomee.views.ActivitiesView;

import java.util.List;

/**
 * Created by esong on 2015-02-27.
 */
public class ActivitiesPresenter extends AbsPresenter<ActivitiesView> {

    public void getEventList(final int skip, final int limit) {
        final long start = System.currentTimeMillis();
        ParseAPI.getMyAndFriendsEvents(ParseUser.getCurrentUser(),
                new APICallback<List<EatingEvent>>() {
                    @Override
                    public void run(List<EatingEvent> result) {
                        System.out.println("Total: " + (System.currentTimeMillis() - start));
                        getView().createList(result, skip);
                    }
                }, skip, limit);
    }
}
