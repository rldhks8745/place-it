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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mini_mo.viewpager.Camera.LoadingDialog;
import com.mini_mo.viewpager.Cluster.ClusterMap;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.GpsInfo;

import org.json.JSONException;

import java.util.ArrayList;


public class MainPageFragment extends Fragment{

    public static final int START_UP = 0;
    public static final int MAP_UP = 1;
    // 어떤 글을 가지고 왔나
    public int whatContent = START_UP;

    //GPS파트
    public GpsInfo gps;
    public double latitude = 0.0;
    public double longitude = 0.0;
    private ClusterMap clusterMap;
    private Animation ani;

    int count = 0;

    private View rootView;
    public RecyclerListView recyclerListView;
    private ImageView btnlocation;
    private TextView location; // 위치 표시 뷰
    private static MainPageFragment instance = null;

    ArrayList<ListViewItemData> near;
    ArrayList<ListViewItemData> push_data = null;

    public LoadingDialog loading;

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

        location.setText( AddressTransformation.getAddress( instance.getActivity(), latitude, longitude ) );

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        //ItemListView listView = new ItemListView(rootView);

        try {

            push_data = getActivity().getIntent().getParcelableArrayListExtra("Push_ArrayList");
            if(!(push_data == null)) {
                Log.i("MainPageFragment", "size" + push_data.size());
            }
            else
            {
                Log.i("MainPageFragment", "push_data = null");
            }
        } catch (Exception e)
        {
            Log.i("MainPageFragment", "ExtraData가 없읍니다.");
        }

        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ani = AnimationUtils.loadAnimation(getActivity(),R.anim.button_anim);
                btnlocation.startAnimation(ani);

                Intent mapintent = new Intent(getContext(),ClusterMap.class);
                startActivity(mapintent);
            }
        });

        view.findViewById(R.id.recyclerView);
        if(Store.rlv == null)
            recyclerListView = new RecyclerListView( getContext(), view,this );
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

            if(push_data == null)
            {
                near = data.read_board_list(min_lat,min_lng,max_lat,max_lng,count);
            }
            else
            {
                near = push_data;
            }

            Store.sendboard = near;
            if(recyclerListView.listViewItems!=null)
            {
                recyclerListView.listViewItems.clear();
                recyclerListView.add(near);
                recyclerListView.adapter.notifyDataSetChanged();
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        push_data = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if( whatContent != MAP_UP ) // 맵을통해 사용자가 content를 가져왔으면
        {
            getLocation(GpsInfo.MAINPAGE);
        }

        if(recyclerListView.listViewItems!=null)
            recyclerListView.listViewItems.clear();

        if(Store.sendboard!=null)
        {
            recyclerListView.add(Store.sendboard);
            recyclerListView.adapter.notifyDataSetChanged();
        }
    }

    public void setTextLocation( Location lo )
    {
        latitude = lo.getLatitude();
        longitude = lo.getLongitude();

        Log.d("GSP 로그", latitude + " " + longitude);


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
        if( !gps.setupLocation( flag ) ) // 정상적으로 gps가 실행되지 않으면
        {
            if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            } else {
                if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                } else {
                    // GPS 사용유무 가져오기
                    if (!gps.isGetLocation()) {
                        // GPS 를 사용할수 없으므로
                        loading.progressOFF();
                        gps.showSettingsAlert();
                    }
                }
            }
        }
        else
        {
            if( latitude == 0.0 )
                loading.progressON( this.getActivity(), "GPS 수신중");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( grantResults.length > 0)
        {
            // 권한허가를 받았는가
            boolean cameraAccepted = ( grantResults[0] == PackageManager.PERMISSION_GRANTED );
            if( cameraAccepted )
            {
                return;
            }
            else
            {
                if (requestCode == 100)
                {
                    if( ContextCompat.checkSelfPermission( this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                    {
                        ActivityCompat.requestPermissions( this.getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101 );
                    }
                    else
                    {
                        // GPS 사용유무 가져오기
                        if (!gps.isGetLocation())
                        {
                            // GPS 를 사용할수 없으므로
                            loading.progressOFF();
                            gps.showSettingsAlert();
                        }
                    }

                }
                else if (requestCode == 101)
                {
                    // GPS 사용유무 가져오기
                    if (!gps.isGetLocation())
                    {
                        // GPS 를 사용할수 없으므로
                        loading.progressOFF();
                        gps.showSettingsAlert();
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}