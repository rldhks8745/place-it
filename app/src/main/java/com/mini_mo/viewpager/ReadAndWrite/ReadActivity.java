package com.mini_mo.viewpager.ReadAndWrite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ReadActivity extends AppCompatActivity implements View.OnClickListener{

    //실험용
    com.mini_mo.viewpager.DAO.ReadBoardInfo rbi;

    InputStream inputStream;
    //실험용


    Geocoder geocoder = null;


    ImageList imgarrlist;
    ImageView tempimg;
    LinearLayout imglist;
    ImageButton like_button,back;
    Button comment = null;
    TextView gps,like_count;
    Animation ani=null;
    Bitmap bitmap = null;
    String boardnumber;

    TextView content;

    int count_state,total_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView time = (TextView)findViewById(R.id.time);
        gps = (TextView)findViewById(R.id.gps);
        ImageView profile = (ImageView)findViewById(R.id.profile);
        back = (ImageButton)findViewById(R.id.back);
        comment = (Button)findViewById(R.id.comment);
        like_button = (ImageButton)findViewById(R.id.like_button);
        like_count = (TextView)findViewById(R.id.like_count);
        geocoder = new Geocoder(this);
        imgarrlist = new ImageList();
        imglist = (LinearLayout)findViewById(R.id.ll);

        content = (TextView)findViewById(R.id.title);

        //실험

        Store.readboard_image.clear();

        try {
            rbi = new Data().readBoardInfo("19");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        content.setText(rbi.content);

        //서버에서 이미지를 받아 ImageView에 넣으니 아웃오브메모리 뜬다. 고쳐야됨
        for(int i=0;i<rbi.b_photos.size();i++) {
             //실험용


            Glide.with(getApplicationContext()).asBitmap().load(rbi.b_photos.get(i))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            imgarrlist.addListresult(newImageCreate(resource)); // 나중에 서버에서 받을땐 Bitmap 으로 바꿔야된다

                            Store.readboard_image.add(resource);
                            imglist.addView(imgarrlist.getListresult(imgarrlist.getSize() - 1));
                        }
                    });


        }


        for(int i=0;i<imgarrlist.getSize();i++) {
            imgarrlist.getListresult(i).setId(i);
            imgarrlist.getListresult(i).setOnClickListener(this);
        }

        //gps 텍스트뷰 : 위치받아오기 완료되면 위치값 넣어주기 (위도 경도 받아오기)
        gps.setText( AddressTransformation.getAddress(this,rbi.latitude,rbi.longitude));
        time.setText(rbi.date);
        like_count.setText(String.valueOf(rbi.good));

        //실험


        count_state = 0; //DB에서 자기가 이 글에 대해 좋아요 눌렀는 지, 안눌렀는지 알아와야됩니다. (0대신 가져온 값/ 0: FLASE , 1: TRUE)
        total_count = 0; //화면 시작할때 DB에서 좋아요 개수를 불러와 넣기

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);

        profile.setImageDrawable(roundedBitmapDrawable);


        time.setText(getDate());
        back.setOnClickListener(this);
        comment.setOnClickListener(this);
        like_button.setOnClickListener(this);

    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        if(0<=v.getId() && v.getId() <= imgarrlist.getSize()){ //나중엔 0부터 이미지 담겨있는 arraylist의 사이즈-1 까지로 정해준다.
            Log.i("Click","클릭 하셨습니다.");

            Intent intent = new Intent(this,ReadBoard_Image_Activity.class);
            intent.putExtra("number",0);

            startActivity(intent);
        }

        switch (v.getId()){
            case R.id.back:
                finish();
                break;

            case R.id.comment:
                Intent intent_comment = new Intent(this,CommentActivity.class);
                startActivity(intent_comment);
                break;

            case R.id.like_button:
                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                like_button.startAnimation(ani);

                if(count_state == 0){
                    count_state=1;
                    Toast.makeText(getApplicationContext(), "좋아요!", Toast.LENGTH_SHORT).show();
                    total_count+=1;
                    like_count.setText(String.valueOf(total_count));
                }else if(count_state==1){
                    count_state=0;
                    Toast.makeText(getApplicationContext(), "좋아요 취소!", Toast.LENGTH_SHORT).show();
                    total_count-=1;
                    like_count.setText(String.valueOf(total_count));
                }
                break;
        }
    }

    public String getDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formatDate = sdfNow.format(date);


        return formatDate;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public ImageButton newImageCreate(Bitmap bitmap){
        ImageButton imgv = new ImageButton(this);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgv.getLayoutParams().height = 400;
        imgv.getLayoutParams().width = 400;
        imgv.setBackgroundColor(Color.parseColor("#ffffff"));
        imgv.setImageBitmap(bitmap);

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public static ImageView newImageMargin(ImageView img,int left , int top, int right, int bottom){

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        img.setLayoutParams(new LinearLayout.LayoutParams(margin));

        return img;
    }

    //서버에서 이미지를 받아 ImageView에 넣으니 아웃오브메모리 뜬다. 고쳐야됨
    public Bitmap ReSizing(Bitmap bitmap, URL url){

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 12;
        opt.inDither = true;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inTempStorage = new byte[32 * 1024];
        bitmap = BitmapFactory.decodeFile(String.valueOf(url), opt);

        return bitmap;
    }

}