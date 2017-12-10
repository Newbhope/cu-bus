package com.friedrice.cubus.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.friedrice.cubus.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by newho on 8/20/2016.
 */
public class DepartureListAdapter extends BaseAdapter {

    ArrayList<String> buses;
    ArrayList<String> etas; //eta in minutes sex: 5 min
    ArrayList<String> colors;
    ArrayList<String> times; //12:00:00 time, military time
    ArrayList<String> istops;
    Context context;

    LayoutInflater mInflater;
    DeparturesViewHolder mViewHolder;

    int arrayLength;

    public DepartureListAdapter(Context c, ArrayList<String> routeNames,
                                ArrayList<String> etaTimes, ArrayList<String> routeColors,
                                ArrayList<String> arrivalTimes, ArrayList<String> isIstops,
                                int size) {
        buses = routeNames;
        etas = etaTimes;
        colors = routeColors;
        times = arrivalTimes;
        istops = isIstops;
        context = c;
        arrayLength = size;

        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return buses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = mInflater.inflate(R.layout.list_departures, parent, false);

        initializeViewHolder(view);

        String hexColor = "#" + colors.get(position);
        mViewHolder.departuresCircle.setColorFilter(Color.parseColor(hexColor));
        mViewHolder.departuresRoute.setText(buses.get(position));

        displayArrivalTime(position);

        if (istops.get(position).equals("true")) {
            mViewHolder.departuresIstop.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.departuresIstop.setVisibility(View.INVISIBLE);
        }

        String eta = etas.get(position);
        mViewHolder.departuresEta.setText(eta);

        if (Integer.parseInt(eta) == 1) {
            mViewHolder.departuresUnit.setText(R.string.singular_minute);
        }

        return view;
    }

    /**
     * @param position departure's position in the list
     * Parses time from "2013-03-28T11:23:00-05:00" format to "h:mm:ss"
     */
    private void displayArrivalTime(int position) {
        String parsed;
        String unparsed = times.get(position);
        int pos = unparsed.indexOf('T');
        String kindaParsed = unparsed.substring(pos + 1); //string manipulation is fun!
        int pos2 = kindaParsed.indexOf('-');
        String almostParsed = kindaParsed.substring(0, pos2);
        if(almostParsed.charAt(0) == '0') { //single digit time, 01, 08, etc.
            parsed = almostParsed.substring(1);
        }
        else {
            parsed = almostParsed;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm:ss");  //converts 24 hour clock to 12 hour
        try {
            Date dateObj = simpleDateFormat.parse(parsed);
            parsed = simpleDateFormat.format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mViewHolder.departuresTime.setText("Arrives at " + parsed);
    }


    /**
     * @param view Layout list view item for the departure
     * Initializes every view object from the view in the view holder to allow faster look up
     */
    private void initializeViewHolder(View view) {
        mViewHolder = new DeparturesViewHolder();
        mViewHolder.departuresCircle = (ImageView) view.findViewById(R.id.departuresColor);
        mViewHolder.departuresRoute = (TextView) view.findViewById(R.id.departuresRoute);
        mViewHolder.departuresTime = (TextView) view.findViewById(R.id.departuresTime);
        mViewHolder.departuresIstop = (ImageView) view.findViewById(R.id.departuresIstop);
        mViewHolder.departuresEta = (TextView) view.findViewById(R.id.departuresEta);
        mViewHolder.departuresUnit = (TextView) view.findViewById(R.id.departuresUnit);
    }

    private static class DeparturesViewHolder {
        ImageView departuresCircle;
        TextView departuresRoute;
        TextView departuresTime;
        ImageView departuresIstop;
        TextView departuresEta;
        TextView departuresUnit;
    }
}
