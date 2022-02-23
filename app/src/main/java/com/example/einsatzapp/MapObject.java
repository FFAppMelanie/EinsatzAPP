package com.example.einsatzapp;

public class MapObject {
    private final String name;
    private final double lat;
    private final double lon;
    private final int idOfSymbol;

    public MapObject(String name, double lat, double lon, int idOfSymbol) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.idOfSymbol = idOfSymbol;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getIdOfSymbol() {
        return idOfSymbol;
    }
}
