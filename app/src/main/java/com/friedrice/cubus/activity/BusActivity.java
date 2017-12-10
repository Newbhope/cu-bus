package com.friedrice.cubus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.friedrice.cubus.R;
import com.friedrice.cubus.util.Bus;
import com.friedrice.cubus.util.MapUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusActivity extends AppCompatActivity implements OnMapReadyCallback {

    Bundle extras;
    String routeName;
    String busId;
    double busLat = 0;
    double busLon = 0;
    double stopLat = 0;
    double stopLon = 0;
    float direction;
    String shapeId;
    String stopName;
    String eta;
    String color;

    FrameLayout layout;
    Snackbar snackbar;

    private GoogleMap mMap;
    MapUtils mapUtils = new MapUtils();
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus);
        layout = (FrameLayout) findViewById(R.id.busActivityFrame);

        initializeBusValues();
        setupActionBar();
        refreshData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.busMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    public void initializeBusValues() {
        Intent intent = getIntent();
        extras = intent.getExtras();
        stopLat = extras.getDouble("stopLat");
        stopLon = extras.getDouble("stopLon");
        stopName = extras.getString("stopName");

        Bus bus = (Bus) extras.get("bus");
        if(bus != null) {
            busId = bus.busId;
            routeName = bus.routeName;
            eta = bus.eta;
            color = bus.color;
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(routeName);
        }
    }

    /**
     * Refreshes the bus's location, heading, and route on the map.
     */
    private void refreshData() {
        Uri urlUri = Uri.parse("https://developer.cumtd.com/api/v2.2/json/GetVehicle")
                .buildUpon()
                .appendQueryParameter("key", getResources().getString(R.string.api_key))
                .appendQueryParameter("vehicle_id", busId)
                .build();
        String url = urlUri.toString();

        final RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray vehiclesArray = response.getJSONArray("vehicles");
                    JSONObject vehicle = vehiclesArray.getJSONObject(0); //0 is the only vehicle

                    JSONObject trip = vehicle.getJSONObject("trip");
                    shapeId = trip.getString("shape_id");

                    mapUtils.drawRouteShape(mMap, shapeId, activity, null, color);
                    drawBusMarker(vehicle, trip);
                    createBusSnackBar();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(busLat, busLon), 16));
                    drawStopMarker();

                } catch (JSONException e) {e.printStackTrace();}
            }
        }, new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) {}});
        queue.add(request);
    }

    /**
     * Draws an arrow representing the bus. The arrow heading depends on the heading obtained
     * in the cumtd api call.
     */
    private void drawBusMarker(JSONObject vehicle, JSONObject trip) throws JSONException {
        switch(trip.getString("direction")){
            case "North":
                direction = 0;
                break;
            case "East":
                direction = 90;
                break;
            case "South":
                direction = 180;
                break;
            case "West":
                direction = 270;
                break;
            default:
                direction = 0;
        }

        JSONObject location = vehicle.getJSONObject("location");
        busLat = location.getDouble("lat");
        busLon = location.getDouble("lon");

        BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource(R.drawable.arrowbig);
        mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(arrow)
                .bearing(direction)
                .position(new LatLng(busLat, busLon), 100));
    }

    /**
     * Creates a SnackBar at the bottom of the screen that displays bus status
     */
    private void createBusSnackBar() {
        String unit = "Minute";
        if(Integer.parseInt(eta) > 1 || Integer.parseInt(eta) == 0) {
            unit = "Minutes";
        }
        snackbar = Snackbar.make(layout,
                routeName + " Arrives in " + eta + " " + unit,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    /**
     * Places a marker that represents the stop that the user was looking at at the previous
     * screen.
     *
     * May display all stops on this route at a future time
     */
    private void drawStopMarker() {
        Marker stopMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(stopLat, stopLon))
                .title(stopName));
        stopMarker.setTag(extras.getString("stopId"));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getBaseContext(), DeparturesActivity.class);
                intent.putExtra("title", marker.getTitle());
                intent.putExtra("id", (String) marker.getTag());

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.refresh_bus:
                if(mMap != null) mMap.clear();
                if(snackbar != null) snackbar.dismiss();
                refreshData();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }
}
