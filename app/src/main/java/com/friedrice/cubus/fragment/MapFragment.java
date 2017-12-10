package com.friedrice.cubus.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.friedrice.cubus.R;
import com.friedrice.cubus.activity.DeparturesActivity;
import com.friedrice.cubus.util.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    final String parent_param = "stops";
    final String id_param = "stop_id";
    final String name_param = "stop_name";

    private GoogleMap mMap;
    JSONObject stops; //Obtained 8/15/2016. Update accordingly

    static double stopLat;
    static double stopLon;
    static Boolean fromDepartures = false;
    int launchNum;

    private Menu menu;
    List<Polyline> routeLines = new ArrayList<>();
    MapUtils mapUtils = new MapUtils();
    List<Marker> markerList = new ArrayList<>();

    /**
     * @param lat  Starting latitude for the map to initialize to if from is true
     * @param lon  Starting longitude for the map to initialize to if from is true
     * @param from True if map default location should be different than the union
     * @return MapFragment containing stops.
     */
    public static MapFragment newInstance(double lat, double lon, Boolean from) {
        MapFragment fragment = new MapFragment();
        stopLat = lat;
        stopLon = lon;
        fromDepartures = from;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        updateLaunchNum();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        initializeStops();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map_fragment, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean isChecked = !item.isChecked();
        item.setChecked(isChecked);
        if(id == R.id.hide_markers) {
            handleMarkers(isChecked);
        }
        updateRoutes();
        return super.onOptionsItemSelected(item);
    }

    private void handleMarkers(boolean isChecked) {
        if(isChecked) {
            for(Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
        else {
            addStopsToMap();
        }
    }


    /**
     * Draws each checked route and removes every other one.
     * Called on every checkable item click.
     */
    private void updateRoutes() {
        for(Polyline polyline : routeLines) {
            polyline.remove();
        }
        Menu routesMenu = menu.findItem(R.id.filter).getSubMenu();
        for(int i = 0; i < routesMenu.size() - 1; i++) {
            MenuItem item = routesMenu.getItem(i);
            if (item.isChecked()) {
                String routeId = item.getTitle().toString();
                routeId = routeId.split(" ")[1];
                routeId = routeId.toUpperCase();
                String routeColor = mapUtils.getColorFromRouteId(routeId);
                String shapeId = mapUtils.getShapeIdFromRouteId(routeId);
                mapUtils.drawRouteShape(mMap, shapeId, getActivity(), routeLines, routeColor);
            }
        }
    }

    private void updateLaunchNum() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        launchNum = sharedPreferences.getInt("launchNum", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("launchNum", launchNum + 1);
        editor.apply();
    }

    /**
     * Parses bus_stops.json and initializes the stops JSONObject
     */
    private void initializeStops() {
        InputStream is = getResources().openRawResource(R.raw.bus_stops);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String treesString = writer.toString();

        try {
            stops = new JSONObject(treesString); //initialize trees jsonobject
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        if (launchNum == 0){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        if (fromDepartures) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(stopLat, stopLon), 18));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.109201, -88.227179), 16));
        }

        addStopsToMap();
    }

    private void addStopsToMap() {
        try {
            JSONArray stopsArray = stops.getJSONArray(parent_param);
            for(int i = 0; i < stopsArray.length(); i++) {
                JSONObject stop = stopsArray.getJSONObject(i);
                String name = stop.getString(name_param);
                String stopId = stop.getString(id_param);

                JSONArray pointsArray = stop.getJSONArray("stop_points");
                JSONObject point = pointsArray.getJSONObject(0); //TODO: determine which corner i should use
                double lat = point.getDouble("stop_lat");
                double lon = point.getDouble("stop_lon");
                LatLng stopLocation = new LatLng(lat, lon);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(stopLocation)
                        .title(name));

                marker.setTag(stopId);
                markerList.add(marker);

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getContext(), DeparturesActivity.class);
                        intent.putExtra("title", marker.getTitle());
                        intent.putExtra("id", (String) marker.getTag());
                        startActivity(intent);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) { //0 is requestCode for location
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                if(mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }
}
