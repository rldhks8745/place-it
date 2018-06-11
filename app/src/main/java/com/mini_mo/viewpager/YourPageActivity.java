package com.mini_mo.viewpager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.ReadAndWrite.ReadBoard_Image_Activity;

import org.json.JSONException;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class YourPageActivity extends AppCompatActivity {

    static public YourPageActivity instance = null;
    public boolean clickImage = false;

    private NestedScrollView nestedScrollView;
    private RecyclerListView recyclerListView;
    private User_Info user_info;

    String loginId;
    String yourId;

    public ImageView icon;
    TextView id;
    TextView message;
    TextView follow;
    TextView follower;

    ArrayList<ListViewItemData> mylistItem;

    public Bitmap icon_bitmap;

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

        View view = this.getWindow().getDecorView() ;
        nestedScrollView = (NestedScrollView) findViewById(R.id.include);
        recyclerListView = new RecyclerListView( this, view, this);
        /** 동적 로딩 설정 **/
        recyclerListView.loadItems(nestedScrollView, this);

        // 레이아웃 객체화
        icon = (ImageView) findViewById(R.id.usericon);
        id = (TextView) findViewById(R.id.userid);
        message = (TextView) findViewById(R.id.status);
        follow = (TextView) findViewById(R.id.follow);
        follower = (TextView) findViewById(R.id.follower);

        yourId = getIntent().getStringExtra("id"); // 글쓴이 아이디
        loginId = MainActivity.getInstance().loginId; // 사용자 아이디

        //관심친구 추가
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int result;
                try {
                    result = new Data().add_friends(loginId, yourId);

                    String friends = new Data().count_friends(yourId);
                    follow.setText( friends.substring( 0, friends.indexOf(',') ) );
                    follower.setText( friends.substring( friends.indexOf(',')+1, friends.length()  ) );

                } catch (JSONException e) {
                    e.printStackTrace();

                    result = -1;
                }

                if( result == 1 )
                    Toast.makeText(YourPageActivity.this, "관심 친구에 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                else if( result == 0 )
                    Toast.makeText(YourPageActivity.this, "관심 친구가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                else if( result < 0 )
                    Toast.makeText(YourPageActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 상대방 사진 누르면 확대해서 보이기
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImage = true;

                Intent intent = new Intent( YourPageActivity.getInstance() , ReadBoard_Image_Activity.class);
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
            recyclerListView.loginId = yourId;
            recyclerListView.listViewItems.clear();
            recyclerListView.add(mylistItem);

            String friends = new Data().count_friends(yourId);
            follow.setText( friends.substring( 0, friends.indexOf(',') ) );
            follower.setText( friends.substring( friends.indexOf(',')+1, friends.length()  ) );


            Glide.with( this )
                    .load( user_info.user_photo )
                    .apply( new RequestOptions().override(100,100).placeholder( R.drawable.user ).error( R.drawable.user ))
                    .into( icon );

            id.setText(user_info.user_id);
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

        if( icon_bitmap != null )
        {
            icon_bitmap.recycle();
            icon_bitmap = null;
        }
    }
}
