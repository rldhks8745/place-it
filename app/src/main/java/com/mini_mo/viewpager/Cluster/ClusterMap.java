package com.mini_mo.viewpager.Cluster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.MainPageFragment;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.Store;
import com.mini_mo.viewpager.YourPageActivity;

import org.json.JSONException;

import java.util.ArrayList;


public class ClusterMap extends AppCompatActivity
        implements OnMapReadyCallback,View.OnClickListener {
    public ArrayList<BoardItem> boardItems = new ArrayList<BoardItem>();
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    Marker customMarker=null;

    private static final String TAG = "googlemap_example";
    private AppCompatActivity mActivity;
    private ClusterManager<MyItem> mClusterManager = null;
    private Animation ani;

    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng savePoint;
    ArrayList<MarkerItem> sampleList = new ArrayList();
    float zoomLevel = 17;
    LatLng temp_savepoint;

    int bdindex = 0;

    ImageView ok, nowlocation, cancel,store_image;
    TextView textView,store_name,store_status;
    View marker_root_view;

    ArrayList<ListViewItemData> clustericon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.cluster_map);

        ok = (ImageView) findViewById(R.id.ok);
        nowlocation = (ImageView) findViewById(R.id.nowlocation);
        cancel = (ImageView) findViewById(R.id.cancel);

        textView = (TextView) findViewById(R.id.textView);

        ok.setOnClickListener(this);
        nowlocation.setOnClickListener(this);
        cancel.setOnClickListener(this);

        marker_root_view = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
        store_name = (TextView)marker_root_view.findViewById(R.id.store_name);
        store_status = (TextView)marker_root_view.findViewById(R.id.store_status);
        store_image = (ImageView)marker_root_view.findViewById(R.id.store_image);

        Log.d(TAG, "onCreate");
        mActivity = this;


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        savePoint = new LatLng(MainPageFragment.getInstance().latitude,MainPageFragment.getInstance().longitude);

    }


    @Override
    public void onResume() {
        super.onResume();
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        setCurrentLocation(savePoint);
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d(TAG, "onMyLocationButtonClick ㅡ");
                mClusterManager.clearItems();
                getVisibleRegion();
                clustericon.clear();
                mMoveMapByAPI = true;
                return true;
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(mActivity);
            dialog  .setTitle("프로필 선택")
                        .setMessage("같은 위치에 다른 가게가 있습니다. 다음 가게 정보를 보시겠습니까?")
                        .setPositiveButton("다음 가게", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int boardItemIndex = Integer.parseInt( marker.getSnippet() );

                                    boardItems.get(boardItemIndex).index = bdindex;

                                if( boardItems.get( boardItemIndex ).index >= boardItems.get( boardItemIndex ).items.size()-1 ) // 0,1,2   size = 3 ;  index = 2 ,  2  >= 2
                                    boardItems.get( boardItemIndex ).index = 0;
                                else
                                    boardItems.get( boardItemIndex ).index++;
                                mGoogleMap.clear();
                                getVisibleRegion();

                                bdindex = boardItems.get(boardItemIndex).index;
                            }
                        })
                        .setNegativeButton("가게 페이지", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), YourPageActivity.class);
                                String str =marker.getTitle().toString();
                                intent.putExtra("id",str);
                                startActivity(intent);
                                getVisibleRegion();
                            }
                        }).create().show();
                return false;
            }
        });
        if (savePoint != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savePoint, zoomLevel));
        }
        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                savePoint = cameraPosition.target;
            }
        });
        temp_savepoint = savePoint;
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        mClusterManager = new ClusterManager<>(this, mGoogleMap);
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(customMarker != null) {
                    mGoogleMap.clear();
                }
                if(boardItems.size()!=0 &&
                        temp_savepoint.latitude + 0.0002 < savePoint.latitude ||
                        savePoint.latitude < temp_savepoint.latitude - 0.0002 ||
                        temp_savepoint.longitude + 0.0002 < savePoint.longitude ||
                        savePoint.longitude < temp_savepoint.longitude - 0.0002){
                    boardItems.clear();
                }
                mGoogleMap.clear();
                getVisibleRegion();
                mClusterManager.cluster();
                temp_savepoint = savePoint;
            }
        });
    }

    //디바이스에 출력되는 지도 범위
    public void getVisibleRegion() {

        Data data = new Data();

        mClusterManager.clearItems();

        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        Log.d("TEST", bounds.toString());
        double max_lat, max_lng, min_lat, min_lng;
        max_lat = bounds.northeast.latitude;
        max_lng = bounds.northeast.longitude;
        min_lat = bounds.southwest.latitude;
        min_lng = bounds.southwest.longitude;


        try {
            clustericon = data.read_board_list(min_lat, min_lng, max_lat, max_lng);
            for (int i = 0; i < clustericon.size(); i++) {
                for( int j=0; j< clustericon.size(); j++)
                {
                    if( i != j )
                        if (clustericon.get(i).user_id.equals(clustericon.get(j).user_id))
                            clustericon.remove(j);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mGoogleMap.getCameraPosition().zoom >= 18.8) {
            mClusterManager.clearItems();
            for (int j = 0; j < boardItems.size(); j++)
            {
                boardItems.get(j).items.clear();
            }

            for (int i = 0; i < clustericon.size(); i++)
            {
                if( boardItems.size() != 0)
                {
                    for (int j = 0; j < boardItems.size(); j++)
                    {
                        if (boardItems.get(j).location.latitude == clustericon.get(i).latitude &&
                                boardItems.get(j).location.longitude == clustericon.get(i).longitude) // 위도 경도 같으면 add 아니면 new
                        {
                            boardItems.get(j).items.add(new MarkerItem(clustericon.get(i).latitude,
                                    clustericon.get(i).longitude, clustericon.get(i).user_id, clustericon.get(i).user_photo));
                        } else {
                            boardItems.add(new BoardItem(new LatLng(clustericon.get(i).latitude,
                                    clustericon.get(i).longitude)));
                        }
                    }
                }
                else
                {
                    boardItems.add(new BoardItem(new LatLng(clustericon.get(i).latitude,
                            clustericon.get(i).longitude)));
                    boardItems.get(0).items.add(new MarkerItem(clustericon.get(i).latitude,
                            clustericon.get(i).longitude, clustericon.get(i).user_id, clustericon.get(i).user_photo));
                }
            }
            setSampleMarkerItems( boardItems );
        }
         else {
            if(customMarker != null) {
                mGoogleMap.clear();

            }
            if(sampleList!=null)
            sampleList.clear();
            for (int i = 0; i < clustericon.size(); i++) {
                double lat = clustericon.get(i).latitude;
                double lng = clustericon.get(i).longitude;
                mClusterManager.getClusterMarkerCollection().clear();
                MyItem myItem = new MyItem(lat, lng);
                mClusterManager.addItem(myItem);
            }
        }

    }
    private void setSampleMarkerItems( ArrayList<BoardItem> boardItems ) {
        for( int i=0; i<boardItems.size(); i++ )
        {
            if(boardItems.get(i).items != null&& boardItems.get(i).items.size() != 0 )
            addMarker( boardItems.get(i).items.get( boardItems.get(i).index ), i );
        }

    }

    private Marker addMarker(MarkerItem markerItem, int itemIndex) {

        User_Info user_info = new User_Info();
        try {
            user_info = new Data().read_myPage( markerItem.getName() );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        String status = user_info.massage;
        String name = user_info.nickname;

        store_status.setText(status.toString());
        store_name.setText(name.toString());

        store_status.setTextColor(Color.BLACK);
        store_name.setTextColor(Color.BLACK);

        Glide.with(this)
                .load( markerItem.getImage() )
                .apply( new RequestOptions().override(0,0).placeholder(R.drawable.noimg).error(R.drawable.noimg))
                .into(store_image);

        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        marker.title( markerItem.name );
        marker.snippet("" + itemIndex );
        marker.position(position);
        return customMarker = mGoogleMap.addMarker(marker);

    }

    private Bitmap createDrawableFromView(Context context, View view) {


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }




    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putFloat("zoom",zoomLevel);
        outState.putDouble("lat",savePoint.latitude);
        outState.putDouble("lng",savePoint.longitude);
    }


    @Override
    protected void onStart() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setCurrentLocation(LatLng location) {

        mMoveMapByUser = false;

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정 fix - 2017. 11.27



        if (mMoveMapByAPI) {

            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.latitude + " " + location.longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(location);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                ok.startAnimation(ani);
                clustericon.clear();
                getVisibleRegion();
                if(Store.sendboard!=null)
                    Store.sendboard.clear();
                if(clustericon!=null){
                    Store.sendboard = clustericon;
                }
                zoomLevel = mGoogleMap.getCameraPosition().zoom;
                MainPageFragment.getInstance().whatContent = MainPageFragment.MAP_UP;
                finish();

                break;
            case R.id.cancel:
                finish();
                break;

            case R.id.nowlocation:

                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                nowlocation.startAnimation(ani);

                textView.setText(AddressTransformation.getAddress(this, savePoint.latitude, savePoint.longitude));
                break;

        }
    }
}