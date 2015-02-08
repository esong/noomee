package com.yksong.noomee.network;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yksong.noomee.presenter.AbsPresenter;

import java.io.IOException;

/**
 * Created by esong on 2015-02-07.
 */
public abstract class HttpCallBack<T> implements Callback {
    private Class<T> mClass;
    private Gson mGson = new Gson();

    public HttpCallBack(Class<T> clazz) {
        mClass = clazz;
    }

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) throws IOException {
        T parsedObj = parse(response);
        callback(parsedObj);
    }

    public @Nullable
    T parse(final Response response) {
        T retObj = null;
        try {
            retObj = mGson.fromJson(response.body().charStream(), mClass);
        } catch (Exception e) {
            showErrorMessage(e);
        }
        return retObj;
    }

    public abstract void callback(@Nullable T parsedObj);

    public void showErrorMessage(Exception e){}
}
