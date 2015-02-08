package com.yksong.noomee.presenter;

import android.app.Dialog;
import android.location.Location;
import android.view.View;

import com.squareup.okhttp.Request;

import com.yksong.noomee.R;
import com.yksong.noomee.model.ChiTag;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.network.HttpCallBack;
import com.yksong.noomee.network.HttpClient;
import com.yksong.noomee.network.HttpConfig;
import com.yksong.noomee.network.RequestBuilder;
import com.yksong.noomee.util.GeoProvider;
import com.yksong.noomee.views.ChiTagView;
import com.yksong.noomee.views.DeciderView;

import java.io.IOException;

/**
 * Created by esong on 2015-01-11.
 */
public class DeciderPresenter extends AbsPresenter<DeciderView> {
    final HttpClient mClient = HttpClient.getInstance();
    final GeoProvider mGeoProvider = GeoProvider.getInstance();

    public void getRestaurant() {
        final DeciderView view = getView();

        final Request request = new RequestBuilder()
                .url(HttpConfig.NOOMEE_RANDOM)
                .addParam("term", "restaurant")
                .addParam(mGeoProvider.getLocation())
                .addParam(view.getSelectedTags())
                .build();

        mClient.asyncCall(request, new PresenterCallBack<Restaurant>(Restaurant.class) {
            @Override
            public void UiCallBack(Restaurant restaurant) {
                view.showRestaurant(restaurant);
            }
        });
    }

    public void getTags() {
        final DeciderView view = getView();
        view.showTagsDialog();

        final Request request = new RequestBuilder()
                .url(HttpConfig.NOOMEE_TAGS)
                .addParam("term", "restaurant")
                .addParam(mGeoProvider.getLocation())
                .build();

        mClient.asyncCall(request, new PresenterCallBack<ChiTag[]>(ChiTag[].class) {
            @Override
            public void UiCallBack(ChiTag[] parsedObj) {
                Dialog dialog = view.getDialog();
                dialog.findViewById(R.id.loading).setVisibility(View.GONE);
                ChiTagView tagView = (ChiTagView)
                        dialog.findViewById(R.id.chi_tag_view);
                if (tagView != null) {
                    tagView.setTags(parsedObj);
                }
            }
        });
    }
}
