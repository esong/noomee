package com.yksong.noomee.test;

import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.yksong.noomee.MainActivity;
import com.yksong.noomee.R;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.test.util.MockHttpCallBack;
import com.yksong.noomee.test.util.TestUtils;

/**
 * Created by esong on 2015-02-06.
 */
public class DeciderPresenterTest extends ActivityUnitTestCase<MainActivity> {

    public DeciderPresenterTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp(){

    }

    @MediumTest
    public void testRestaurantParsing() {
        MockHttpCallBack<Restaurant> mockCallBack = new MockHttpCallBack<>(Restaurant.class);

        mockCallBack.parse(TestUtils.createResponse(getActivity(),
                R.raw.test_search_restaurant_regular));
    }
}
