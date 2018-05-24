package com.mini_mo.viewpager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


    private View rootView;
    private RecyclerListView recyclerListView;
    private User_Info user_info;
    String loginId;

    ImageView icon;
    TextView id;
    TextView message;

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

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerListView = new RecyclerListView(getContext(), view,this);
        ArrayList<ListViewItemData> mylistItem = new ArrayList<>();

        try {
            user_info = new Data().read_myPage(loginId);
            mylistItem = new Data().read_myBoard(loginId, 0);
            recyclerListView.add(mylistItem);

            id.setText(user_info.user_id);
            message.setText(user_info.massage);
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
        super.onViewCreated(view, savedInstanceState);
    }

    public void setLoginId(String id)
    {
        loginId = id;
    }
}
