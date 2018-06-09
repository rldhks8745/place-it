package com.mini_mo.viewpager.Camera;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by 김기완 on 2018-03-31.
 */

public class CustomCamera implements SurfaceHolder.Callback
{
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private CameraActivity m_mainActivity;

    public SurfaceView m_cameraView; // 앱화면 SurfaceView
    public SurfaceHolder m_CameraHolder; // SurfaceView 와 카메라 instance 를 이어주는 holder

    public android.hardware.Camera m_Camera; // 핸드폰 카메라 instance

    public CustomCamera(CameraActivity mainActivity, SurfaceView cameraView )
    {
        m_mainActivity = mainActivity;
        m_cameraView = cameraView;
    }

    public void showCamera()
    {
        // 핸드폰 카메라 얻어오기
        if (m_Camera == null)
        {
            m_Camera = android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
            m_Camera.setDisplayOrientation(90);
        }

        // SurfaceView에 연결될 holder 설정
        if (m_CameraHolder == null)
        {
            m_CameraHolder = m_cameraView.getHolder(); // SurfaceView에서 holder 얻어온다. holder를 통해서 view와 카메라간의 화면 연동이 이루어짐
            m_CameraHolder.addCallback(this); // 콜백함수 등록
            m_CameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        // SurfaceView에 카메라 미리보기 화면( Preview ) 띄우기
        try {
            if (m_Camera != null)
            {
                m_Camera.setPreviewDisplay(m_CameraHolder); // 표시할 holder 정보를 넘겨주교 startPreview()를 함께 해야 바로 화면에 나온다. 중요!!!
                m_Camera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void _showMessagePermission( String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener noListener)
    {
        // 대화창 띄우기 "허용" 클릭 시 okListener 실행
        new android.support.v7.app.AlertDialog.Builder( m_mainActivity )
                .setMessage( message )
                .setPositiveButton("허용", okListener)
                .setNegativeButton("거부", noListener)
                .create()
                .show();
    }

    /**
     * SurfaceView 처음 생성될 때 ( 껏다가 다시켜도 생성됨 )
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) //
    {
        try
        {
            if (m_Camera != null)
            {
                m_Camera.setPreviewDisplay(holder);
                m_Camera.startPreview();
            }
        }
        catch (IOException e) {}
    }

    /**
     * 현재까지 내가 직접 실험해본 바로는 surfaceCreated 호출 후 이어서 호출됨 ( 아마 SurfaceView 화면이 바뀐다던가 size가 바뀐다던가 하면 호출되는것 같음 )
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        // View 를 재생성한다.
        try
        {
            m_Camera.setPreviewDisplay(m_CameraHolder);
            m_Camera.startPreview();
        } catch (Exception e) {}
    }

    /**
     * SurfaceView가 없어질 때 ( 뒤로가기로 나갔다던가 )
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (m_Camera != null) {
            m_Camera.stopPreview();
            m_Camera.release();
            m_Camera = null;
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if( grantResults.length > 0)
        {
            // 권한허가를 받았는가
            boolean cameraAccepted = ( grantResults[0] == PackageManager.PERMISSION_GRANTED );
            if( cameraAccepted )
            {
                return true;
            }
            else
            {
                if( Build.VERSION.SDK_INT >= 23 )
                    if(  m_mainActivity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ) // 권한요청을 실행한적이 있고 이를 거부했을시 true값 반환한다.
                        _showMessagePermission( "권한 허가를 해주셔야 VR카메라 기능을 사용할 수 있습니다.",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        if( Build.VERSION.SDK_INT >= 23 )
                                            m_mainActivity.requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                                    }
                                },

                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        m_mainActivity.finish(); // GPS를 허용하지않으면 끈다
                                    }
                                }
                        ); // 대화창 생성

                return false;
            }
        }

        return false;
    }
}
