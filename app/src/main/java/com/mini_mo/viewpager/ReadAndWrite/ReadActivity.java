package com.mini_mo.viewpager.ReadAndWrite;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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

import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ReadActivity extends AppCompatActivity implements View.OnClickListener{

    //실험용
    int[] image = new int[10];

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




        count_state = 0; //DB에서 자기가 이 글에 대해 좋아요 눌렀는 지, 안눌렀는지 알아와야됩니다. (0대신 가져온 값/ 0: FLASE , 1: TRUE)
        total_count = 0; //화면 시작할때 DB에서 좋아요 개수를 불러와 넣기

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        roundedBitmapDrawable.setCircular(true);

        profile.setImageDrawable(roundedBitmapDrawable);

        /*Intent intent = getIntent();
        boardnumber = intent.getExtras().getString("Board_num");*/

        for(int i=0;i<10;i++) {

            Drawable drawable = getResources().getDrawable(R.drawable.test);

            imgarrlist.addListresult(newImageCreate(drawable)); // 나중에 서버에서 받을땐 Bitmap 으로 바꿔야된다.

            //url을 받아오면 그걸 비트맵으로 바꿔주고 그걸 drawable로 변환
            /*Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Drawable drawable = new BitmapDrawable(bitmap);*/

            //받은 사진들을 Arraylist에 담아 크기를 구해주고

            image[i] = R.drawable.test; //실험용  drawable 은 int 형이기 떄문에 나중에 DB연동할떈 STRING으로 바꿔야된다

            imgarrlist.getListresult(imgarrlist.getSize() - 1).setId(imgarrlist.getSize() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨

            imgarrlist.getListresult(imgarrlist.getSize() - 1).setOnClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.

            imglist.addView(imgarrlist.getListresult(imgarrlist.getSize() - 1)); // 마지막으로 ll이라는 리스트뷰에 넣어준다.
        }


        time.setText(getDate());
        back.setOnClickListener(this);
        comment.setOnClickListener(this);
        like_button.setOnClickListener(this);

        //gps 텍스트뷰 : 위치받아오기 완료되면 위치값 넣어주기 (위도 경도 받아오기)
        gps.setText( getAddress(37.57,126.97));
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        if(0<=v.getId() && v.getId() <= 9){ //나중엔 0부터 이미지 담겨있는 arraylist의 사이즈-1 까지로 정해준다.
            Intent intent = new Intent(this,ReadBoard_Image_Activity.class);
            intent.putExtra("number",0);

            for(int i=0; i < 10; i++){ //나중에 i< 10 부분을 i < arraylist.size 로 바꿔줘야됨
                Log.e("for mCon Tack","index");
                String key = "image"+String.valueOf(i);
                intent.putExtra(key, image[i]);
            }
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

    public String getAddress(double latitude, double longitude){

        List<Address> list = null;
        try {
            //위도 받기
            //경도 받기

            list = geocoder.getFromLocation(
                    latitude, // 위도
                    longitude, // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "server err - location read err");
        }
        String str = list.get(0).getLocality().toString()+" " + list.get(0).getSubLocality() + " " +list.get(0).getThoroughfare().toString();

        return str;
    }




    public ImageButton newImageCreate(){
        ImageButton imgv = new ImageButton(this);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setBackgroundResource(R.drawable.plus);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgv.getLayoutParams().height = 400;
        imgv.getLayoutParams().width = 400;


        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public ImageButton newImageCreate(Drawable drawable){
        ImageButton imgv = new ImageButton(this);



        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgv.getLayoutParams().height = 300;
        imgv.getLayoutParams().width = 300;
        imgv.setBackgroundColor(Color.parseColor("#ffffff"));
        imgv.setImageDrawable(drawable);

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public ImageView newImageCreate(ImageView imgv){

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgv.getLayoutParams().height = 400;
        imgv.getLayoutParams().width = 400;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public ImageView newImageMargin(ImageView img,int left , int top, int right, int bottom){

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        img.setLayoutParams(new LinearLayout.LayoutParams(margin));

        return img;
    }


}