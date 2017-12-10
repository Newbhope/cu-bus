package com.friedrice.cubus.util;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.friedrice.cubus.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by newho on 11/9/2016.
 */

public class MapUtils {

    /**
     * @param mMap GoogleMap to draw the polyline on
     * @param shapeId Shape ID to draw
     * @param activity Calling Activity
     * @param routeLines List to store created polyline in.
     * @param colorString Color to draw polyline in "rrggbb" format. AccentColor is used if null
     */
    public void drawRouteShape(@NonNull final GoogleMap mMap, @NonNull String shapeId,
                               @NonNull final Activity activity, final List<Polyline> routeLines,
                               String colorString) {
        Log.d("MapUtils", ""+shapeId);

        final int routeColor;
        if(colorString != null) {
            routeColor = Color.parseColor("#" + colorString);
        }
        else {
            routeColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.colorAccent);
        }
        final RequestQueue queue = Volley.newRequestQueue(activity);
        Uri shapeUrlUri = Uri.parse("https://developer.cumtd.com/api/v2.2/json/GetShape")
                .buildUpon()
                .appendQueryParameter("key", activity.getString(R.string.api_key))
                .appendQueryParameter("shape_id", shapeId)
                .build();
        String shapeUrl = shapeUrlUri.toString();

        JsonObjectRequest shapeRequest = new JsonObjectRequest(Request.Method.GET, shapeUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray shapes = response.getJSONArray("shapes");
                    ArrayList<LatLng> pointsList = new ArrayList<>();
                    for(int i = 0; i < shapes.length(); i++) { //constructs polyline out of json response
                        JSONObject point = shapes.getJSONObject(i);
                        double pointLat = point.getDouble("shape_pt_lat");
                        double pointLon = point.getDouble("shape_pt_lon");
                        LatLng pointLatLng = new LatLng(pointLat, pointLon);
                        pointsList.add(pointLatLng);
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(pointsList)
                            .width(10)
                            .color(routeColor));
                    if(routeLines != null) {
                        routeLines.add(polyline);
                    }

                } catch (JSONException e) {e.printStackTrace();}
            }
        }, new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) {}});
        queue.add(shapeRequest);
    }

    public String getShapeIdFromRouteId(String routeId) {
        switch(routeId) {
            case "YELLOW":
                return "[@14.0.56288340@]23";
            case "GREEN":
                return "[@15.0.66064083@]71";
            case "TEAL":
                return "12W TEAL 12";
            case "SILVER":
                return "130N SILVER EVENING 1";
            case "ILLINI":
                return "22S ILLINI 20";
            default:
                return null;
        }
    }

    public String getColorFromRouteId(String routeId) {
        switch(routeId) {
            case "YELLOW":
                return "fcee1f";
            case "GREEN":
                return "008063";
            case "TEAL":
                return "006991";
            case "SILVER":
                return "cccccc";
            case "ILLINI":
                return "5a1d5a";
            default:
                return null;
        }
    }
}


