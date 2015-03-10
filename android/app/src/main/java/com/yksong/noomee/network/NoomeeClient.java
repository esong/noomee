package com.yksong.noomee.network;

import com.yksong.noomee.util.NoomeeAPI;

import retrofit.RestAdapter;

/**
 * Created by esong on 2015-03-10.
 */
public class NoomeeClient {
    private static NoomeeAPI sAPI;

    static {
        setUpApi();
    }

    public static NoomeeAPI getApi() {
        return sAPI;
    }

    private static void setUpApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HttpConfig.NOOMEE_PROTOCOL + HttpConfig.NOOMEE_HOST)
                .build();

        sAPI = restAdapter.create(NoomeeAPI.class);
    }
}
