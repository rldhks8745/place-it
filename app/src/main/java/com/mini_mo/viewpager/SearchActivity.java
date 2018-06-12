package com.mini_mo.viewpager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ReadAndWrite.ImageResizing;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.ReadAndWrite.WriteActivity;

import org.json.JSONException;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity{

    Data data;
    SearchListViewAdapter searchadapter;
    ArrayList<ListViewItemData> listViewItemData;
    RoundedBitmapDrawable roundedBitmapDrawable;
    User_Info user_info;
    ArrayList<ReadCommentInfo> rci;
    com.mini_mo.viewpager.DAO.ReadBoardInfo rbi;

    ImageButton searchbutton;

    EditText searchline;

    ListView listView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity);

        searchbutton = (ImageButton)findViewById(R.id.searchbutton);
        searchline = (EditText)findViewById(R.id.searchline);

        listView = (ListView)findViewById(R.id.listview);

        searchadapter = new SearchListViewAdapter();

        listView.setAdapter(searchadapter);

        try {
            listViewItemData = data.search_board(searchline.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<listViewItemData.size();i++){
            final ListViewItemData searchlistitem = listViewItemData.get(i);

            try {
                user_info = data.read_myPage(listViewItemData.get(i).user_id);
                rci = data.readComment(String.valueOf(listViewItemData.get(i).board_num));
                rbi = new Data().readBoardInfo(String.valueOf(listViewItemData.get(i).board_num));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(user_info.user_photo!=null) {

                Glide.with(getApplicationContext()).asBitmap().load(user_info.user_photo)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap bitmap = ImageResizing.ReSizing(ImageResizing.bitmapToByteArray(resource));

                                roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                roundedBitmapDrawable.setCircular(true);

                                searchadapter.addItem(searchlistitem.board_num,searchlistitem.content,searchlistitem.date_board,
                                        searchlistitem.good,searchlistitem.latitude,searchlistitem.longitude,
                                        searchlistitem.user_id,roundedBitmapDrawable,rci.size(),rbi.b_photos.size());
                            }
                        });

            }

            roundedBitmapDrawable = null;
        }

        searchadapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Store.board_num = searchadapter.getBoardNumber(position);

                Intent intent = new Intent(SearchActivity.this,ReadActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
