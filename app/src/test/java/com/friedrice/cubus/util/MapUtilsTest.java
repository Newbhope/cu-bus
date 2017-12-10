package com.friedrice.cubus.util;

import com.friedrice.cubus.activity.BusActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

/**
 * Created by newho on 11/9/2016.
 */
public class MapUtilsTest {
    private MapUtils mapUtils = new MapUtils();

    ArrayList<Polyline> list = new ArrayList<>();
    @Mock
    GoogleMap googleMap;
    @Mock
    BusActivity busActivity;


    @Test
    public void drawRouteShape() throws Exception {
//        mapUtils.drawRouteShape(googleMap, "[@14.0.56288340@]23", busActivity,
//                list, "ffffff");
    }

    @Test
    public void getShapeIdFromRouteIdYellow() throws Exception {
        String shapeId = mapUtils.getShapeIdFromRouteId("YELLOW");
        assertEquals("[@14.0.56288340@]23", shapeId);
    }
    @Test
    public void getShapeIdFromRouteIdGreen() throws Exception {
        String shapeId = mapUtils.getShapeIdFromRouteId("GREEN");
        assertEquals("[@15.0.66064083@]71", shapeId);
    }
    @Test
    public void getShapeIdFromRouteIdTeal() throws Exception {
        String shapeId = mapUtils.getShapeIdFromRouteId("TEAL");
        assertEquals("12W TEAL 12", shapeId);
    }
    @Test
    public void getShapeIdFromRouteIdInvalid() throws Exception {
        String shapeId = mapUtils.getShapeIdFromRouteId("hi");
        assertEquals(null, shapeId);
    }

    @Test
    public void getColorFromRouteIdSilver() throws Exception {
        String color = mapUtils.getColorFromRouteId("SILVER");
        assertEquals("cccccc", color);
    }
    @Test
    public void getColorFromRouteIdIllini() throws Exception {
        String color = mapUtils.getColorFromRouteId("ILLINI");
        assertEquals("5a1d5a", color);
    }
    @Test
    public void getColorFromRouteIdTeal() throws Exception {
        String color = mapUtils.getColorFromRouteId("TEAL");
        assertEquals("006991", color);
    }
    @Test
    public void getColorFromRouteIdInvalid() throws Exception {
        String color = mapUtils.getColorFromRouteId("blurple");
        assertEquals(null, color);
    }
}