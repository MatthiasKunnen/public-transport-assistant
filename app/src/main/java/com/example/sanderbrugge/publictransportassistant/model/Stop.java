package com.example.sanderbrugge.publictransportassistant.model;

public class Stop {
    //sequentie, busnr, haltenaam, locatie, arrived_on, stopId
    private int sequence;
    private String stopName, stopPlace;
    private String arrived_on;
    private int subscriptionId;
    private String routeName;

    public Stop(String arrived_on, int sequence, int subscriptionId, int vehicleId, String stopName, String stopPlace, String routeName) {
        this.sequence = sequence;
        this.stopName = stopName;
        this.stopPlace = stopPlace;
        this.arrived_on = arrived_on;
        this.subscriptionId = subscriptionId;
        this.routeName = routeName;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopPlace() {
        return stopPlace;
    }

    public void setStopPlace(String stopPlace) {
        this.stopPlace = stopPlace;
    }

    public String getArrived_on() {
        return arrived_on;
    }

    public void setArrived_on(String arrived_on) {
        this.arrived_on = arrived_on;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
