package com.mini_mo.viewpager.ReadAndWrite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.mini_mo.viewpager.R;


/**
 * Created by sasor on 2018-04-30.
 */

public class ReadBoard_Image_Activity extends AppCompatActivity {

    ViewPager viewPager;
    int[] image;
    Intent intent;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_test_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        image = new int[10];

        intent = getIntent();
        int num = intent.getExtras().getInt("number");

        for(int i=0; i < 10; i++){
            Log.e("for mCon Tack","index");
            intent = getIntent();
            String key = "image"+String.valueOf(i);
            image[i] = intent.getExtras().getInt(key);

        }

        viewPager.setAdapter(new MyViewPagerAdaper(getSupportFragmentManager(),image));
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }
}