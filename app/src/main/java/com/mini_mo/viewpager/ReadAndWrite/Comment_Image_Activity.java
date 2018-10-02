package com.mini_mo.viewpager.ReadAndWrite;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by sasor on 2018-04-29.
 */

public class Comment_Image_Activity extends AppCompatActivity {

    PhotoViewAttacher mAttacher;
    ImageView imageView;
    Bitmap bitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_imageselect);
        imageView = (ImageView)findViewById(R.id.select_img);

        Intent intent = getIntent();
        String img = intent.getStringExtra("img");

        Glide.with(getApplicationContext()).asBitmap().load(img)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        bitmap = ReadActivity.ReSizing( ReadActivity.bitmapToByteArray( resource ) );
                        imageView.setImageBitmap(bitmap);
                    }
                });

        mAttacher = new PhotoViewAttacher(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
