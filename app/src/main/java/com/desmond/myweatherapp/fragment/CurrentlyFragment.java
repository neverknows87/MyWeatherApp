package com.desmond.myweatherapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.desmond.myweatherapp.MainActivity;
import com.desmond.myweatherapp.R;
import com.johnhiott.darkskyandroidlib.models.DataPoint;

/**
 * Created by neverknows on 06-Mar-17.
 */

public class CurrentlyFragment extends Fragment {
    private ImageView weatherIcon;
    private  TextView area;
    private TextView temperature;
    private TextView summary;
    private TextView pressure;
    private TextView humidity;
    private TextView windSpeed;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateViews();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("event-notification"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.currently_fragment_layout, container, false);
        weatherIcon = (ImageView)view.findViewById(R.id.imageView);
        area = (TextView)view.findViewById(R.id.area);
        temperature = (TextView) view.findViewById(R.id.temperature);
        summary = (TextView) view.findViewById(R.id.summary);
        pressure = (TextView) view.findViewById(R.id.pressure);
        humidity = (TextView) view.findViewById(R.id.humidity);
        windSpeed = (TextView) view.findViewById(R.id.windspeed);
        return view;
    }

    @UiThread
    public void updateViews() {
        DataPoint currentlyData = ((MainActivity)getActivity()).weatherResponses.getCurrently();
        String filename = currentlyData.getIcon().replace("-", "_");
        weatherIcon.setImageResource(getResources().getIdentifier(filename, "drawable", getContext().getPackageName()));
        area.setText(((MainActivity)getActivity()).weatherResponses.getTimezone());
        temperature.setText(getString(R.string.degree_celcius, currentlyData.getTemperature()));
        summary.setText(currentlyData.getSummary());
        pressure.setText(getString(R.string.pressure, currentlyData.getPressure()));
        humidity.setText(getString(R.string.humidity, currentlyData.getHumidity()));
        windSpeed.setText(getString(R.string.wind_speed, currentlyData.getWindSpeed()));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }
}
