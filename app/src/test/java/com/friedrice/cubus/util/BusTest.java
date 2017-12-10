package com.friedrice.cubus.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by newho on 11/10/2016.
 */
public class BusTest {
    @Test
    public void testConstructor() {
        Bus bus = new Bus("busId", "routeName", "eta", "color");
        assertEquals("busId", bus.busId);
        assertEquals("routeName", bus.routeName);
        assertEquals("eta", bus.eta);
        assertEquals("color", bus.color);

    }
}