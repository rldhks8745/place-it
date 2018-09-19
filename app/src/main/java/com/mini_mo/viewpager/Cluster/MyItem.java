package com.mini_mo.viewpager.Cluster;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.mini_mo.viewpager.R;

//외부에서 위치를 받아서 반환 ㅡ
public class MyItem implements ClusterItem{
    private final LatLng mPosition;
    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }


    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
