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
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ListView.RecyclerListView;
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
    private FloatingActionButton fab;
    private Animation ani;
    public User_Info user_info;
    private User_Info your_location;

    private Activity activity;
    String loginId;
    String yourId;

    ArrayList<ListViewItemData> mylistItem;
    private TextView followers;
    private TextView following;
    private TextView userId;
    private TextView location;
    private TextView message;
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

        View view = this.getWindow().getDecorView() ;
        nestedScrollView = (NestedScrollView) findViewById(R.id.include);
        recyclerListView = new RecyclerListView( this, view, this);
        /** 동적 로딩 설정 **/
        recyclerListView.loadItems(nestedScrollView, this);

        // 레이아웃 객체화
        followers = (TextView) view.findViewById(R.id.follwers);
        following = (TextView) view.findViewById(R.id.following);
        userId = (TextView) view.findViewById(R.id.userid);
        location = (TextView) view.findViewById(R.id.location);
        message = (TextView) view.findViewById(R.id.message);
        usericon = (ImageView) view.findViewById(R.id.usericon);


        yourId = getIntent().getStringExtra("id"); // 글쓴이 아이디
        loginId = MainActivity.getInstance().loginId; // 사용자 아이디

        //관심친구 추가
        fab = (FloatingActionButton) findViewById(R.id.write_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result;

                ani = AnimationUtils.loadAnimation(activity, R.anim.button_anim);
                fab.startAnimation(ani);

                try {
                    result = new Data().add_friends(loginId, yourId);

                    String friends = new Data().count_friends(yourId);
                    following.setText( friends.substring( 0, friends.indexOf(',') ) );
                    followers.setText( friends.substring( friends.indexOf(',')+1, friends.length()  ) );

                } catch (JSONException e) {
                    e.printStackTrace();

                    result = -1;
                }

                if( result == 1 ) {
                    fab.setImageResource(R.drawable.friendlikeon);
                    Toast.makeText(YourPageActivity.this, "관심 친구에 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                }else if( result == 0 ) {
                    fab.setImageResource(R.drawable.friendlikeoff);
                    Toast.makeText(YourPageActivity.this, "관심 친구가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                }else if( result < 0 )
                    Toast.makeText(YourPageActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 상대방 사진 누르면 확대해서 보이기
        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImage = true;

                Intent intent = new Intent( YourPageActivity.getInstance() , ProfileImageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        clickImage = false; // 이미지 클릭했나?
        super.onResume();
        try {
            user_info = new Data().read_myPage(yourId); // 상대방 정보 받아오기
            mylistItem = new Data().read_myBoard(yourId, 0);
            your_location = new Data().read_myLocation(yourId);
            recyclerListView.loginId = yourId;
            recyclerListView.listViewItems.clear();
            recyclerListView.add(mylistItem);

            String friends = new Data().count_friends(yourId);
            following.setText( friends.substring( 0, friends.indexOf(',') ) );
            followers.setText( friends.substring( friends.indexOf(',')+1, friends.length()  ) );


            Glide.with( this )
                    .load( user_info.user_photo )
                    //.apply( new RequestOptions().placeholder( R.drawable.user ).error( R.drawable.user ))
                    .into( usericon );

            userId.setText(user_info.user_id);
            if(your_location.latitude == 0.0 || your_location.longitude == 0.0){
                location.setText("위치가 설정되지 않았습니다.");
            }else{
                location.setText(AddressTransformation.getAddress(this, your_location.latitude, your_location.longitude));
            }
            message.setText(user_info.massage);
        } catch (JSONException e) {
            e.printStackTrace();
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
