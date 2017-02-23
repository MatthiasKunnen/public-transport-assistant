package com.example.sanderbrugge.publictransportassistant.model;

import java.util.ArrayList;

/**
 * Created by sanderbrugge on 23/02/17.
 */

public class Data {
    private ArrayList<Stop> stops;

    public Data(ArrayList<Stop> stops) {
        this.stops = stops;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Stop> stops) {
        this.stops = stops;
    }
}
