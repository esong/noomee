package com.yksong.noomee.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.yksong.noomee.BuildConfig;

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
    }
}
