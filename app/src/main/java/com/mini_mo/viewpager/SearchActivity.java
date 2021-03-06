package com.mini_mo.viewpager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.Camera.CameraActivity;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ReadAndWrite.ImageResizing;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.ReadAndWrite.Util;

import org.json.JSONException;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity{

    public static SearchActivity instance;

    Data data;
    SearchListViewAdapter searchadapter;
    ArrayList<ListViewItemData> listViewItemDatas;
    ListViewItemData listViewItemData;
    Intent intent;

    ImageView searchbutton;
    TextView searchline;
    ListView listView;

    public static SearchActivity getInstance()
    {
        return instance;
    }
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity);
        instance = this;

        SearchListViewAdapter.isShowing = true;

        intent = getIntent();
        searchbutton = (ImageView)findViewById(R.id.searchbutton);
        searchline = (TextView)findViewById(R.id.searchline);

        listView = (ListView)findViewById(R.id.listview);

        searchadapter = new SearchListViewAdapter();

        listView.setAdapter(searchadapter);

        listViewItemDatas = new ArrayList<>();
        data = new Data();

        Util.Log("인텐트",intent.getStringExtra("content"));

        try {
            listViewItemDatas = data.search_board(intent.getStringExtra("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i=0;i<listViewItemDatas.size();i++){
            listViewItemData = listViewItemDatas.get(i);

            searchadapter.addItem(listViewItemData.board_num,listViewItemData.content,listViewItemData.date_board,listViewItemData.good,listViewItemData.latitude,listViewItemData.longitude,listViewItemData.user_id,listViewItemData.user_photo,listViewItemData.comment_cnt);
            searchadapter.notifyDataSetChanged();

        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Store.board_num = searchadapter.getBoardNumber(position);

                Intent intent = new Intent(SearchActivity.this,ReadActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        finish();
        super.onBackPressed();

        SearchListViewAdapter.isShowing = false;
    }

}
