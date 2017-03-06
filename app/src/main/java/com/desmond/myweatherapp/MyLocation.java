package com.desmond.myweatherapp;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.desmond.myweatherapp.callback.MyLocationCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static com.desmond.myweatherapp.constant.CommonConstant.*;

/**
 * Created by neverknows on 07-Mar-17.
 */

public class MyLocation implements LocationListener {
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest locationRequest;
    private Location mLastLocation;
    private MyLocationCallback listener;

    public MyLocation(Context context, GoogleApiClient googleApiClient) {
        mContext = context;
        mGoogleApiClient = googleApiClient;
        checkPermission();
    }

    public void setLocationListener(MyLocationCallback listener) {
        this.listener = listener;
    }

    public void settingsRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("here", "success");
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d("here", "required");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    (MainActivity)mContext, REQUEST_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            listener.onLocationResultFailed();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d("here", "unavailable");
                        listener.onLocationResultFailed();
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    private void checkPermission() {
        if (!isPermissionGranted()) {
            Log.d("Location", "request permisson");
            ActivityCompat.requestPermissions((MainActivity)mContext,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        } else {
            Log.d("Location", "permission granted");
            settingsRequest();
        }
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location", "request permission");
            ActivityCompat.requestPermissions((MainActivity)mContext,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("Location", "last location get");
            Log.d("Location",String.valueOf(mLastLocation.getLatitude()));
            Log.d("Location",String.valueOf(mLastLocation.getLongitude()));
            listener.onLocationUpdated(mLastLocation.getLongitude(), mLastLocation.getLatitude());
        } else {
            Log.d("Location", "request update");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location", "location updated");
        Log.d("Location",String.valueOf(location.getLatitude()));
        Log.d("Location",String.valueOf(location.getLongitude()));
        listener.onLocationUpdated(location.getLongitude(), location.getLatitude());
    }
}
