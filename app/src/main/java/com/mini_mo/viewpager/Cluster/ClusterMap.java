package com.mini_mo.viewpager.Cluster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
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
import com.mini_mo.viewpager.MainPageFragment;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.ReadAndWrite.AddressTransformation;
import com.mini_mo.viewpager.ReadAndWrite.ReadActivity;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ClusterMap extends AppCompatActivity
        implements OnMapReadyCallback,View.OnClickListener {

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;
    Marker customMarker=null;

    private static final String TAG = "googlemap_example";
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    private ClusterManager<MyItem> mClusterManager = null;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    boolean isSetClusteringListener= false;
    LatLng savePoint;
    ArrayList<MarkerItem> sampleList = new ArrayList();
    float zoomLevel = 16;

    ImageView ok, nowlocation, cancel, mapmenu,store_image;
    TextView textView,store_name,store_status;
    View marker_root_view;

    ArrayList<ListViewItemData> clustericon;
    MarkerOptions marker = new MarkerOptions();

    @SuppressLint("RestrictedApi")
    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    public ClusterMap() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.cluster_map);

        ok = (ImageView) findViewById(R.id.ok);
        nowlocation = (ImageView) findViewById(R.id.nowlocation);
        cancel = (ImageView) findViewById(R.id.cancel);
        mapmenu = (ImageView) findViewById(R.id.mapmenu);

        textView = (TextView) findViewById(R.id.textView);

        ok.setOnClickListener(this);
        nowlocation.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mapmenu.setOnClickListener(this);

        marker_root_view = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
        store_name = (TextView)marker_root_view.findViewById(R.id.store_name);
        store_status = (TextView)marker_root_view.findViewById(R.id.store_status);
        store_image = (ImageView)marker_root_view.findViewById(R.id.store_iamge);

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
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
                startActivity(intent);
                finish();
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
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        mClusterManager = new ClusterManager<>(this, mGoogleMap);
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                getVisibleRegion();
                mGoogleMap.setOnCameraIdleListener(mClusterManager);
            }
        });

    }

    //디바이스에 출력되는 지도 범위
    public void getVisibleRegion() {

        mClusterManager.clearItems();

        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        Log.d("TEST", bounds.toString());
        double max_lat, max_lng, min_lat, min_lng;
        max_lat = bounds.northeast.latitude;
        max_lng = bounds.northeast.longitude;
        min_lat = bounds.southwest.latitude;
        min_lng = bounds.southwest.longitude;


        Data data = new Data();
        try {
            clustericon = data.read_board_list(min_lat, min_lng, max_lat, max_lng);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mGoogleMap.getCameraPosition().zoom >= 19) {
            mClusterManager.clearItems();
            for (int i = 0; i < clustericon.size(); i++) {
                double lat = clustericon.get(i).latitude;
                double lng = clustericon.get(i).longitude;
                if (mGoogleMap.getCameraPosition().zoom > 19) {
                    sampleList.add(new MarkerItem(lat, lng, clustericon.get(i).content,clustericon.get(i).user_id,clustericon.get(i).user_photo));
                }
            }
            getSampleMarkerItems(sampleList);
        }
         else {
            if(customMarker != null)
            mGoogleMap.clear();
            for (int i = 0; i < clustericon.size(); i++) {
                double lat = clustericon.get(i).latitude;
                double lng = clustericon.get(i).longitude;
                mClusterManager.getClusterMarkerCollection().clear();
                MyItem myItem = new MyItem(lat, lng);
                mClusterManager.addItem(myItem);
            }
        }

    }
    private void getSampleMarkerItems(ArrayList<MarkerItem> sampleList) {

        for (MarkerItem markerItem : sampleList) {
            addMarker(markerItem, false);
        }

    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {


        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        String status = markerItem.getStatus();
        String name = markerItem.getName();

        store_status.setText(status.toString());
        store_name.setText(name.toString());

        store_status.setTextColor(Color.BLACK);
        store_name.setTextColor(Color.BLACK);

        Glide.with(this)
                .load( markerItem.getImage())
                .apply( new RequestOptions().override(50,50).placeholder(R.drawable.noimg).error(R.drawable.noimg))
                .into(store_image);


        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
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


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정 fix - 2017. 11.27

        currentMarker = mGoogleMap.addMarker(markerOptions);


        if (mMoveMapByAPI) {

            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.latitude + " " + location.longitude);
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(location);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }


    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:

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
                textView.setText(AddressTransformation.getAddress(this, savePoint.latitude, savePoint.longitude));
                break;

            case R.id.mapmenu:

                break;


        }
    }
}