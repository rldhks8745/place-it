package com.mini_mo.viewpager.FriendListView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.FriendsList;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;
import com.mini_mo.viewpager.YourPageActivity;

import org.json.JSONException;

import java.util.ArrayList;

/*
 * Created by 노현민 on 2018-04-21.
 */

public class FriendListFragment extends Fragment{

    private View rootView;
    private ListView listView;
    private FriendListView adapter; // Adapter
    private ArrayList<FriendsList> items;

    String yourId; // 상대방 아이디

    // 생성자 필수
    public FriendListFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.content_friendlist, container, false);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /** listView 부분 **/
        listView = (ListView) rootView.findViewById(R.id.friendListView);
        adapter = new FriendListView();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO 리스트의 아이템 클릭시
                Intent intent = new Intent(getActivity(), YourPageActivity.class);
                intent.putExtra("yourId", adapter.listViewItemList.get(position).)
                startActivity(intent);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            items = new Data().readFriends(MainActivity.getInstance().loginId); // 친구 목록 받아옴
            adapter.add(items);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
