package com.yksong.noomee.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by esong on 2015-01-13.
 */
public class GeoProvider {
    private static final GeoProvider sInstance = new GeoProvider();
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 20;
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 5000;

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

        NoomeeLocationListener listener = new NoomeeLocationListener();

        List<String> providers = mLocationManager.getAllProviders();

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, listener);
        }

        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, listener);
        }
    }

    public Location getLocation(){
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet =
                mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    public LatLng getLatLng() {
        Location location = getLocation();

        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }

        return null;
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
