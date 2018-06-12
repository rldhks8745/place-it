package com.mini_mo.viewpager.ReadAndWrite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TooManyListenersException;

/**
 * Created by sasor on 2018-04-25.
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    RoundedBitmapDrawable roundedBitmapDrawable;

    Data data;
    User_Info user_info;

    int count,temp;

    ImageButton back =null, send = null;
    ImageView myprofile;

    Animation ani=null;
    EditText title = null;
    private ListView comment_list ;
    private CustomAdapter myadapter;

    ArrayList<ReadCommentInfo> rci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_comment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        back = (ImageButton)findViewById(R.id.back);
        send = (ImageButton)findViewById(R.id.send);

        myprofile = (ImageView)findViewById(R.id.myprofile);

        comment_list = (ListView)findViewById(R.id.listview);
        title = (EditText)findViewById(R.id.comment);
        myadapter = new CustomAdapter();

        comment_list.setAdapter(myadapter);

        data = new Data();

        try {
            rci = data.readComment(String.valueOf(Store.board_num));
            user_info = data.read_myPage(Store.userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(user_info.user_photo!=null) {
            Glide.with(getApplicationContext()).asBitmap().load(user_info.user_photo)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            roundedBitmapDrawable.setCircular(true);

                            myprofile.setImageDrawable(roundedBitmapDrawable);
                        }
                    });
        }else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
            roundedBitmapDrawable.setCircular(true);

            myprofile.setImageDrawable(roundedBitmapDrawable);
        }

        roundedBitmapDrawable = null;
        count = 0;
        temp = 0;



        for(int i=0;i<rci.size();i++){
            final ReadCommentInfo readCommentInfo = rci.get(i);
            final String content = readCommentInfo.comment_content;
            final String date = readCommentInfo.comment_date;

            Log.i("사진"+i , rci.get(i).user_photo);

            try {
                user_info = data.read_myPage(rci.get(i).comment_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(user_info.user_photo!=null) {

                Glide.with(getApplicationContext()).asBitmap().load(user_info.user_photo)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                                roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                roundedBitmapDrawable.setCircular(true);

                                myadapter.addItem(roundedBitmapDrawable,content,date);
                            }
                        });

            }

            roundedBitmapDrawable = null;
            count++;
        }

        comment_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        myadapter.notifyDataSetChanged();
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
                if(!title.getText().toString().equals("")) {
                    try {
                        data.writeComment(String.valueOf(Store.board_num), Store.userid, title.getText().toString());

                        try {
                            rci = data.readComment(String.valueOf(Store.board_num));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = count; i < rci.size(); i++) {
                            ReadCommentInfo readCommentInfo = rci.get(i);

                            try {
                                user_info = data.read_myPage(rci.get(i).comment_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (user_info.user_photo != null) {

                                Glide.with(getApplicationContext()).asBitmap().load(user_info.user_photo)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                                                roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                                roundedBitmapDrawable.setCircular(true);
                                            }
                                        });

                                myadapter.addItem(roundedBitmapDrawable, readCommentInfo.comment_content, readCommentInfo.comment_date);
                            }

                            roundedBitmapDrawable = null;
                            count++;
                        }

                        comment_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        myadapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    title.setText("");
                }else{
                    Util.Toast(this,"내용을 입력하세요!");
                }



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

    //서버에서 이미지를 받아 ImageView에 넣으니 아웃오브메모리 뜬다. 고쳐야됨
    public Bitmap ReSizing(byte[] bytes){
        Bitmap bitmap;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 6;
        opt.inDither = true;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inTempStorage = new byte[32 * 1024];
        bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,opt);

        return bitmap;
    }

    public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }


}
