package com.yksong.noomee.network;

import android.location.Location;

import com.squareup.okhttp.Request;
import com.yksong.noomee.model.ChiTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esong on 2015-01-13.
 */
public class RequestBuilder extends Request.Builder {
    private static final String NOOMEE_URL = HttpConfig.NOOMEE_PROTOCOL + HttpConfig.NOOMEE_HOST
            + "/";
    private static final String TAG = RequestBuilder.class.getName();

    private StringBuilder mUrlStringBuilder;
    private List<Query> mQueries = new ArrayList<>();

    public RequestBuilder url(String url) {
        mUrlStringBuilder = new StringBuilder(NOOMEE_URL).append(url);
        return this;
    }

    public RequestBuilder addParam(String key, String value) {
        mQueries.add(new Query(key, value));
        return this;
    }

    public RequestBuilder addParam(Location location) {
        if (location == null) return this;
        mQueries.add(new Query("lati", Double.toString(location.getLatitude())) );
        mQueries.add(new Query("longi", Double.toString(location.getLongitude())) );
        return this;
    }

    public RequestBuilder addParam(ChiTag[] tags) {
        if (tags == null) return this;
        for (ChiTag tag : tags) {
            mQueries.add(new Query("tags", tag.getQuery()));
        }
        return this;
    }

    public Request build() {
        if (mUrlStringBuilder == null) throw new IllegalStateException("url == null");

        if (!mQueries.isEmpty()) {
            mUrlStringBuilder.append('?');
            for (Query query : mQueries) {
                mUrlStringBuilder.append(query.mField).append('=')
                        .append(query.mValue).append('&');
            }
        }

        super.url(mUrlStringBuilder.toString());

        return super.build();
    }

    private static class Query {
        String mField;
        String mValue;

        public Query(String field, String value) {
            mField = field;
            mValue = value;
        }
    }
}
