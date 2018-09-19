package com.mini_mo.viewpager.Cluster;

import android.widget.TextView;

public class MarkerItem {
    String text;
    double lat;
    double lon;

    public MarkerItem(double lat, double lon, String text){
        this.text = text;
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }


    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }
}
