package com.friedrice.cubus.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.friedrice.cubus.R;
import com.friedrice.cubus.adapter.DepartureListAdapter;
import com.friedrice.cubus.util.Bus;
import com.friedrice.cubus.util.GsonFileUtils;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DeparturesActivity extends AppCompatActivity {

    final String tag = "DeparturesActivity";

    final String base_url = "https://developer.cumtd.com/api/v2.2/json/GetDeparturesByStop";
    final String key_param = "key";
    final String stop_param = "stop_id";
    final String time_param = "pt";

    final String getStopUrl = "https://developer.cumtd.com/api/v2.2/json/GetStop";

    final String parent_param="departures";
    final String eta_param="expected_mins";

    RequestQueue queue;
    JsonObjectRequest request;

    String stopName;
    String stopId;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relativeLayout;

    double[] stopLatlon;
    String[] busId;
    String[] etas;
    String[] routeNames;
    String[] colors;

    Bus[] buses;

    Snackbar snackbar;
    ArrayAdapter<String> departureAdapter;
    ListView listView;
    TextView message;
    DepartureListAdapter departureListAdapter;

    private GsonFileUtils gsonFileUtils = new GsonFileUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departures);
        relativeLayout = findViewById(R.id.departuresLayout);
        message = findViewById(R.id.noBusesMessage);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //change title to stop name
        setTitle(actionBar);

        listView = findViewById(R.id.departures);
        departureAdapter = new ArrayAdapter<>(
                this,
                R.layout.list_departures,
                R.id.departuresRoute,
                new ArrayList<String>()
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BusActivity.class);
                intent.putExtra("stopLat", stopLatlon[0]).putExtra("stopLon", stopLatlon[1]);
                intent.putExtra("stopName", stopName).putExtra("stopId", stopId);
                intent.putExtra("bus", buses[position]);
                startActivity(intent);
            }
        });
        refreshData();
    }

    private void setTitle(ActionBar actionBar) {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data == null) {
            stopName = intent.getStringExtra("title");
            stopId = intent.getStringExtra("id");
        } else {
            stopName = data.toString();
            stopId = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
        }
        actionBar.setTitle(stopName);
    }

    private void refreshData() {
        Uri urlUri = Uri.parse(base_url)
                .buildUpon()
                .appendQueryParameter(key_param, getResources().getString(R.string.api_key))
                .appendQueryParameter(stop_param, stopId)
                .appendQueryParameter(time_param, "60") //want up to 60 mins of preview
                .build();
        String url = urlUri.toString();
        Log.d(tag, url);

        queue = Volley.newRequestQueue(this);
        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    clearListAndAdapter();
                    JSONArray departuresArray = response.getJSONArray(parent_param);

                    //TODO: somehow refactor this
                    String[] departureMessages = new String[departuresArray.length()];
                    busId = new String[departuresArray.length()];
                    routeNames = new String[departuresArray.length()];
                    etas = new String[departuresArray.length()];
                    colors = new String[departuresArray.length()];
                    String[] times = new String[departuresArray.length()];
                    String[] istops = new String[departuresArray.length()];
                    //

                    buses = new Bus[departuresArray.length()];

                    for(int i = 0; i < departuresArray.length(); i++) {
                        JSONObject departures = departuresArray.getJSONObject(i);

                        String routeName = departures.getString(getString(R.string.headsign_param));
                        String eta = departures.getString(eta_param);
                        String unit = "Minute";
                        if(Integer.parseInt(eta) > 1 || Integer.parseInt(eta) == 0) {
                            unit = "Minutes";
                        }
                        departureMessages[i] = routeName + " Arrives in " + eta + " " + unit;

                        busId[i] = departures.getString("vehicle_id");
                        routeNames[i] = departures.getString(getString(R.string.headsign_param));
                        etas[i] = eta;

                        JSONObject route = departures.getJSONObject("route");
                        colors[i] = route.getString(getString(R.string.route_color_param));
                        times[i] = departures.getString("expected");
                        istops[i] = departures.getString("is_istop"); //might be a string

                        Bus currentBus = new Bus(busId[i], routeNames[i], etas[i], colors[i]);
                        buses[i] = currentBus;
                    }

                    departureListAdapter = new DepartureListAdapter(
                            getApplicationContext(),
                            new ArrayList<>(Arrays.asList(routeNames)),
                            new ArrayList<>(Arrays.asList(etas)),
                            new ArrayList<>(Arrays.asList(colors)),
                            new ArrayList<>(Arrays.asList(times)),
                            new ArrayList<>(Arrays.asList(istops)),
                            departuresArray.length()
                            );
                    listView.setAdapter(departureListAdapter);
                    departureAdapter.addAll(departureMessages);

                    if(swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if(departuresArray.length() == 0) { //no bus times found
                        listView.setVisibility(View.GONE);
                        message.setVisibility(View.VISIBLE);
                    }
                    else {
                        listView.setVisibility(View.VISIBLE);
                        message.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {e.printStackTrace();}
            }
        }, new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) {}});

        getStopLocation(stopId);
        queue.add(request);

        initializeSwipeRefresh();
    }

    /**
     * Clears the ListView and any adapter associated with it
     */
    private void clearListAndAdapter() {
        ArrayList<String> clear = new ArrayList<>();
        clear.clear();
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_departures,
                android.R.id.text1, clear);
        listView.setAdapter(adapter);
        departureAdapter.clear();
    }

    /**
     * Inflates and initializes the SwipeRefreshLayout for the ListView
     */
    private void initializeSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queue.add(request);
                snackbar = Snackbar.make(relativeLayout, "Times Refreshed", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_departures, menu);
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        Boolean isFavorited = sharedPreferences.getBoolean(stopName + "Favorited", false);
        if (isFavorited) {
            menu.getItem(1).setIcon(R.drawable.ic_favorite_white_24dp);
            menu.getItem(1).getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }
        else {
            menu.getItem(1).getIcon().clearColorFilter();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav:
                onHeartClicked(item);
                break;
            case R.id.stopLocation:
                onStopLocationClicked();
                break;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onHeartClicked(MenuItem item) {
        ArrayList favoritesList = null;

        String filename = "favorites.json";
        FileInputStream inputStream = null;

        try {
            inputStream = openFileInput(filename);
            String json = IOUtils.toString(inputStream, "UTF-8");
            Gson gson = new Gson();
            favoritesList = gson.fromJson(json, ArrayList.class);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(favoritesList == null) return;

        SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        Boolean isFavorited = sharedPreferences.getBoolean(stopName + "Favorited", false);
        if (!isFavorited) { //favoriting
            item.setIcon(R.drawable.ic_favorite_white_24dp);
            item.getIcon().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            favoritesList.add(stopName); //TODO make sure this doesn't break anything
            isFavorited = true;
        } else { //unfavoriting
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            item.getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
            favoritesList.remove(stopName);
            isFavorited = false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(stopName + "Favorited", isFavorited);
        editor.putString(stopName, stopId); //Stores stop id value with stopName key
        editor.apply();

        gsonFileUtils.writeListToFile(favoritesList, this);
    }

    private void onStopLocationClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("busLat", stopLatlon[0]);
        intent.putExtra("busLon", stopLatlon[1]);
        intent.putExtra("map", true);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setRefreshing(true);
        refreshData();
        snackbar = Snackbar.make(relativeLayout, "Times Refreshed", Snackbar.LENGTH_SHORT);
        snackbar.show();
        super.onResume();
    }

    /**
     * inserts values into stopLatlon with stop_id
     * @param stopId
     */
    private void getStopLocation(String stopId) {
        final RequestQueue queue = Volley.newRequestQueue(this);

        Uri stopUrlUri = Uri.parse(getStopUrl)
                .buildUpon()
                .appendQueryParameter(key_param, getResources().getString(R.string.api_key))
                .appendQueryParameter(stop_param, stopId)
                .build();
        String stopUrl = stopUrlUri.toString();

        JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET, stopUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    stopLatlon = new double[2];
                    JSONArray stopsArray = response.getJSONArray("stops");
                    JSONObject stop = stopsArray.getJSONObject(0);
                    JSONArray pointsArray = stop.getJSONArray("stop_points");
                    JSONObject point = pointsArray.getJSONObject(0); //TODO: determine which corner I should use
                    stopLatlon[0] = point.getDouble("stop_lat");
                    stopLatlon[1] = point.getDouble("stop_lon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(locationRequest);
    }

}
