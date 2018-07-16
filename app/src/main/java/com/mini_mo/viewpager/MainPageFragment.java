package com.mini_mo.viewpager;

/*
 * Created by 노현민 on 2018-04-19.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mini_mo.viewpager.Camera.LoadingDialog;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.GpsInfo;

import org.json.JSONException;

import java.util.ArrayList;


public class MainPageFragment extends Fragment{

    //GPS파트
    private GpsInfo gps;
    public double latitude = 0.0;
    public double longitude = 0.0;

    int count = 0;

    private View rootView;
    public RecyclerListView recyclerListView;
    private ImageView btnlocation;
    private TextView location; // 위치 표시 뷰
    private static MainPageFragment instance = null;

    ArrayList<ListViewItemData> near;
    LinearLayout linearLayout;

    LoadingDialog loading;

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

        loading = new LoadingDialog();
        gps = new GpsInfo( getContext() );

        location.setText(AddressTransformation.getAddress(instance.getActivity(), latitude, longitude));
        getLocation( GpsInfo.MAINPAGE );

        if( latitude == 0.0 )
            loading.progressON( this.getActivity(), "위치 수신 준비중");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //ItemListView listView = new ItemListView(rootView);

        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.findViewById(R.id.recyclerView);
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

    public void nearSearch(){
        double max_lat, max_lng, min_lat, min_lng;
        max_lat = latitude + 0.0005;
        max_lng = longitude + 0.0005;
        min_lat = latitude - 0.0005;
        min_lng = longitude -0.0005;

        Data data = new Data();

        try{
            near = data.read_board_list(min_lat,min_lng,max_lat,max_lng,count);
            Store.sendboard = near;
            recyclerListView.add(near);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setTextLocation( Location lo )
    {
        latitude = lo.getLatitude();
        longitude = lo.getLongitude();
        location.setText(AddressTransformation.getAddress(instance.getActivity(), latitude, longitude));
        loading.progressOFF();

        nearSearch();
    }

    @Override
    public void onDestroy() {
        Store.rlv = recyclerListView;

        super.onDestroy();
    }

    public void getLocation( int flag )
    {
        gps.setupLocation( flag );

        if( ContextCompat.checkSelfPermission( this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission( this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions( MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions( MainActivity.getInstance(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        else
        {
            // GPS 사용유무 가져오기
            if ( !gps.isGetLocation() ) {
                // GPS 를 사용할수 없으므로
                loading.progressOFF();
                gps.showSettingsAlert();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}