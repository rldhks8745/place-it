package com.mini_mo.viewpager.Camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import com.mini_mo.viewpager.MainActivity;


/**
 * Created by userForGame on 2018-04-25.
 */

public class CommentView extends View
{
    public static CommentView instance = null;

    private CameraActivity mCameraActivity;

    public int mScreenWIdth, mScreenHeight; // 핸드폰 화면 사이즈

    public static CommentView getInstnce()
    {
        return instance;
    }

    public CommentView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        instance = this;

        Display display = MainActivity.getInstance().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
        mScreenWIdth = size.x; // 코멘트 표시될때 화면 넘어서까지 부드럽게 이동하기 위해서
        mScreenHeight = size.y;

        mCameraActivity = CameraActivity.getInstance();

       // Resources r = context.getResources();
        //mCommentImage = BitmapFactory.decodeResource(r, R.drawable.comment );

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        CustomMapView customMapView = CustomMapView.getInstance();

        // 코멘트 위치 표시
        if( customMapView != null && customMapView.mComments != null )
        {
            for( int i=0; i < customMapView.mComments.size(); i++ )
            {
                CommentVector2 comment = customMapView.mComments.get(i);
                if( comment.mIsinCamera && ( comment.mDistance <= CustomMapView.COMMENT_DISTANCE ) && ( comment.changedDistance >= 0 ) )
                {
                    if ( !comment.mAddView )
                    {
                        comment.show();
                    }
                    float widthRatio = (float) customMapView.mComments.get(i).mScreenWIdthRatio;
                    float radius = (float) ( ( 1 / customMapView.mComments.get(i).mDistance ) );
                    radius = (float)( ( radius >= 1.0 ) ? 1.0 : radius );
                    float x = (widthRatio != 0.0) ? (float) (mScreenWIdth * customMapView.mComments.get(i).mScreenWIdthRatio) : -100.0f;

                    // 삼중연산자
                    float y = ( mCameraActivity.sensorY <= -( 90 - 45 ) && mCameraActivity.sensorY >= -( 90 + 45 ) ) ?
                            ( ( (float) -( mCameraActivity.sensorY + 45 ) / 90 ) * mScreenHeight ) + ( float )( customMapView.mComments.get(i).mDistance * 2 ) :
                            -100;

                    if( x < -99 || y < -99 )
                    {
                        if ( comment.mAddView )
                        {
                            comment.hide();
                        }
                    }

                    comment.setXY( x - (comment.layoutWidth/2), y - (comment.layoutHeight/2) );

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
