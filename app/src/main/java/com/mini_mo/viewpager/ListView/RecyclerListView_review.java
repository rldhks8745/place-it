package com.mini_mo.viewpager.ListView;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.CustomListviewitem;

import org.json.JSONException;

import java.util.ArrayList;

/*
 * Created by 노현민 on 2018-04-29.
 */

public class RecyclerListView_review {

    private RecyclerView recyclerView;
    public RecyclerViewAdapter_review adapter;
    public ArrayList<ReadCommentInfo> listViewItems;

    public String loginId;
    int count;

    /**
     * 생성자
     * listViewItems 생성 및 어댑터 설정
     */
    public RecyclerListView_review(Context context, View view, Fragment fragment)
    {
        listViewItems = new ArrayList<ReadCommentInfo>();
        adapter = new RecyclerViewAdapter_review(listViewItems, R.layout.rnw_custom_listview, fragment);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public RecyclerListView_review(Context context, View view, AppCompatActivity activity )
    {
        listViewItems = new ArrayList<ReadCommentInfo>();
        adapter = new RecyclerViewAdapter_review(listViewItems, R.layout.rnw_custom_listview, activity);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    // Default 값, 테스트 용
    public void addItem()
    {
        ReadCommentInfo listViewItem = new ReadCommentInfo();
        listViewItems.add(listViewItem);
    }

    public void add(ArrayList<ReadCommentInfo> items)
    {
        Log.i("숫자는?",items.size()+"");
        for( int i = 0; i < items.size(); i++ )
        {
            listViewItems.add(items.get(i));
        }
        adapter.notifyDataSetChanged();
    }

   /*동적 로딩을 위한 NestedScrollView의 아래 부분을 인식
    public void loadItems(NestedScrollView nestedScrollView, final Context context) {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // TODO add listItems

                    count += 10;
                    //Toast.makeText(context, "loading", Toast.LENGTH_SHORT).show();
                    ArrayList<ListViewItemData> mylistItem = null;
                    try {
                        mylistItem = new Data().read_myBoard(loginId, count);
                        if( ( mylistItem.size() != 0 ) && ( mylistItem != null ) ) {
                            add(mylistItem);
                            // 어댑터에 연결된 ListView를 갱신
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }*/

    /** 데이터베이스 연동 부분 **/
    /** 데이터베이스로 보내주는 부분**/
    /* 현재 사용자의 위치 */
    /* count ( 코멘트를 10개씩 가져오기 위함 AND 메인 화면에 게시글 수 ( = ArrayList<DAO_ListViewItem>의 크기 ) )*/
    /** 데이터베이스에서 받는 부분 **/
    /* ArrayList<DAO_ListViewItem> readListViweItems() 메소드를 사용하여 코멘트 받아오기 */
}