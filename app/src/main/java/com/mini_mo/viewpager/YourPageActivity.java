package com.mini_mo.viewpager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ListView.RecyclerListView;

import org.json.JSONException;

import java.util.ArrayList;

public class YourPageActivity extends AppCompatActivity {


    private NestedScrollView nestedScrollView;
    private RecyclerListView recyclerListView;
    private User_Info user_info;

    String loginId;
    String yourId;

    ImageView icon;
    TextView id;
    TextView message;
    ArrayList<ListViewItemData> mylistItem;

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

        icon = (ImageView) findViewById(R.id.usericon);
        id = (TextView) findViewById(R.id.userid);
        message = (TextView) findViewById(R.id.status);

        yourId = getIntent().getStringExtra("id"); // 글쓴이 아이디
        loginId = MainActivity.getInstance().loginId; // 사용자 아이디

        try {
            user_info = new Data().read_myPage(yourId); // 상대방 정보 받아오기
            mylistItem = new Data().read_myBoard(yourId, 0);
            recyclerListView.loginId = yourId;
            recyclerListView.listViewItems.clear();
            recyclerListView.add(mylistItem);

            id.setText(user_info.user_id);
            message.setText(user_info.massage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //관심친구 추가
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(YourPageActivity.this, "관심 친구에 등록 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
