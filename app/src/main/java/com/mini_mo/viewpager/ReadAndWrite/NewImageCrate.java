package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.mini_mo.viewpager.Store;

/**
 * Created by Administrator on 2018-05-15.
 */

public class NewImageCrate {

    private final static int SIZE = (int)(Store.display_height/10*1.5);

    //새로운 이미지를 만들어주는 클래스 , Bitmap , DrawAble 등

    public static ImageView ReadnewViewCreate(Activity activity, int height){
        ImageView imgv = new ImageButton(activity);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        imgv.setBackgroundColor(Color.rgb(255,255,255));
        imgv.setLayoutParams(layoutParams);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imgv.getLayoutParams().width = SIZE;

        newImageMargin(imgv,40,40,40,50);

        return imgv;
    }

    public static VideoView ReadnewPlayVideoCreate(Activity activity, String path, int height){

        VideoView videov = new VideoView(activity);

        videov.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams)videov.getLayoutParams();
        lp2.gravity = Gravity.CENTER;
        videov.setLayoutParams(lp2);
        videov.setVideoPath(path);

        videov.getLayoutParams().width= SIZE-50;

        videov.start();

        newPlayVideoMargin(videov,40,40,40,50);

        return videov;
    }


    public static FrameLayout ReadnewVideoCreate(Activity activity, String path, int height){

        VideoView videov = new VideoView(activity);
        ImageView play = new ImageView(activity);
        FrameLayout frameLayout = new FrameLayout(activity);

        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        frameLayout.setBackgroundColor(Color.rgb(0,0,0));

        frameLayout.getLayoutParams().width= SIZE-50;



        play.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        play.setImageResource(R.drawable.videocamera);
        play.setBackgroundColor(Color.alpha(255));

        FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams)play.getLayoutParams();
        lp1.gravity = Gravity.START;
        play.setLayoutParams(lp1);
        play.getLayoutParams().width= SIZE/5;
        play.getLayoutParams().height= SIZE/5;

        videov.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams)videov.getLayoutParams();
        lp2.gravity = Gravity.CENTER;
        videov.setLayoutParams(lp2);
        videov.setVideoPath(path);

        videov.getLayoutParams().width= SIZE-50;

        newVideoMargin(frameLayout,40,40,40,50);

        frameLayout.addView(videov);
        frameLayout.addView(play);

        videov.start();

        return frameLayout;
    }

    public static ImageButton WritenewImageCreate(Activity activity, Bitmap bitmap){
        ImageButton imgv = new ImageButton(activity);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setImageBitmap(bitmap);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imgv.getLayoutParams().width = SIZE;
        imgv.getLayoutParams().height = SIZE;

        newImageMargin(imgv,40,40,40,50);

        return imgv;
    }

    public static FrameLayout WritenewVideoCreate(Activity activity, String path){
        VideoView videov = new VideoView(activity);
        ImageView play = new ImageView(activity);
        FrameLayout frameLayout = new FrameLayout(activity);

        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        frameLayout.setBackgroundColor(Color.rgb(0,0,0));
        frameLayout.getLayoutParams().width= SIZE;
        frameLayout.getLayoutParams().height= SIZE;




        play.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        play.setImageResource(R.drawable.playbutton);
        play.setBackgroundColor(Color.alpha(255));

        FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams)play.getLayoutParams();
        lp1.gravity = Gravity.CENTER;
        play.setLayoutParams(lp1);
        play.getLayoutParams().width= SIZE/2;
        play.getLayoutParams().height= SIZE/2;

        videov.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams)videov.getLayoutParams();
        lp2.gravity = Gravity.CENTER;
        videov.setLayoutParams(lp2);
        videov.setVideoPath(path);
        videov.getLayoutParams().width= SIZE;
        videov.getLayoutParams().height = SIZE;

        newVideoMargin(frameLayout,40,40,40,50);

        frameLayout.addView(videov);
        frameLayout.addView(play);

        return frameLayout;
    }

    public static ImageView newImageMargin(ImageView img,int left , int top, int right, int bottom){

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        img.setLayoutParams(new LinearLayout.LayoutParams(margin));

        return img;
    }

    public static void newVideoMargin(FrameLayout frameLayout,int left , int top, int right, int bottom){
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(frameLayout.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(margin));
    }

    public static void newPlayVideoMargin(VideoView videoView,int left , int top, int right, int bottom){
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(videoView.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        videoView.setLayoutParams(new LinearLayout.LayoutParams(margin));
    }


}
