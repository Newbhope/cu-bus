package com.friedrice.cubus.util;

import java.io.Serializable;

/**
 * Created by newho on 11/10/2016.
 */

public class Bus implements Serializable {

    //bus specific variables
    public String busId;
    public String eta;

    //route variables
    public String routeName;
    public String color;


    public Bus(String busId, String routeName, String eta, String color) {
        this.busId = busId;
        this.eta = eta;
        this.routeName = routeName;
        this.color = color;
    }
}
