package com.yksong.noomee.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by esong on 2015-01-13.
 */
public class GeoProvider {
    private static final GeoProvider sInstance = new GeoProvider();
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 2;
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 60000;

    private LocationManager mLocationManager;

    public static void initialize(Context appContext) {
        sInstance.init(appContext);
    }

    public static GeoProvider getInstance() {
        return sInstance;
    }

    public void init(Context appContext) {
        mLocationManager = (LocationManager)
                appContext.getSystemService(Context.LOCATION_SERVICE);

        mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,
                new NoomeeLocationListener(), null);
    }

    public Location getLocation(){
        return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public boolean hasUpdate(Location location) {
        return false;
    }

    private class NoomeeLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
