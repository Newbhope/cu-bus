package com.friedrice.cubus.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.friedrice.cubus.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by newho on 8/16/2016.
 */
public class SuggestionsProvider extends ContentProvider {

    private static final String TAG = "SuggestionsProvider";
    String [][] suggestions;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String query = selectionArgs[0];

        if(query.length() > 0) { //text present in SearchView
            final String base_url = "https://search.mtd.org/v1.0.0/stop/suggest/";
            final String query_param = "query";

            Uri urlUri = Uri.parse(base_url)
                    .buildUpon()
                    .appendPath(query)
                    .build();
            String url = urlUri.toString();

            RequestQueue queue = Volley.newRequestQueue(getContext());
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    final String result_param = "result";
                    final String id_param = "id";
                    final String name_param = "name";

                    try {
                        suggestions = new String[response.length()][2]; //array to hold stop name and stop id

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject stop = response.getJSONObject(i);
                            JSONObject result = stop.getJSONObject(result_param);
                            String id = result.getString(id_param);
                            String name = result.getString(name_param);
                            suggestions[i][0] = name;
                            suggestions[i][1] = id;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("bruh", "mang");
                }
            });

            queue.add(request);

            String[] columns = {
                    BaseColumns._ID,
                    SearchManager.SUGGEST_COLUMN_TEXT_1,
                    SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                    SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA};

            MatrixCursor cursor = new MatrixCursor(columns);

            for (int i = 0; i < suggestions.length; i++) {
                String[] suggestion = {Integer.toString(i), suggestions[i][0], suggestions[i][0], suggestions[i][1]};
                cursor.addRow(suggestion);
            }
            return cursor;
        }
        else{
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

}
