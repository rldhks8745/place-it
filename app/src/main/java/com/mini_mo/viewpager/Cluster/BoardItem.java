package com.mini_mo.viewpager.Cluster;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BoardItem {

    public LatLng location;
    public int index =0 ;
    public ArrayList<MarkerItem> items = new ArrayList<MarkerItem>();

    public BoardItem( LatLng lo )
    {
        this.location = lo;
    }
}
