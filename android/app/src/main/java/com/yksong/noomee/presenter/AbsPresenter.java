package com.yksong.noomee.presenter;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yksong.noomee.network.HttpCallBack;

import java.io.IOException;

/**
 * Created by esong on 2015-01-11.
 */
public abstract class AbsPresenter<T extends View> {
    private static final String sError =  "Network Connection Error: ";
    private T mView;

    public void setView(T view) {
        mView = view;
    }

    protected T getView() {
        return mView;
    }

    public abstract class PresenterCallBack<T> extends HttpCallBack<T> {
        public PresenterCallBack(Class<T> clazz) {
            super(clazz);
        }

        public void showErrorMessage(final Exception e){
            mView.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getView().getContext(), sError + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        public void callback(final T parseObj) {
            mView.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    UiCallBack(parseObj);
                }
            });
        }

        public abstract void UiCallBack(T parsedObj);
    }
}
