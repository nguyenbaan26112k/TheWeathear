package com.example.theweathear.model;

import java.io.Serializable;

public class City implements Serializable {
    String Name;
    String lat;
    String lon;

    public City() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public City(String name, String lat, String lon) {
        Name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "City{" +
                "Name='" + Name + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                '}';
    }
}
