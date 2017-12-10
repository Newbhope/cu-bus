package com.friedrice.cubus.util;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by newho on 12/7/2016.
 */

public class GsonFileUtils {

    //this method doesn't save many lines of code...
    public String getJsonStringFromFile(String filename, Activity currentActivity) throws IOException {
        FileInputStream inputStream;
        inputStream = currentActivity.openFileInput(filename);
        String jsonString = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
        return jsonString;
    }

    public void writeListToFile(ArrayList changedList, Activity currentActivity) {
        FileOutputStream outputStream ;
        try {
            outputStream = currentActivity.openFileOutput("favorites.json", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(changedList);
        try {
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
