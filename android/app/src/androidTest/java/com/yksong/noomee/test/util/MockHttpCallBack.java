package com.yksong.noomee.test.util;

import android.support.annotation.Nullable;

import com.yksong.noomee.network.HttpCallBack;
import com.yksong.noomee.presenter.AbsPresenter;

/**
 * Created by esong on 2015-02-07.
 */
public class MockHttpCallBack<T> extends HttpCallBack<T> {
    public MockHttpCallBack(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public void callback(@Nullable T parsedObj) {

    }
}
