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

    public ArrayList<Board_Location> mReadComments;

    public static CameraActivity getInstance(){ return instance; }

    public CameraActivity() {
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_camera); // 메인 에티비티 붙이기.

        /** 코멘트 표시할 맵 **/
        mCustomMapView = (CustomMapView)findViewById(R.id.mapView);
        mCommentView = (CommentView)findViewById(R.id.comentView);
        // 해당 코멘트를 클릭하면?
        mCommentView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                CustomMapView customMapView = CustomMapView.getInstance();

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        if( customMapView.mComments != null ) {

                            for (int i = 0; i < customMapView.mComments.size(); i++) {
                                CommentVector2 comment = customMapView.mComments.get(i);

                                // 카메라 안에 있는 녀석들만 검사
                                if( comment.mIsinCamera ) {
                                    // 코멘트를 클릭했으면
                                    if ((comment.mvecScreenPos.x - comment.radius <= event.getX()) && (comment.mvecScreenPos.x + comment.radius >= event.getX()) &&
                                            (comment.mvecScreenPos.y - comment.radius <= event.getY()) && (comment.mvecScreenPos.y + comment.radius >= event.getY())) {

                                        Intent intent = new Intent( CameraActivity.getInstance(), CameraCommentsList.class );
                                        intent.putExtra("lat", comment.mvecAbsolutePosition.y);
                                        intent.putExtra("lon", comment.mvecAbsolutePosition.x);
                                        startActivity(intent);
                                        /*

                                        여기다가
                                        comment.mvecAbsolutePosition.y (위도), comment.mvecAbsolutePosition.x (경도)인 모든 코멘트 찾아서

                                         */
                                        Toast.makeText(CameraActivity.getInstance(), "클릭한 코멘트 개수 : " + comment.mCount, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE :
                        break;
                    case MotionEvent.ACTION_UP   :
                        break;
                }
                return true;
            }
        });

        /************************************
         *          구글, 현재위치 버튼 누를 시
         ************************************/
        ImageView imgVGoogleMap = (ImageView)findViewById( R.id.googleMap );
        imgVGoogleMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // 지도메뉴 누르면 구글지도 띄우기
                Intent intent = new Intent( CameraActivity.getInstance(), CustomGoogleMap.class );
                intent.putExtra("lat", ( m_customGPS.mCurrentPosition != null) ? m_customGPS.mCurrentPosition.latitude : 37.56 ); // 위도값 전달
                intent.putExtra("lon",  ( m_customGPS.mCurrentPosition != null) ? m_customGPS.mCurrentPosition.longitude : 126.97 ); // 경도값 전달
                startActivityForResult(intent, GOOGLE_MAP_RESULT);
            }
        });

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE); // 카메라 퍼미션 요청
        }
        else if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CustomGPS.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION); // GPS 퍼미션 요청
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
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                    sensorManager.registerListener( sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

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

    /*********************
     *  맵뷰에서 사용할 코멘트 위치, 거리
     **********************/
    public void calculateComentsPosition()
    {
        // 코멘트 벡터 생성
        ArrayList<CommentVector2> comments = new ArrayList<>();

        for( int i=0; i < mReadComments.size(); i++)
        {
            double lon = mReadComments.get(i).longitude; // x좌표, 경도
            double lat = mReadComments.get(i).latitude; // y좌표, 위도
            int count = mReadComments.get(i).board_count;


            // 코멘트와 현재위치 거리
            double distance = SphericalUtil.computeDistanceBetween( m_customGPS.mCurrentPosition, new LatLng( lat, lon ) );

            // 코멘트 위치 구하기. ( 벡터를 만들때는 lon(경도) = x, lat(위도) = y )
            Vector2 commentVector2 = new Vector2( lon , lat );

            // 코멘트, 현재위치를 기준으로 상대위치 구하기
            double relX = commentVector2.x - m_customGPS.mCurrentPosition.longitude;
            double relY = commentVector2.y - m_customGPS.mCurrentPosition.latitude;

            // 코멘트 벡터 추가
            comments.add( new CommentVector2( new Vector2( lon, lat ), new Vector2( relX, relY ), distance, count ) );
            comments.get(i).mvecRelativePosition.normalize(); // 노멀화
        }

        // 맵뷰에서 사용할 수 있도록 넘기기
        Collections.sort( comments ); // 내림차순 정렬 ( 가까운 순으로 )
        mCustomMapView.mComments = comments;

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