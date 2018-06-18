package com.mini_mo.viewpager.Camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.mini_mo.viewpager.DAO.Board_Location;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.SearchActivity;
import com.mini_mo.viewpager.SearchListViewAdapter;
import com.mini_mo.viewpager.SearchListViewItem;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by userForGame on 2018-04-03.
 */

public class CustomGPS extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int ANGLE = 60; // 카메라 화각 좌우 합쳐서 90도

    public static final String TAG = "CameraActivity";
    public static final int GPS_ENABLE_REQUEST_CODE = 2001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 100;  // 0.1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 10; // 0.01초

    private CameraActivity m_cameraActivity;

    public boolean mRequestingLocationUpdates = false;

    protected LocationManager m_locationManager;

    @SuppressLint("RestrictedApi")
    public  LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    public LatLng mCurrentPosition;
    public GoogleApiClient mGoogleApiClient;

    public LoadingDialog loadingDialog; // 로딩중 다이얼로그

    /***** 생성자 ****/
    public CustomGPS( CameraActivity cameraActivity ) {
        m_cameraActivity = cameraActivity;

        loadingDialog = new LoadingDialog();

        // locationManager 가져오기
        m_locationManager = (LocationManager) m_cameraActivity.getSystemService(LOCATION_SERVICE);

        if( mGoogleApiClient == null ) {
            mGoogleApiClient = new GoogleApiClient.Builder( m_cameraActivity )
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    public void onConnectGoogleApiClient()
    {
        // 구글APi Client 커넥트
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d( TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }
    }

    public void onStart() {

        // 커넷트 되어 있으면
        if ( mGoogleApiClient.isConnected() ) {
            Log.d( TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }
        else
        {
            mGoogleApiClient.connect();
        }
    }

    public void onStop()
    {
        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d( TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentPosition = new LatLng( location.getLatitude(), location.getLongitude());
        // 디비에서 가져온다.
        try
        {
            if( !SearchListViewAdapter.getInstance().isShowing )
            {
                CameraActivity.getInstance().mReadComments = new Data().read_board_location(mCurrentPosition.latitude - 0.0005,
                        mCurrentPosition.latitude + 0.0005,
                        mCurrentPosition.longitude - 0.0005,
                        mCurrentPosition.longitude + 0.0005);
            }
            else
            {
                CameraActivity.getInstance().mReadComments = new ArrayList<Board_Location>();
                ArrayList<SearchListViewItem> items = SearchListViewAdapter.getInstance().searchListViewItems;
                for( int i=0; i<items.size(); i++)
                {
                    CameraActivity.getInstance().mReadComments.add( new Board_Location( items.get(i).latitude, items.get(i).longitude, 1 ));
                }
            }

            // 현재위치 갱신했으니 코멘트들의 위치도 갱신하자.
            if (  mCurrentPosition != null )
            {
                CameraActivity.getInstance().calculateComentsPosition(); // 현재위치를 기반으로 코멘트들의 거리를 계속 계산해줘야한다.
            }
            // 좌표값 받아왔으니 로딩화면 중지
            stopLocationUpdates();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        loadingDialog.progressOFF();
    }

    public void isInCamera()
    {
        // 코멘트들이 생성 되었다면
        if(  CustomMapView.getInstance().mComments != null )
        {
            CustomMapView customMapView = CustomMapView.getInstance();
            CommentVector2 comment;

            Vector2 dest = new Vector2(); // 코멘트 위치
            for(int i = 0; i < customMapView.mComments.size(); i++ ) // 코멘트가 카메라안에 있는지 검사한다.
            {
                comment = customMapView.mComments.get(i);
                dest.setX( comment.mvecAbsolutePosition.x );
                dest.setY( comment.mvecAbsolutePosition.y );

                dest.x -= mCurrentPosition.longitude; // 현재위치에서 코멘트 사이의 벡터
                dest.y -= mCurrentPosition.latitude;

                dest.normalize(); // 노멀라이징

                /****************** 코멘트의 좌우각도 교차지점 ******************/
                Vector2 comentCurPosition = new Vector2(dest.x * customMapView.mComments.get(i).mDistance, dest.y * customMapView.mComments.get(i).mDistance);// 코멘트 위치 평면화
                double length = comentCurPosition.getLength(); // 코멘트 길이
                Vector2 comentNormal = Vector2.normalize(new Vector2(dest.x * customMapView.mComments.get(i).mDistance, dest.y * customMapView.mComments.get(i).mDistance)); // 코멘트 노말화

                Vector2 left = new Vector2(customMapView.mvecLeftAngle.x * 50, customMapView.mvecLeftAngle.y * 50); // left 앵글 실제 거리로 변환
                Vector2 right = new Vector2(customMapView.mvecRightAngle.x * 50, customMapView.mvecRightAngle.y * 50); // left 앵글 실제 거리로 변환

                //double a = Vector2.multiply( customView.mvecLeftAngle, dirFromComentToCurPosition);
                // 평면 방정식 + 직선방정식 ( 평면,직선이 원점을 기준이면 ), t = -D / N * P,            D = 원점으로부터 평면이 떨어진 길이, N = 평면 노말벡터, P = 원점시작->직선벡터
                double leftRatio = (-(length) / (Vector2.multiply(customMapView.mvecLeftAngle, new Vector2(comentNormal.x*-1.0, comentNormal.y*-1.0 )))) / 50; // left앵글 직선의 몇% 위치에 현재위치가 있나? 0 ~ 1
                double rightRatio = (-(length) / (Vector2.multiply(customMapView.mvecRightAngle, new Vector2(comentNormal.x*-1.0, comentNormal.y*-1.0 )))) / 50;

                Vector2 intersectLeft = new Vector2(left.x * leftRatio, left.y * leftRatio); // 실제 코멘트평면이 left앵글의 어디지점에 있는가?
                Vector2 intersectRIght = new Vector2(right.x * rightRatio, right.y * rightRatio); // 실제 코멘트평면이 right앵글의 어디지점에 있는가?

                // 앵글 안에 시작점 ~ 끝점 사이에 있는 코멘트 위치를 길이값으로 비교해서 안에 있나 판단
                double totalLength = new Vector2(intersectRIght.x - intersectLeft.x, intersectRIght.y - intersectLeft.y).getLength();
                double curLength = new Vector2(comentCurPosition.x - intersectLeft.x, comentCurPosition.y - intersectLeft.y).getLength();

                // 앵글 안에 들어가있으면
                double leftIn = Vector2.multiply( customMapView.mvecLeftAngleNormal, comentNormal );
                double rightIn = Vector2.multiply( customMapView.mvecRightAngleNormal, comentNormal );

                if (leftIn >= 0.0 && rightIn >= 0.0 )
                {
                    double ratio = curLength / totalLength; // 왼쪽앵글직선 ~ 오른쪽앵글직선 사이에 몇 %에 있냐
                    customMapView.mComments.get(i).mIsinCamera = true;
                    customMapView.mComments.get(i).mScreenWIdthRatio = ratio;
                }
                else
                {
                    customMapView.mComments.get(i).mIsinCamera = false;
                    customMapView.mComments.get(i).mScreenWIdthRatio = 2;
                }
            }
        }
    }

    /***********************************
     *      카메라 방향, 좌각도, 우각도, 좌각도노말벡터, 우각도노말벡터 구하기
     **********************************/
    public void getDirection(CustomMapView customMapView)
    {
        int phoneAngle = CameraActivity.getInstance().sensorX;
        double toRadian = Math.PI / 180;

        // 카메라 방향
        customMapView.mvecDirection.setX( Math.sin( phoneAngle * toRadian ) );
        customMapView.mvecDirection.setY( Math.cos( phoneAngle * toRadian ) );

        // 카메라 왼쪽 (-30도) 방향 구하기
        customMapView.mvecLeftAngle.setX( Math.sin( adjustAngle( phoneAngle - CustomGPS.ANGLE/2 ) * toRadian ) );
        customMapView.mvecLeftAngle.setY( Math.cos( adjustAngle( phoneAngle - CustomGPS.ANGLE/2 ) * toRadian ) );

        // 카메라 오른쪽 (+30도) 방향 구하기
        customMapView.mvecRightAngle.setX( Math.sin( adjustAngle( phoneAngle + CustomGPS.ANGLE/2 ) * toRadian ) );
        customMapView.mvecRightAngle.setY( Math.cos( adjustAngle( phoneAngle + CustomGPS.ANGLE/2 ) * toRadian ) );

        // 카메라 왼쪽 (-30도) 방향 노멀벡터
        customMapView.mvecLeftAngleNormal.setX( Math.sin( adjustAngle( m_cameraActivity.sensorX + (90 - CustomGPS.ANGLE/2) ) * toRadian ) );
        customMapView.mvecLeftAngleNormal.setY( Math.cos( adjustAngle( m_cameraActivity.sensorX + (90 - CustomGPS.ANGLE/2) ) * toRadian ) );

        // 카메라 오른쪽 (+30도) 방향 노멀벡터
        customMapView.mvecRightAngleNormal.setX( Math.sin( adjustAngle( m_cameraActivity.sensorX - (90 - CustomGPS.ANGLE/2) ) * toRadian ) );
        customMapView.mvecRightAngleNormal.setY( Math.cos( adjustAngle( m_cameraActivity.sensorX - (90 - CustomGPS.ANGLE/2) ) * toRadian ) );
    }

    // 각도를 0 ~ 360 사이의 값만 가지게 하려고
    public int adjustAngle( int degree )
    {
        if( degree > 360 )
            return degree - 360;
        else if ( degree < 0 )
            return degree + 360;

        return degree;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager)m_cameraActivity.getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void startLocationUpdates() {

        // 위치 기능 On 돼있나?
        if (!checkLocationServicesStatus()) // 안돼 있으면
        {
            showDialogForLocationServiceSetting();
        }
        else // 돼있으면
        {
            if (ActivityCompat.checkSelfPermission( m_cameraActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission( m_cameraActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            // 현재위치 받아오기 전에 로딩화면 띄우기
            loadingDialog.progressON( CameraActivity.getInstance(), "위치 로딩중..." );

            LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if ( mRequestingLocationUpdates == false )
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                // GPS 퍼미션 허락되어 있지 않으면
                if (ContextCompat.checkSelfPermission( m_cameraActivity, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_DENIED)
                {
                    ActivityCompat.requestPermissions( m_cameraActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                else
                {
                    startLocationUpdates();
                }

            }
            else
            {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d( CustomGoogleMap.TAG, "onConnectionFailed");
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d( TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e( TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e( TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    public void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.getInstance() );
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하세요");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                m_cameraActivity.startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                m_cameraActivity.finish();
            }
        });
        builder.create().show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.getInstance());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions( m_cameraActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                m_cameraActivity.finish();
            }
        });
        builder.create().show();
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissions()
    {
        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
    }

}