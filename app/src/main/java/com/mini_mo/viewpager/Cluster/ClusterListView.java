package com.mini_mo.viewpager.Cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.maps.android.clustering.Cluster;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.R;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by userForGame on 2018-05-01.
 */

public class ClusterListView extends AppCompatActivity {

    private RecyclerListView recyclerListView;
    Data data = new Data();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_commentslist_activity);


    }
    public void ClusterListView() {
        ClusterMap cm = new ClusterMap();
        for (int i = 0; i < cm.clustericon.size(); i++) {
            double lat = cm.clustericon.get(i).latitude;
            double lon = cm.clustericon.get(i).longitude;
            try {
                ArrayList<ListViewItemData> items = data.read_board_camera(lat, lon);
                View view = this.getWindow().getDecorView();
                recyclerListView = new RecyclerListView(this, view, this);
                recyclerListView.add(items);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
