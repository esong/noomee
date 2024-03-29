package com.yksong.noomee.presenter;

import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.yksong.noomee.BuildConfig;

import retrofit.Callback;
import retrofit.RetrofitError;

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

    public abstract class PresenterCallBack<T> implements Callback<T> {
        public void failure(final RetrofitError error) {
            if (!BuildConfig.DEBUG) {
                Crashlytics.logException(error.getCause());
            } else {
                if (mView != null) {
                    mView.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getView().getContext(), sError + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }
}
