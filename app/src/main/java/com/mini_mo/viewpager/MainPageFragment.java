package com.mini_mo.viewpager;

/*
 * Created by 노현민 on 2018-04-19.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mini_mo.viewpager.ListView.RecyclerListView;


public class MainPageFragment extends Fragment{

    private View rootView;
    private RecyclerListView recyclerListView;
    private NestedScrollView nestedScrollView;
    // 생성자 필수
    public MainPageFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mainpage, container, false);
        nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.include);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ItemListView listView = new ItemListView(rootView);

        recyclerListView = new RecyclerListView(getContext(), view,this);

        recyclerListView.add(Store.sendboard);

        /* Google Map의 세로 드래그 문제를 해결 하기 위한 부분*/
        AppBarLayout appBar = (AppBarLayout) rootView.findViewById(R.id.app_bar);
        if (appBar.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();
            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            layoutParams.setBehavior(appBarLayoutBehaviour);
        }

        /** floating Button 클릭 이벤트 ( 위로 스크롤 하기 ) **/
        FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.up_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getContext(), "up", Toast.LENGTH_SHORT).show();
                AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);
                nestedScrollView.scrollTo(0,0);
                appBarLayout.setExpanded(true);
            }
        });

        /** 동적 로딩 설정 **/
        recyclerListView.loadItems(nestedScrollView, getContext());

        super.onViewCreated(view, savedInstanceState);
    }

}
