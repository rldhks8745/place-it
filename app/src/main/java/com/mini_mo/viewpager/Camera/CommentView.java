package com.mini_mo.viewpager.Camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;

import java.util.Collections;


/**
 * Created by userForGame on 2018-04-25.
 */

public class CommentView extends View
{
    private CameraActivity mCameraActivity;

    public int mScreenWIdth, mScreenHeight; // 핸드폰 화면 사이즈

    public  Bitmap mCommentImage = null;

    public CommentView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        Display display = MainActivity.getInstance().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize( size );
        mScreenWIdth = size.x; // 코멘트 표시될때 화면 넘어서까지 부드럽게 이동하기 위해서
        mScreenHeight = size.y;

        mCameraActivity = CameraActivity.getInstance();

        Resources r = context.getResources();
        mCommentImage = BitmapFactory.decodeResource(r, R.drawable.comment );

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        CustomMapView customMapView = CustomMapView.getInstance();

        /*********** 코멘트 위치 표시 ************/
        if( customMapView != null && customMapView.mComments != null )
        {
            Paint myPaint = new Paint();

            myPaint.setStrokeWidth( 1.0f );
            myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            myPaint.setColor(Color.RED);

            for( int i=0; i < customMapView.mComments.size(); i++ )
            {
                CommentVector2 comment = customMapView.mComments.get(i);
                if( comment.mIsinCamera && ( comment.mDistance <= CustomMapView.COMENT_DISTANCE ) )
                {
                    float widthRatio = (float) customMapView.mComments.get(i).mScreenWIdthRatio;
                    float radius = (float) ( ( 1 / customMapView.mComments.get(i).mDistance ) );
                    float x = (widthRatio != 0.0) ? (float) (mScreenWIdth * customMapView.mComments.get(i).mScreenWIdthRatio) : -100.0f;

                    // 삼중연산자
                    float y = ( mCameraActivity.sensorY <= -( 90 - 45 ) && mCameraActivity.sensorY >= -( 90 + 45 ) ) ?
                            ( ( (float) -( mCameraActivity.sensorY + 45 ) / 90 ) * mScreenHeight ) + ( float )( customMapView.mComments.get(i).mDistance * 2 ) :
                            -100;

                    canvas.drawCircle( x, y, ( mCommentImage.getWidth() * 5 * radius ) / 2, myPaint ); // 색상 표시

                    Bitmap bitComment = mCommentImage.createScaledBitmap( mCommentImage, (int)( mCommentImage.getWidth() * 5 * radius ),(int)( mCommentImage.getWidth() * 5 * radius ), true);
                    canvas.drawBitmap( bitComment, x - (bitComment.getWidth()/2), y - (bitComment.getHeight()/2), myPaint ); // 실제 코멘트 이미지 표시

                    /** 코멘트 위치 수정 **/
                    comment.mvecScreenPos.x = x;
                    comment.mvecScreenPos.y = y;
                    comment.radius = mCommentImage.getWidth() * 5 * radius;
                }
            }
        }
    }
}
