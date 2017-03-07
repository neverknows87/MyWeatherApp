package com.desmond.myweatherapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.desmond.myweatherapp.callback.MyLocationCallback;
import com.desmond.myweatherapp.fragment.CurrentlyFragment;
import com.desmond.myweatherapp.fragment.HourlyFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.desmond.myweatherapp.constant.CommonConstant.REQUEST_LOCATION_SETTINGS;
import static com.desmond.myweatherapp.constant.CommonConstant.SECRET_KEY;
import static com.desmond.myweatherapp.constant.CommonConstant.TAG;

public class MainActivity extends AppCompatActivity implements MyLocationCallback {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog progressDialog;
    public WeatherResponse weatherResponses;
    private MyLocation myLocation;
    private GoogleApiClient mGoogleApiClient;

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 123;
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initForecastApi();
        initGoogleClientApi();

        myLocation = new MyLocation(this, mGoogleApiClient);
        myLocation.setLocationListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initForecastApi() {
        ForecastApi.create(SECRET_KEY);
    }

    private void initGoogleClientApi() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();

        }
    }

    private void makeRequest(double longitude, double latitude) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.show();
        RequestBuilder weather = new RequestBuilder();

        Request request = new Request();
        request.setLat(Double.toString(latitude));
        request.setLng(Double.toString(longitude));
        request.setUnits(Request.Units.SI);
        request.setLanguage(Request.Language.ENGLISH);
        request.addExcludeBlock(Request.Block.DAILY);
        request.addExcludeBlock(Request.Block.MINUTELY);

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                progressDialog.dismiss();
                Log.d(TAG, "response_url: " + response.getUrl());
                Log.d(TAG, "response_result: " + weatherResponse.getCurrently().getSummary());
                weatherResponses = weatherResponse;
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(new Intent("event-notification"));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, "Error while calling: " + retrofitError.getUrl());
                Toast.makeText(getApplicationContext(), R.string.error_while_loading_result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentlyFragment(), "CURRENT");
        adapter.addFragment(new HourlyFragment(), "HOURLY");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("permission", "request at main activity");
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            Log.d("Location", "location request");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Location", "location request granted");
                myLocation.settingsRequest();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("MainActivity", "on activity result");
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_LOCATION_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        myLocation.getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        myLocation.settingsRequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationUpdated(double longitude, double latitude) {
        makeRequest(longitude, latitude);
    }

    @Override
    public void onLocationResultFailed() {
        Toast.makeText(getApplicationContext(), R.string.unable_to_retrieve_your_location, Toast.LENGTH_SHORT).show();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
