package com.friedrice.cubus.util;

import android.app.Activity;

import org.junit.Test;
import org.mockito.Mock;

/**
 * Created by newho on 12/7/2016.
 */
public class GsonFileUtilsTest {
    private GsonFileUtils gsonFileUtils = new GsonFileUtils();
    @Mock
    Activity activity;

    //can't mock InputStream
    @Test
    public void getJsonStringFromFile() throws Exception {
//        String result = gsonFileUtils.getJsonStringFromFile("favorites.json", activity);
//        System.out.println(result);
    }

    @Test
    public void writeListToFile() throws Exception {

    }

}