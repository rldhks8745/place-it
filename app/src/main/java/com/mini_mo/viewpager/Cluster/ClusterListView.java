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
    ArrayList<MyItem> ary;

    Data data = new Data();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_commentslist_activity);
        Intent intent = getIntent();
        ary = intent.getDoubleExtra("location", MyItem.getPosition);
        /*
            위도,경도로 코멘트들 다 받아오기
         */
        Data data = new Data();
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
