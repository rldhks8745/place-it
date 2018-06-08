package com.mini_mo.viewpager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ReadAndWrite.WriteActivity;

import org.json.JSONException;

import java.util.ArrayList;

/*
 * Created by 노현민 on 2018-04-19.
 */

public class MyPageFragment extends Fragment {

    //사진
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    final int IMAGE_CODE = 100;

    Animation ani=null;
    //사진

    private View rootView;
    private NestedScrollView nestedScrollView;
    private RecyclerListView recyclerListView;
    private User_Info user_info;
    String loginId;

    ImageView icon;
    TextView id;
    TextView message;
    TextView follow;
    TextView follower;

    private static MyPageFragment instance = null;
    ArrayList<ListViewItemData> mylistItem;

    public static MyPageFragment getInstance()
    {
        return instance;
    }

    // 생성자 필수
    public MyPageFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mypage, container, false);
        icon = (ImageView) rootView.findViewById(R.id.usericon);
        id = (TextView) rootView.findViewById(R.id.userid);
        message = (TextView) rootView.findViewById(R.id.status);
        follow = (TextView) rootView.findViewById(R.id.follow);
        follower = (TextView) rootView.findViewById(R.id.follower);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.include);
        recyclerListView = new RecyclerListView(getContext(), view, this);


        /** 동적 로딩 설정 **/
        recyclerListView.loadItems(nestedScrollView, getContext());

        /** Fab 클릭 이벤트 --> 코멘트 작성 액티비티로 전환 **/
        FloatingActionButton writeButton = (FloatingActionButton) rootView.findViewById(R.id.write_fab);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시글 작성 액티비티로 전환
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
            }
        });

        // 유저 이미지 클릭해서 변경할 때
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

                if(permissionCheck== PackageManager.PERMISSION_DENIED){

                    if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                        // 이 권한을 필요한 이유를 설명해야하는가?
                        //if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다

                        // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        //} else {


                        // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

                        //}
                    }
                }else{
                        ani = AnimationUtils.loadAnimation(getActivity(), R.anim.button_anim);
                        icon.startAnimation(ani);
                        //사진 추가하기

                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent, IMAGE_CODE);

                }
            }
        });

        // 상태메세지 변경
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    //사진 불러오는 곳

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case IMAGE_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;

                default:
                    break;
            }

        }
    }*/

    //사진 불러오는 곳

    public void setLoginId(String id)
    {
        loginId = id;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            user_info = new Data().read_myPage(loginId);
            mylistItem = new Data().read_myBoard(loginId, 0);
            String friends = new Data().count_friends(loginId);

            follow.setText( friends.substring( 0, friends.indexOf(',') ) );
            follower.setText( friends.substring( friends.indexOf(',')+1, friends.length()  ) );
            recyclerListView.loginId = loginId;
            recyclerListView.listViewItems.clear();
            recyclerListView.add(mylistItem);

            id.setText(user_info.user_id);
            // photo 넣는곳
            message.setText(user_info.massage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerListView.adapter.notifyDataSetChanged();
    }
}
