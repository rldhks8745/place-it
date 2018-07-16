package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.mini_mo.viewpager.R;

/**
 * Created by Administrator on 2018-05-15.
 */

public class NewImageCrate {

    //새로운 이미지를 만들어주는 클래스 , Bitmap , DrawAble 등

    public static ImageButton ReadnewImageCreate(Activity activity, Bitmap bitmap){
        ImageButton imgv = new ImageButton(activity);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setImageBitmap(bitmap);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imgv.getLayoutParams().height=400;
        imgv.getLayoutParams().width = 400;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static FrameLayout ReadnewVideoCreate(Activity activity, String path){

        VideoView videov = new VideoView(activity);
        ImageView play = new ImageView(activity);
        FrameLayout frameLayout = new FrameLayout(activity);

        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        frameLayout.getLayoutParams().height=400;
        frameLayout.getLayoutParams().width=400;



        play.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        play.setImageResource(R.drawable.playbutton);
        play.setBackgroundColor(Color.alpha(255));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)play.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        play.setLayoutParams(lp);


        videov.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
        videov.setVideoPath(path);
        videov.getLayoutParams().height = 400;
        frameLayout.getLayoutParams().width=400;
        newVideoMargin(videov,40,40,0,50);

        frameLayout.addView(videov);

        return frameLayout;
    }

    public static ImageButton WritenewImageCreate(Activity activity, Bitmap bitmap){
        ImageButton imgv = new ImageButton(activity);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setImageBitmap(bitmap);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imgv.getLayoutParams().height = 300;
        imgv.getLayoutParams().width = 200;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static FrameLayout WritenewVideoCreate(Activity activity, String path){
        VideoView videov = new VideoView(activity);
        ImageView play = new ImageView(activity);
        FrameLayout frameLayout = new FrameLayout(activity);

        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        frameLayout.getLayoutParams().height=300;
        frameLayout.getLayoutParams().width=200;



        play.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        play.setImageResource(R.drawable.playbutton);
        play.setBackgroundColor(Color.alpha(255));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)play.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        play.setLayoutParams(lp);


        videov.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT));
        videov.setVideoPath(path);
        videov.getLayoutParams().height = 300;
        frameLayout.getLayoutParams().width=200;
        newVideoMargin(videov,40,40,0,50);

        frameLayout.addView(videov);

        return frameLayout;
    }

    public static ImageView newImageMargin(ImageView img,int left , int top, int right, int bottom){

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        img.setLayoutParams(new LinearLayout.LayoutParams(margin));

        return img;
    }

    public static void newVideoMargin(VideoView video,int left , int top, int right, int bottom){
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(video.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        video.setLayoutParams(new LinearLayout.LayoutParams(margin));
    }


}
