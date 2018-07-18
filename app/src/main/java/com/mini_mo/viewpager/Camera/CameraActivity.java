package com.mini_mo.viewpager.Camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.mini_mo.viewpager.DAO.Board_Location;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import static com.mini_mo.viewpager.Camera.CustomCamera.CAMERA_PERMISSION_REQUEST_CODE;


public class CameraActivity extends AppCompatActivity {

    public static CameraActivity instance = null;

    private static final int GOOGLE_MAP_RESULT = 1;

    // 카메라 클래스
    public CustomCamera m_Custom_Camera;
    public CustomGPS m_customGPS;

    public  long lastTime = System.currentTimeMillis();

    /***********************
     카메라 센서
     *************************/
    public SensorManager sensorManager;
    public SensorEventListener sensorListener;
    public Sensor sensor;
    public int sensorX, sensorY, sensorZ;

    /***********************
     *      DB로부터 받은 마커값
     ***********************/

    public ArrayList<Double> marrLatMarker = null;
    public  ArrayList<Double> marrLonMarker = null;

    /*******************************
     *      코멘트 표시할 맵
     ****************************/
    public  CustomMapView mCustomMapView; // 오른쪽 위 작은 맵뷰
    public  CommentView mCommentView; // 화면 전체 코멘트 표시할 뷰

    // 카메라 코멘트 Layout을 띄울 상위 레이아웃
    public ConstraintLayout constraintLayout;

    public static CameraActivity getInstance(){ return instance; }

    public CameraActivity() {
        instance = this;
    }

    public int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_camera); // 메인 에티비티 붙이기.

        /** 코멘트 표시할 맵 **/
        mCustomMapView = (CustomMapView)findViewById(R.id.mapView);
        mCommentView = (CommentView)findViewById(R.id.comentView);

        /************************************
         *        현재위치 버튼 누를 시
         ************************************/

        ImageView imgVcurLocation = (ImageView)findViewById( R.id.curLocation );
        imgVcurLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                m_customGPS.startLocationUpdates();
            }
        });

        // 카메라 객체 생성
        m_Custom_Camera = new CustomCamera(this, (SurfaceView) findViewById(R.id.cameraView));
        m_customGPS = new CustomGPS(this ); // GPS 객체 생성

        // 카메라 퍼미션 허용 안돼있으면
        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE ); // 카메라 퍼미션 요청
        }
        else if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CustomGPS.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ); // GPS 퍼미션 요청
        }

        /**************************************
                    카메라 센서 초기화
         **************************************/

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        /*****************************
         *            센서
         *****************************/
        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                sensorX = (int) sensorEvent.values[0];
                sensorY = (int) sensorEvent.values[1]; // picth ( 무조건 수직 )
                sensorZ = (int) sensorEvent.values[2]; // 카메라 기울기

                m_customGPS.getDirection(mCustomMapView); // 방향 생성
                m_customGPS.isInCamera(); // 생성된 방향으로 코멘트들이 카메라에 있는가? ( 현재는 0번째 인덱스만 검사 중 )

                // 커스텀 뷰들 재 그리기
                CustomMapView.getInstance().invalidate();
                mCommentView.invalidate();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        constraintLayout = (ConstraintLayout)findViewById(R.id.conslayout);

        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {
            if( m_customGPS.checkLocationServicesStatus() )
            {
                m_customGPS.onStart();
            }
            else
            {
                m_customGPS.showDialogForLocationServiceSetting();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (m_Custom_Camera != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                m_Custom_Camera.showCamera();

                /************
                 카메라 센서
                 ************/
                if( sensorManager != null )
                    sensorManager.registerListener( sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL );

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        /************
         카메라 센서
         ************/
        if( sensorManager != null )
            sensorManager.unregisterListener( sensorListener, sensor );
    }

    @Override
    protected void onStop() {
        super.onStop();

        m_customGPS.onStop();
    }

    /***********************************
     *     액티비티 이동 후 돌아 온 뒤
     **********************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch( requestCode )
        {
            case GOOGLE_MAP_RESULT:
                // 위도, 경도 값받기
                //if( resultCode == RESULT_OK )
                //{

                //}
                break;
            case CustomGPS.GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if ( m_customGPS.checkLocationServicesStatus() ) {

                    if ( !m_customGPS.mGoogleApiClient.isConnected() ) {

                        m_customGPS.mGoogleApiClient.connect();
                    }
                    return;
                }
                else
                {

                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /************************************
     *  사용자가 권한 허용/거부 버튼을 눌렀을 때 호출되는 메서드
     ************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch( requestCode )
        {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if ( m_Custom_Camera.onRequestPermissionsResult( requestCode, permissions, grantResults ) )
                {
                    m_Custom_Camera.showCamera();

                    // 카메라 퍼미션 확인 -> GPS 퍼미션 확인
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  // GPS 퍼미션 거부면
                    {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CustomGPS.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                    else // GPS 퍼미션 허용이면
                    {
                        m_customGPS.onConnectGoogleApiClient(); // 커넥션
                    }
                }
                break;
            case CustomGPS.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION :
                if ( grantResults.length > 0) {
                    if ( grantResults[0] == PackageManager.PERMISSION_GRANTED )
                    {
                        if ( !m_customGPS.mGoogleApiClient.isConnected())
                        {
                            m_customGPS.onConnectGoogleApiClient();
                        }
                    } else {
                        m_customGPS.checkPermissions();
                    }
                }
                break;
        }

    }

}