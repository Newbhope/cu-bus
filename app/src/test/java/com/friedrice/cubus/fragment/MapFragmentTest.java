package com.friedrice.cubus.fragment;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by newho on 11/3/2016.
 */
public class MapFragmentTest {

    /**
     * Tests MapFragment creation
     */
    @Test
    public void newInstance() throws Exception {
        MapFragment mapFragment = MapFragment.newInstance(10.0, 15.0, false);
        assertThat(mapFragment.stopLat, is(10.0));
        assertThat(mapFragment.stopLon, is(15.0));
        assertThat(mapFragment.fromDepartures, is(false));
        assertThat(mapFragment.launchNum, is(0));
    }

    @Test
    public void onCreateView() throws Exception {

    }

    @Test
    public void onAttach() throws Exception {

    }

    @Test
    public void onDetach() throws Exception {

    }

    @Test
    public void onMapReady() throws Exception {
        
    }

    @Test
    public void onRequestPermissionsResult() throws Exception {

    }

}