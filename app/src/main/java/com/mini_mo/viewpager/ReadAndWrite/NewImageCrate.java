package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.request.target.SimpleTarget;
import com.mini_mo.viewpager.R;

/**
 * Created by Administrator on 2018-05-15.
 */

public class NewImageCrate {

    //새로운 이미지를 만들어주는 클래스 , Bitmap , DrawAble 등

    public static ImageButton newImageCreate(Activity activity){
        ImageButton imgv = new ImageButton(activity);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setBackgroundResource(R.drawable.plus);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgv.getLayoutParams().height = 400;
        imgv.getLayoutParams().width = 400;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static ImageButton newImageCreate(Activity activity, Bitmap bitmap){
        ImageButton imgv = new ImageButton(activity);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setImageBitmap(bitmap);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imgv.getLayoutParams().width = 200;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static ImageButton newImageCreate(Activity activity, Drawable drawable){
        ImageButton imgv = new ImageButton(activity);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgv.getLayoutParams().height = 300;
        imgv.getLayoutParams().width = 300;
        imgv.setBackgroundColor(Color.parseColor("#ffffff"));
        imgv.setImageDrawable(drawable);

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static ImageView newImageCreate(ImageView imgv){

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imgv.getLayoutParams().height = GridLayout.LayoutParams.MATCH_PARENT;
        //imgv.getLayoutParams().width = 400;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static ImageView newImageMargin(ImageView img,int left , int top, int right, int bottom){

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        img.setLayoutParams(new LinearLayout.LayoutParams(margin));

        return img;
    }


}
