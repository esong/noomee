package com.yksong.noomee.presenter;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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

        private void showErrorMessage(final Exception e){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getView().getContext(), sError + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onFailure(Request request,final IOException e) {
            showErrorMessage(e);
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

        public @Nullable T parse(final Response response) {
            T retObj = null;
            try {
                retObj = mGson.fromJson(response.body().charStream(), mClass);
            } catch (Exception e) {
                showErrorMessage(e);
            }
            return retObj;
        }

        public abstract void callback(@Nullable T parsedObj);
    }
}
