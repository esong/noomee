package com.yksong.noomee.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.yksong.noomee.BuildConfig;
import com.yksong.noomee.R;
import com.yksong.noomee.network.NoomeeClient;
import com.yksong.noomee.util.GeoProvider;


import java.util.Calendar;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

/**
 * Created by esong on 2014-12-08.
 */
public class NoomeeApp extends Application {
    @Override
    public void onCreate(){
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        Parse.initialize(this, "X34utz63uXlItFOEEdWYPSo92ptbC39cQIOwGvMS",
                "rkw1js5t3rr0f7ILH3Y7joFioVXoq3I38UnyP9t4");
        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

        GeoProvider.initialize(this);
        NoomeeClient.initialize(this);
        AlarmScheduler.scheduleRecommendPush(this);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
