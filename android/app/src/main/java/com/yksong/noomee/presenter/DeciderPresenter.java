package com.yksong.noomee.presenter;

import android.app.Dialog;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import com.yksong.noomee.R;
import com.yksong.noomee.model.ChiTag;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.network.HttpCallBack;
import com.yksong.noomee.network.HttpClient;
import com.yksong.noomee.network.HttpConfig;
import com.yksong.noomee.network.NoomeeClient;
import com.yksong.noomee.network.RequestBuilder;
import com.yksong.noomee.util.GeoProvider;
import com.yksong.noomee.util.NoomeeAPI;
import com.yksong.noomee.views.ChiTagView;
import com.yksong.noomee.views.DeciderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by esong on 2015-01-11.
 */
public class DeciderPresenter extends AbsPresenter<DeciderView> {
    final GeoProvider mGeoProvider = GeoProvider.getInstance();
    final NoomeeAPI mNoomeeAPI = NoomeeClient.getApi();

    public void getRestaurant() {
        final DeciderView view = getView();

        Location location = mGeoProvider.getLocation();

        if (location == null) {
            view.showLocationPromote();
            return;
        }

        ChiTag[] tags = view.getSelectedTags();
        String[] tagQuery = new String[tags.length];

        for (int i = 0; i < tags.length; ++i) {
            tagQuery[i] = tags[i].getQuery();
        }

        mNoomeeAPI.randomRestaurant(location.getLatitude(), location.getLongitude(), tagQuery,
                new PresenterCallBack<Restaurant>() {
            @Override
            public void success(Restaurant restaurant, Response response) {
                view.showRestaurant(restaurant);
            }
        });
    }

    public void getTags() {
        final DeciderView view = getView();

        Location location = mGeoProvider.getLocation();

        if (location == null) {
            view.showLocationPromote();
            return;
        }

        mNoomeeAPI.getTags(location.getLatitude(), location.getLongitude(),
                new PresenterCallBack<ChiTag[]>() {
            @Override
            public void success(ChiTag[] tags, Response response) {
                view.showTagsDialog(tags);
            }
        });
    }
}
