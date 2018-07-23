package com.mini_mo.viewpager.ReadAndWrite;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ReadCommentInfo;
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    Data data;
    User_Info user_info;

    int count,temp;

    ImageView send = null;
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

        send = (ImageView)findViewById(R.id.send);

        myprofile = (ImageView)findViewById(R.id.usericon);

        comment_list = (ListView)findViewById(R.id.listview);
        title = (EditText)findViewById(R.id.comment);
        myadapter = new CustomAdapter(this);

        comment_list.setAdapter(myadapter);

        data = new Data();

        try {
            rci = data.readComment(String.valueOf(Store.board_num));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<rci.size();i++) {
            Glide.with(getApplicationContext()).load(rci.get(i).user_photo).apply(bitmapTransform(new CircleCrop())).into(myprofile);
        }

        count = 0;
        temp = 0;

        for(int i=0;i<rci.size();i++){
            final ReadCommentInfo readCommentInfo = rci.get(i);

            Log.i("사진"+i , rci.get(i).user_photo);

            Glide.with(getApplicationContext()).load(rci.get(i).user_photo).apply(bitmapTransform(new CircleCrop())).into(myprofile);

            myadapter.addItem(rci.get(i).board_num,rci.get(i).comment_num,rci.get(i).user_photo, readCommentInfo.comment_content, readCommentInfo.comment_id);
            count++;
        }

        comment_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        myadapter.notifyDataSetChanged();
        send.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.image:
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
                                Glide.with(getApplicationContext()).load(rci.get(i).user_photo).apply(bitmapTransform(new CircleCrop())).into(myprofile);

                                myadapter.addItem(rci.get(i).board_num,rci.get(i).comment_num,rci.get(i).user_photo, readCommentInfo.comment_content, readCommentInfo.comment_id);

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
}
