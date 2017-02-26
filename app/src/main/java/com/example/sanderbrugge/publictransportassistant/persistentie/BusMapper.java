package com.example.sanderbrugge.publictransportassistant.persistentie;

import com.example.sanderbrugge.publictransportassistant.model.Stop;

import java.util.ArrayList;

public class VehicleMapper {
    private static final String TAG = "Stopmapper";
    private ArrayList<Stop> stops;

    public VehicleMapper() {
        stops = new ArrayList<>();
        initStops();
    }

    private void initStops() {
        stops.add(new Stop("", 1, 1, 1, "testhalte", "testLocatie", "70"));
        stops.add(new Stop("", 2, 2, 1, "testhalte", "testLocatie", "70"));
        stops.add(new Stop("", 3, 3, 1, "testhalte", "testLocatie", "70"));
        stops.add(new Stop("", 4, 4, 1, "testhalte", "testLocatie", "70"));
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }
}
