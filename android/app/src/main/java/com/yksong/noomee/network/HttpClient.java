package com.yksong.noomee.network;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.yksong.noomee.BuildConfig;

/**
 * Created by esong on 2015-01-13.
 */
public class HttpClient {
    private final static String TAG = HttpClient.class.getName();
    private final static HttpClient sInstance = new HttpClient();

    private OkHttpClient client = new OkHttpClient();

    public static HttpClient getInstance(){
        return sInstance;
    }

    public void asyncCall(Request request, Callback callback) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Url: " + request.urlString());
            Log.d(TAG, "Method: " + request.method());
        }

        client.newCall(request).enqueue(callback);
    }
}
