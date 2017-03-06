package com.desmond.myweatherapp.callback;

/**
 * Created by neverknows on 07-Mar-17.
 */

public interface MyLocationCallback {
    void onLocationUpdated(double longitude, double latitude);
    void onLocationResultFailed();
}
