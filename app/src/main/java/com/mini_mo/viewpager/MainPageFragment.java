package com.mini_mo.viewpager;

/*
 * Created by 노현민 on 2018-04-19.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mini_mo.viewpager.Camera.LoadingDialog;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.GpsInfo;


public class MainPageFragment extends Fragment{

    //GPS파트
    private GpsInfo gps;
    double latitude;
    double longitude;

    private View rootView;
    public RecyclerListView recyclerListView;
    private ImageView btnlocation;
    private TextView location; // 위치 표시 뷰
    private static MainPageFragment instance = null;

    public static MainPageFragment getInstance()
    {
        return instance;
    }
    // 생성자 필수
    public MainPageFragment()
    {
        instance = this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mainpage, container, false);
        btnlocation = (ImageView) rootView.findViewById(R.id.btnlocation);
        location = (TextView) rootView.findViewById(R.id.location);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ItemListView listView = new ItemListView(rootView);

        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 맵 액티비티로 전환

            }
        });
        if(Store.rlv == null)
            recyclerListView = new RecyclerListView(getContext(), view,this);
        else
            recyclerListView = Store.rlv;

        /* Google Map의 세로 드래그 문제를 해결 하기 위한 부분
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
        */

        /* floating Button 클릭 이벤트 ( 위로 스크롤 하기 )
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
        */
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        btnClick();
    }

    @Override
    public void onDestroy() {
        Store.rlv = recyclerListView;

        super.onDestroy();
    }

    public void btnClick()
    {
        // 현재 위치 받아오기

        gps = new GpsInfo(getContext());

        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            location.setText( AddressTransformation.getAddress(instance.getActivity(), latitude, longitude));
        } else {
            // GPS 를 사용할수 없으므로

            gps.showSettingsAlert();
        }
    }


}