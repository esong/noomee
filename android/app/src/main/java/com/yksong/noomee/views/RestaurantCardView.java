package com.yksong.noomee.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import com.yksong.noomee.model.Restaurant;

/**
 * Created by esong on 2015-02-07.
 */
public class RestaurantCardView extends CardView {
    private Restaurant mRestaurant;

    public RestaurantCardView(Context context) {
        this(context, null);
    }

    public RestaurantCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RestaurantCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }
}
