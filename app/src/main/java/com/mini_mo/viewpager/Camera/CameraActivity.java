package com.mini_mo.viewpager.Camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.mini_mo.viewpager.R;

import java.util.ArrayList;

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

    // 카메라 코멘트 Layout을 띄울 상위 레이아웃
    public RelativeLayout camera_rel;
    public int mScreenWIdth, mScreenHeight; // 핸드폰 화면 사이즈

    /******************
     * 카메라 줌 값
     ******************/
    public int zoomDIstance = 0;

    public static CameraActivity getInstance(){ return instance; }

    public CameraActivity() {
        instance = this;
    }

    public int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_camera); // 메인 에티비티 붙이기.
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
                m_customGPS.getDirection(); // 방향 생성
                m_customGPS.isInCamera(); // 생성된 방향으로 코멘트들이 카메라에 있는가? ( 현재는 0번째 인덱스만 검사 중 )
                calcCommentPos();

                // 커스텀 뷰들 재 그리기
                if( CustomMapView.getInstance() != null )
                    CustomMapView.getInstance().invalidate();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

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

        /** 줌인,아웃 바 **/
        SeekBar zoomBar = (SeekBar)findViewById(R.id.camera_zoombar);
        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) // progress 가 xml에서 지정한 min ~ max 값
            {
                zoomDIstance = progress;

                if( CustomMapView.getInstance() == null )
                    return;

                ArrayList<CommentVector2> comments = CustomMapView.getInstance().mComments;

                for( int i=0; i<comments.size(); i++ )
                {
                    comments.get(i).reSizeLayout( progress ); // 줌인,아웃 으로 거리값 증가,감소
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        camera_rel = (RelativeLayout)findViewById(R.id.camera_rel); // 코멘트들이 add될 레이아웃

        Display display = CameraActivity.getInstance().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
        mScreenWIdth = size.x; // 코멘트 표시될때 화면 넘어서까지 부드럽게 이동하기 위해서
        mScreenHeight = size.y;
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

    public void calcCommentPos()
    {

        CustomMapView customMapView = CustomMapView.getInstance();
        if( customMapView == null )
            return;

        // 코멘트 위치 표시
        if( customMapView != null && customMapView.mComments != null )
        {
            for( int i=0; i < customMapView.mComments.size(); i++ )
            {
                CommentVector2 comment = customMapView.mComments.get(i);
                if( comment.mIsinCamera && ( comment.mDistance <= CustomMapView.COMENT_DISTANCE ) && ( comment.changedDistance >= 0 ) )
                {
                    if ( !comment.mAddView )
                    {
                        comment.show();
                    }
                    float widthRatio = (float) customMapView.mComments.get(i).mScreenWIdthRatio;
                    float x = (widthRatio != 0.0) ? (float) (mScreenWIdth * customMapView.mComments.get(i).mScreenWIdthRatio) : -100.0f;

                    // 삼중연산자
                    float y = ( sensorY <= -( 90 - 45 ) && sensorY >= -( 90 + 45 ) ) ?
                            ( ( (float) -( sensorY + 45 ) / 90 ) * mScreenHeight ) + ( float )( customMapView.mComments.get(i).mDistance * 2 ) :
                            -100;

                    if( x < -99 || y < -99 )
                    {
                        if ( comment.mAddView )
                        {
                            comment.hide();
                        }
                    }

                    comment.setXY( x - 150, y - 100 );

                    /** 코멘트 위치 수정 **/
                    comment.mvecScreenPos.x = x;
                    comment.mvecScreenPos.y = y;
                }
                else
                {
                    if ( comment.mAddView )
                    {
                        comment.hide();
                    }
                }
            }
        }
    }
}