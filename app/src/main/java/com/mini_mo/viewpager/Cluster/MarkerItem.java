package com.mini_mo.viewpager.Cluster;

import android.media.Image;
import android.widget.ImageView;

public class MarkerItem {
    String image;
    String name;
    double lat;
    double lon;

    public MarkerItem(double lat, double lon,String name,String image){
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.image = image;
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

    public String getName(){return name;}

    public void setName(String name){this.name=name;}

    public String getImage(){return image;}

    public void setImage(String image){this.image = image;}
}
