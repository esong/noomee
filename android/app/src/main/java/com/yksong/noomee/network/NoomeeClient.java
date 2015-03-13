package com.yksong.noomee.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yksong.noomee.BuildConfig;
import com.yksong.noomee.R;
import com.yksong.noomee.util.NoomeeAPI;

import retrofit.RestAdapter;

/**
 * Created by esong on 2015-03-10.
 */
public class NoomeeClient {
    private static NoomeeAPI sAPI;

    public static NoomeeAPI getApi() {
        return sAPI;
    }

    public static void initialize(Context context) {
        String host = HttpConfig.NOOMEE_HOST;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (BuildConfig.DEBUG && preferences.getBoolean(
                context.getResources().getString(R.string.pref_enable_local_server), false)) {
            host = HttpConfig.NOOMEE_LOCAL_HOST;
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(HttpConfig.NOOMEE_PROTOCOL + host)
                .build();

        sAPI = restAdapter.create(NoomeeAPI.class);
    }
}
