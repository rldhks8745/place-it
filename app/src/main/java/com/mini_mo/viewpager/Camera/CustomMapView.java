package com.mini_mo.viewpager.Camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Vector;


/**
 * Created by userForGame on 2018-04-19.
 */

/***************************
 *  센터 100, 100
 *  좌우 100px 씩 남는다.
 *  100px = 50m로 계산해서, 거리 = 픽셀 * 2
 ***************************/

public class CustomMapView extends View
{
    // CustomView 멤버변수
    public static CustomMapView instance = null;

    public static final float COMENT_DISTANCE = 50;
    public static final float DIR_DISTANCE = 100;
    public static final float CENTER_X = 100.0f;
    public static final float CENTER_Y = 100.0f;
    public static final float CANVAS_WIDTH = 200;
    public static final float CANVAS_HEIGHT = 200;

    public ArrayList<CommentVector2> mComments = null; // 화면에 보일 코멘트 위치정보
    public Vector2 mvecLeftAngle = null; // 왼쪽각도 벡터
    public Vector2 mvecLeftAngleNormal = null; // 왼쪽각도 법선벡터
    public Vector2 mvecRightAngle = null; // 오른쪽각도 벡터
    public Vector2 mvecRightAngleNormal = null; // 오른쪽각도 법선벡터
    public Vector2 mvecDirection = null; // 현재위치에서 바라보는 방향 벡터

    // 맵뷰 화면 px 과 나타낼 거리 간의 비율
    // 중앙 에서 맵뷰 끝까지의 px = 100px
    // 100 / 50 = 2, 1m 당 2px 의 비율
    public  float ratio = ( CANVAS_WIDTH / 2 ) / COMENT_DISTANCE;;

    public static CustomMapView getInstance(){ return instance; }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        instance = this;

        // 코멘트, 방향 각도 생성
        mvecDirection = new Vector2( 0.0f, 0.0f );
        mvecLeftAngle = new Vector2( 0.0f, 0.0f );
        mvecLeftAngleNormal = new Vector2( 0.0f, 0.0f );
        mvecRightAngle = new Vector2( 0.0f, 0.0f );
        mvecRightAngleNormal = new Vector2( 0.0f, 0.0f );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CameraActivity mainActivity = CameraActivity.getInstance();

        /********* 현재위치 표시 ********/
        Paint myPaint = new Paint();

        myPaint.setStrokeWidth( 1.0f );
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        myPaint.setColor(Color.YELLOW);
        // 현재 위치에서 50m 바운더리
        canvas.drawCircle( CENTER_X, CENTER_Y,100.0f, myPaint );

        // 현재 위치
        myPaint.setStrokeWidth( 5.0f );
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        myPaint.setColor(Color.RED);
        canvas.drawCircle( CENTER_X, CENTER_Y,10.0f, myPaint );

        /*********** 코멘트 위치 표시 ************/
        myPaint.setColor(Color.BLUE);
        if( mComments != null )
        {
            for( int i=0; i < mComments.size(); i++ )
            {
                // 코멘트 위치 뿌리기  ( 현재는 COMENT_DISTANCE( 50m ) 안에 있을 경우 && 카메라안에 있을 경우에만 뜬다 )
                if( mComments.get(i).mDistance <= COMENT_DISTANCE ) {
                    canvas.drawCircle( (float)( CENTER_X + ( mComments.get(i).mvecRelativePosition.x * mComments.get(i).mDistance * ratio ) ),
                            (float)( CENTER_Y + -1 * ( mComments.get(i).mvecRelativePosition.y * mComments.get(i).mDistance * ratio ) ),
                            5.0f,
                            myPaint);
                    Log.d("Canvas", "위치 x : " + (float)( CENTER_X + ( mComments.get(i).mvecRelativePosition.x * mComments.get(i).mDistance * ratio ) ) +
                            " y : " +  (float)( CENTER_Y + -1 * ( mComments.get(i).mvecRelativePosition.y * mComments.get(i).mDistance * ratio ) ) );
                }
            }
        }

        /*********** 현재위치에서 방향, 각도 표시 ************/
        myPaint.setStrokeWidth( 1.0f );
        myPaint.setStyle(Paint.Style.STROKE);

        // 왼쪽 angle
        Path leftPath = new Path();
        leftPath.moveTo( CENTER_X,CENTER_Y );
        leftPath.lineTo( CENTER_X + (float)( mvecLeftAngle.x * DIR_DISTANCE ),  CENTER_Y + (float)( -1 * mvecLeftAngle.y * DIR_DISTANCE ) );
        leftPath.close();

        // 오른쪽 right
        Path rightPath = new Path();
        rightPath.moveTo( CENTER_X,CENTER_Y );
        rightPath.lineTo( CENTER_X + (float)( mvecRightAngle.x * DIR_DISTANCE ), CENTER_Y + (float)( -1 * mvecRightAngle.y * DIR_DISTANCE ) );
        rightPath.close();

        canvas.drawPath( leftPath, myPaint );
        canvas.drawPath( rightPath, myPaint );

    }
}