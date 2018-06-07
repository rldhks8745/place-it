package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.Save_List;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.util.ArrayList;

public class LoadLocateActivity extends AppCompatActivity{

    Data data;
    ArrayList<Save_List> save_lists;

    LocateListviewAdapter locateadapter;
    ListView locatelist;

    Activity activity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_loadlocate);

        locateadapter = new LocateListviewAdapter(this);
        locatelist = (ListView)findViewById(R.id.locatelist);

        data = new Data();

        activity = this;

        try {
            save_lists =  data.load_locate(Store.userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<save_lists.size();i++){
            Save_List saveList = save_lists.get(i);
            locateadapter.addItem(saveList.latitude,saveList.longitude,saveList.save_number,saveList.massage);
        }

        locatelist.setAdapter(locateadapter);

        locatelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                double[] arr = locateadapter.getLocate(position);

                Store.latitude = arr[0];
                Store.longitude = arr[1];

                Intent intent = new Intent(activity,WriteActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(activity,WriteActivity.class);
        startActivity(intent);
        finish();
    }
}
