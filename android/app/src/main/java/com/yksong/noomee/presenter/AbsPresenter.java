package com.yksong.noomee.presenter;

import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yksong.noomee.model.Restaurant;

import java.io.IOException;

/**
 * Created by esong on 2015-01-11.
 */
public abstract class AbsPresenter<T extends View> {
    private static final String sError =  "Network Connection Error: ";
    private T mView;
    private Gson mGson = new Gson();

    public void setView(T view) {
        mView = view;
    }

    protected T getView() {
        return mView;
    }

    public abstract class HttpCallBack<T> implements Callback{
        private final Handler mHandler = AbsPresenter.this.getView().getHandler();
        private Class<T> mClass;

        public HttpCallBack(Class<T> clazz) {
            mClass = clazz;
        }

        @Override
        public void onFailure(Request request,final IOException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getView().getContext(), sError + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            final T parsedObj = parse(response);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback(parsedObj);
                }
            });
        }

        public T parse(final Response response) {
            return mGson.fromJson(response.body().charStream(), mClass);
        }

        public abstract void callback(T parsedObj);
    }
}
