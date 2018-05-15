package com.mini_mo.viewpager.ReadAndWrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import java.util.ArrayList;


/**
 * Created by sasor on 2018-04-30.
 */

public class ReadBoard_Image_Activity extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<Bitmap> image;
    Intent intent;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_test_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        image = Store.readboard_image;

        viewPager.setAdapter(new MyViewPagerAdaper(getSupportFragmentManager(),image));
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }
}