package com.mini_mo.viewpager.Camera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.R;

/**
 * Created by userForGame on 2018-05-01.
 */

public class CameraCommentsList extends AppCompatActivity {

    private RecyclerListView recyclerListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_commentslist_activity);

        View view = this.getWindow().getDecorView() ;
        recyclerListView = new RecyclerListView(this, view, this );
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
        recyclerListView.addItem();
    }
}
