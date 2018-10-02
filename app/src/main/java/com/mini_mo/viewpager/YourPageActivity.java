package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ListView.RecyclerListView_review;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.ReadBoard_Image_Activity;

import org.json.JSONException;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class YourPageActivity extends AppCompatActivity {

    static public YourPageActivity instance = null;
    public boolean clickImage = false;

    private NestedScrollView nestedScrollView;
    private RecyclerListView recyclerListView;
    private RecyclerListView_review recyclerListView_review;
    public User_Info user_info;
    private User_Info your_location;
    private Animation ani;
    View view;

    private Activity activity;
    String loginId;
    String yourId;
    int tap_onoff;

    ArrayList<ListViewItemData> mylistItem;
    ArrayList<ReadCommentInfo> reviewlistitem;
    private TextView userId;
    private TextView location;
    private TextView message;
    private TextView review;
    private TextView board;
    private ImageView board_line;
    private ImageView review_line;
    private ImageView usericon;

    static public YourPageActivity getInstance()
    {
        return instance;
    }
    public YourPageActivity()
    {
        instance = this;
    }
    //사진 확대
    PhotoViewAttacher mAttacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activity = this;

        tap_onoff = 1;

        view = this.getWindow().getDecorView() ;
        nestedScrollView = (NestedScrollView) findViewById(R.id.include);


        // 레이아웃 객체화
        userId = (TextView) view.findViewById(R.id.userid);
        location = (TextView) view.findViewById(R.id.location);
        message = (TextView) view.findViewById(R.id.message);
        usericon = (ImageView) view.findViewById(R.id.usericon);
        review = (TextView) view.findViewById(R.id.review);
        board = (TextView) view.findViewById(R.id.board);
        review_line = (ImageView) view.findViewById(R.id.review_line);
        board_line = (ImageView) view.findViewById(R.id.board_line);


        yourId = getIntent().getStringExtra("id"); // 글쓴이 아이디
        loginId = MainActivity.getInstance().loginId; // 사용자 아이디

        // 상대방 사진 누르면 확대해서 보이기
        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImage = true;

                Intent intent = new Intent( YourPageActivity.getInstance() , ProfileImageActivity.class);
                startActivity(intent);
            }

        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ani = AnimationUtils.loadAnimation(activity,R.anim.alpha2);
                review_line.startAnimation(ani);

                ani = AnimationUtils.loadAnimation(activity,R.anim.alpha2);
                review.startAnimation(ani);

                tap_onoff = 2;
                onResume();

                review_line.setImageResource(R.drawable.click_line);
                board_line.setImageResource(R.drawable.clickoff_line);
            }
        });

        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ani = AnimationUtils.loadAnimation(activity,R.anim.alpha2);
                board_line.startAnimation(ani);

                ani = AnimationUtils.loadAnimation(activity,R.anim.alpha2);
                board.startAnimation(ani);

                tap_onoff = 1;
                onResume();

                review_line.setImageResource(R.drawable.clickoff_line);
                board_line.setImageResource(R.drawable.click_line);
            }
        });
    }

    @Override
    protected void onResume() {
        clickImage = false; // 이미지 클릭했나?
        super.onResume();

        if(tap_onoff==1) {
            recyclerListView = new RecyclerListView( this, view, this);

            try {
                user_info = new Data().read_myPage(yourId); // 상대방 정보 받아오기
                mylistItem = new Data().read_myBoard(yourId, 0);
                your_location = new Data().read_myLocation(yourId);
                recyclerListView.loginId = yourId;
                recyclerListView.listViewItems.clear();
                recyclerListView.add(mylistItem);

                /** 동적 로딩 설정 **/
                recyclerListView.loadItems(nestedScrollView, this);

                Glide.with( this )
                        .load( user_info.user_photo )
                        .apply( new RequestOptions().override(usericon.getWidth(),usericon.getHeight()).placeholder( R.drawable.user ).error( R.drawable.user ))
                        .into( usericon );

                userId.setText(user_info.user_id);
                if (your_location.latitude == 0.0 || your_location.longitude == 0.0) {
                    location.setText("위치가 설정되지 않았습니다.");
                } else {
                    location.setText(AddressTransformation.getAddress(this, your_location.latitude, your_location.longitude));
                }
                message.setText(user_info.massage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            recyclerListView_review = new RecyclerListView_review(this, view, this);

            Log.i("아이디가 뭘까?", yourId);

            try {
                // 사용자 정보
                reviewlistitem = new Data().read_myPage_comment(yourId);

                // 보드 정보
                recyclerListView_review.loginId = yourId;
                recyclerListView_review.listViewItems.clear();
                recyclerListView_review.add(reviewlistitem);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            recyclerListView_review.adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
