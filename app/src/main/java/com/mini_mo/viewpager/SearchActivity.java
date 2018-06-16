package com.mini_mo.viewpager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ReadAndWrite.ImageResizing;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.ReadAndWrite.Util;
import com.mini_mo.viewpager.ReadAndWrite.WriteActivity;

import org.json.JSONException;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity{

    Data data;
    SearchListViewAdapter searchadapter;
    ArrayList<ListViewItemData> listViewItemDatas;
    RoundedBitmapDrawable roundedBitmapDrawable;
    User_Info user_info;
    ArrayList<ReadCommentInfo> rci;
    com.mini_mo.viewpager.DAO.ReadBoardInfo rbi;
    ListViewItemData listViewItemData;

    ImageButton searchbutton;

    Intent intent;

    TextView searchline;

    ListView listView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity);

        intent = getIntent();
        searchbutton = (ImageButton)findViewById(R.id.searchbutton);
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

            try {
                user_info = data.read_myPage(listViewItemDatas.get(i).user_id);
                rci = data.readComment(String.valueOf(listViewItemDatas.get(i).board_num));
                rbi = data.readBoardInfo(String.valueOf(listViewItemDatas.get(i).board_num));

                Util.Log("rci 사이즈",String.valueOf(rci.size()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(user_info.user_photo!=null) {

                Glide.with(getApplicationContext()).asBitmap().load(user_info.user_photo)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                int count=0;
                                Bitmap bitmap = ImageResizing.ReSizing(ImageResizing.bitmapToByteArray(resource));

                                roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                roundedBitmapDrawable.setCircular(true);



                                if(rbi.b_photos == null || rbi.b_photos.size() == 0){
                                    count = 0;
                                }else{
                                    count = rbi.b_photos.size();
                                    Util.Log("사진 유무",rbi.b_photos.get(0));
                                }

                                Util.Log("객체 안속", listViewItemData.board_num+"\n"+
                                        listViewItemData.content+"\n"+
                                        listViewItemData.date_board+"\n"+
                                        listViewItemData.good+"\n"+
                                        listViewItemData.latitude+"\n"+
                                        listViewItemData.longitude+"\n"+
                                        listViewItemData.user_id+"\n"+
                                        listViewItemData.user_photo);


                                searchadapter.addItem(listViewItemData.board_num,listViewItemData.content,listViewItemData.date_board,
                                        listViewItemData.good,listViewItemData.latitude,listViewItemData.longitude,
                                        listViewItemData.user_id,roundedBitmapDrawable,rci.size(),count);

                                searchadapter.notifyDataSetChanged();
                            }
                        });

            }

            roundedBitmapDrawable = null;
        }



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

    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}