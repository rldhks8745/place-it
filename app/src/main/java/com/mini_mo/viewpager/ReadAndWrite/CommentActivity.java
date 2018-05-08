package com.mini_mo.viewpager.ReadAndWrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sasor on 2018-04-25.
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton back =null, send = null;
    Animation ani=null;
    EditText title = null;
    private ListView comment_list ;
    private CustomAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_comment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        back = (ImageButton)findViewById(R.id.back);
        send = (ImageButton)findViewById(R.id.send);
        comment_list = (ListView)findViewById(R.id.listview);
        title = (EditText)findViewById(R.id.comment);
        myadapter = new CustomAdapter();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);

        for(int i=0;i<1;i++)

            myadapter.addItem(roundedBitmapDrawable,"안녕하세요!","2018");



        comment_list.setAdapter(myadapter);
        back.setOnClickListener(this);
        send.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.send:
                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                send.startAnimation(ani);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                roundedBitmapDrawable.setCircular(true);

                myadapter.addItem(roundedBitmapDrawable,title.getText().toString(),getDate());
                myadapter.notifyDataSetChanged();

                title.setText("");

                break;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public String getDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formatDate = sdfNow.format(date);


        return formatDate;
    }


}
