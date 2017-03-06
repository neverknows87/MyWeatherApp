package com.desmond.myweatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.johnhiott.darkskyandroidlib.models.DataPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by neverknows on 07-Mar-17.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<DataPoint> data;
    private String offset;

    public ListViewAdapter(Context context, List<DataPoint>data, String offset) {
        this.context = context;
        this.data = data;
        this.offset = offset;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public DataPoint getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.imageView);
            holder.summary = (TextView)convertView.findViewById(R.id.summary);
            holder.temperature = (TextView) convertView.findViewById(R.id.temperature);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        DataPoint dataPoint = getItem(i);
        holder.summary.setText(dataPoint.getSummary());
        holder.temperature.setText(context.getString(R.string.degree_celcius, dataPoint.getTemperature()));
        String filename = dataPoint.getIcon().replace("-", "_");
        holder.image.setImageResource(context.getResources().getIdentifier(filename, "drawable", context.getPackageName()));
        holder.time.setText(getTime(dataPoint.getTime()));
        return convertView;
    }

    private String getTime(long milliSeconds) {
        long exactTimeInMillis = milliSeconds + (Integer.parseInt(offset) * 60 * 60 * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(exactTimeInMillis);
        return formatter.format(calendar.getTime());
    }


    class ViewHolder {
        TextView summary;
        TextView time;
        TextView temperature;
        ImageView image;
    }
}
