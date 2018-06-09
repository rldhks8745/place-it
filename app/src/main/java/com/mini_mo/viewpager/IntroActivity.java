package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mini_mo.viewpager.Login.LoginActivity;

public class IntroActivity extends AppCompatActivity {

    Activity activity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introlayout);

        activity = this;

        Handler handler = new Handler();
        boolean b = handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        },2000);
    }
}
