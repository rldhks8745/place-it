package com.mini_mo.viewpager.Camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.mini_mo.viewpager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CustomGoogleMap extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener{

    /*
            카메라 센서
     */
    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor sensor;
    TextView t;
    TextView otherPoint;
    int x, y, z;

    /*
            구글 MAP
     */

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    public static final String TAG = "CameraActivity";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    public static final int UPDATE_INTERVAL_MS = 100;  // 0.1초
    public static final int FASTEST_UPDATE_INTERVAL_MS = 10; // 0.01초
    public static final int CAMERA_ZOOM_IN = 20; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng mCurrentPosition;

    // 메인엑티비티에 보낼 위도,경도, 거리
    ArrayList<String> marrLatMarker;
    ArrayList<String> marrLonMarker;

    @SuppressLint("RestrictedApi")
    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView( R.layout.camera_google_map );

        marrLatMarker = new ArrayList<String>();
        marrLonMarker = new ArrayList<String>();

        Intent intent = getIntent();
        double startLat = intent.getDoubleExtra("lat",37.56);
        double startLon = intent.getDoubleExtra("lon",126.97);
        mCurrentPosition =  new LatLng(startLat, startLon);
        Log.d(TAG, "onCreate");
        mActivity = this;


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
                카메라 센서 초기화
         */
        t = (TextView)findViewById(R.id.textView2);
        otherPoint = (TextView)findViewById(R.id.textView3);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                x = (int)sensorEvent.values[0]; // Roll ( 무조건 수평 )
                y = (int)sensorEvent.values[1]; // picth ( 무조건 수직 )
                z = (int)sensorEvent.values[2]; // 카메라 기울기

                t.setText("x: " + x + "y: " + y + "z: " + z );
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // 마커찍은거 카메라로 보내기
                Intent intent = getIntent();
                intent.putStringArrayListExtra("arrLat", marrLatMarker );
                intent.putStringArrayListExtra("arrLon", marrLonMarker );
                setResult( RESULT_OK, intent );
                finish();
            }
        } );
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onResume() {

        super.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }

        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }

        /*
                카메라 센서
         */
        if( sensorManager != null )
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }



    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }


    /*
         onMapReady();
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();


        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        /*
                OnMapClick
         */
        mGoogleMap.setOnMapClickListener( new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions
                        .position( latLng )
                        .title("마커");

                mGoogleMap.addMarker( markerOptions );

                marrLatMarker.add( "" + latLng.latitude );
                marrLonMarker.add( "" + latLng.longitude);

                Log.d( TAG, "onMapClick : " + "찍은 위도 : " + latLng.latitude + "경도 : " + latLng.longitude + "ArrayList 갯수 : " + marrLatMarker.size() );
                Log.d( TAG, "onMapClick : " + "현재 위치에서 거리 : " + SphericalUtil.computeDistanceBetween( mCurrentPosition, latLng ));
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int reason ) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMoveStarted : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {

                //Log.d(TAG, "onCameraMove : 카메라 사용자가 움직이는 중");
            }
        });

        // 마커클릭 시
        mGoogleMap.setOnMarkerClickListener(this);

    }


    @Override
    public void onLocationChanged(Location location) {

        mCurrentPosition = new LatLng( location.getLatitude(), location.getLongitude());

        TextView textView3 = (TextView)findViewById(R.id.textView3);
        textView3.setText( "현재위치 \n위도 : " + mCurrentPosition.latitude + "\n경도 : " + mCurrentPosition.longitude );

        Log.d(TAG, "CustomGPAonLocationChanged : " + "현재위도:" + String.valueOf( location.getLatitude() ) + " 현재경도:" + String.valueOf( location.getLongitude() ) );

        String markerTitle = getCurrentAddress(mCurrentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);
    }


    @Override
    protected void onStart() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        Log.d(TAG, "onStop : ");

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
                카메라 센서
         */
        if( sensorManager != null )
            sensorManager.unregisterListener(listener);

        Log.d(TAG, "onPause : ");
    }

    @Override
    public void onConnected(Bundle connectionHint) {


        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            //Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            Log.d( TAG, "지오코더 서비스 사용불가" );

            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정 fix - 2017. 11.27
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        currentMarker = mGoogleMap.addMarker(markerOptions);


        if ( mMoveMapByAPI ) {

            mCurrentLocatiion = location;
            float bearing = x;
            float tilt = y;

            if( tilt > 0 ) {

                if( tilt > 90 )
                    tilt = 90.f;
            }
            else if( tilt < 0 ) {
                tilt *= -1.0f;

                if( tilt > 90 )
                    tilt = 90.0f;
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target( mCurrentPosition )
                    .zoom( CAMERA_ZOOM_IN )
                    .bearing( bearing )
                    .tilt( tilt )
                    .build();

            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition( cameraPosition ));

            TextView textView = (TextView)findViewById(R.id.textView);
            textView.setText( "bearing : " + bearing + " tilt : " + tilt  );
        }
    }

    /*
        현재 위치를 GPS로 받아오지 못할 경우 Default Location 값
     */
    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = mCurrentPosition;
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( DEFAULT_LOCATION );
        markerOptions.title( markerTitle );
        markerOptions.snippet( markerSnippet );
        markerOptions.draggable( true );
        markerOptions.icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_RED ) );
        currentMarker = mGoogleMap.addMarker( markerOptions );

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {
                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }
            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하세요");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");

                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }
                break;
        }
    }

    /*************************
     *      구글지도에 마커 클릭 시
     *************************/
    @Override
    public boolean onMarkerClick(Marker marker) {

        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;

        Log.d( TAG, "onActivityResult : 마커 클릭 함 ");
        Log.d( TAG, "클릭한 마커 위도 : " + lat + " 경도 : " + lon);
        otherPoint.setText("클릭한 마커 \n위도 : " + lat + " \n경도 : " + lon + "\n현재위치에서 거리 : " + + SphericalUtil.computeDistanceBetween( mCurrentPosition, marker.getPosition() ) );

        return true;
    }
}