package com.yksong.noomee.util;

import com.yksong.noomee.model.ChiTag;
import com.yksong.noomee.model.Restaurant;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by esong on 2015-02-10.
 */
public interface NoomeeAPI {
    @GET("/random?term=restaurant")
    void randomRestaurant(@Query("lati") double lati, @Query("longi")double longi,
                          @Query("tags") String[] tags,
                          Callback<Restaurant> cb);

    @GET("/categories?term=restaurant")
    void getTags(@Query("lati") double lati, @Query("longi") double longi,
                 Callback<ChiTag[]> cb);
}
