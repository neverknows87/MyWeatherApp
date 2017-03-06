package com.desmond.myweatherapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.desmond.myweatherapp.ListViewAdapter;
import com.desmond.myweatherapp.MainActivity;
import com.desmond.myweatherapp.R;
import com.johnhiott.darkskyandroidlib.models.DataBlock;
import com.johnhiott.darkskyandroidlib.models.DataPoint;

import java.util.List;

/**
 * Created by neverknows on 06-Mar-17.
 */

public class HourlyFragment extends Fragment{
    private ListView listView;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DataBlock hourlyData = ((MainActivity)getActivity()).weatherResponses.getHourly();
            updateViews(hourlyData.getData(), ((MainActivity)getActivity()).weatherResponses.getOffset());
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
        View view = inflater.inflate(R.layout.hourly_fragment_layout, container, false);
        listView = (ListView)view.findViewById(R.id.listView);
        return view;
    }

    private void updateViews(List<DataPoint> dataPoints, String offset) {
        ListViewAdapter adapter = new ListViewAdapter(getContext(), dataPoints, offset);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }
}
