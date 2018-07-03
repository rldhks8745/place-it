package com.mini_mo.viewpager.ReadAndWrite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import com.mini_mo.viewpager.DAO.User_Info;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;
import com.mini_mo.viewpager.YourPageActivity;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReadActivity extends AppCompatActivity implements View.OnClickListener{

    //실험용
    com.mini_mo.viewpager.DAO.ReadBoardInfo rbi;

    Activity activity;
    InputStream inputStream;
    ArrayList<ImageButton> buttons;
    View.OnClickListener listener;
    Data data;
    User_Info user_info;
    int count;

    //실험용



    ImageList imgarrlist;

    LinearLayout imglist,bar;

    ImageButton like_button,change,delete;
    ImageView profile,back;
    Button comment = null;
    TextView userid,gps,like_count,content;

    Animation ani=null;
    Geocoder geocoder = null;

    int count_state,total_count;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readboard);

        TextView time = (TextView)findViewById(R.id.date);
        userid = (TextView)findViewById(R.id.userid);
        bar = (LinearLayout)findViewById(R.id.bar);
        gps = (TextView)findViewById(R.id.gps);
        profile = (ImageView)findViewById(R.id.profile);
        back = (ImageView) findViewById(R.id.back);
        comment = (Button)findViewById(R.id.comment);
        like_button = (ImageButton)findViewById(R.id.like_button);
        like_count = (TextView)findViewById(R.id.like_count);
        geocoder = new Geocoder(this);
        imgarrlist = new ImageList();
        imglist = (LinearLayout)findViewById(R.id.ll);

        content = (TextView)findViewById(R.id.title);

        //실험
        data = new Data();

        count = 0;

        listener = this;
        buttons = new ArrayList<>();

        change = (ImageButton)findViewById(R.id.change);
        delete = (ImageButton)findViewById(R.id.delete);

        activity = this;

        Store.readboard_image.clear();
        //String.valueOf(Store.board_num)
        try {
            //String.valueOf(Store.board_num)
            rbi = new Data().readBoardInfo(String.valueOf(Store.board_num));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!Store.userid.equals(rbi.user_id)) {
            bar.removeView(delete);
            bar.removeView(change);
        }

        userid.setText(rbi.user_id.toString());
        content.setText(rbi.content.toString());

        if(rbi.b_photos != null) {

            for (int i = 0; i < rbi.b_photos.size(); i++) {
                //실험용

                //서버에서 이미지를 Glide를 이용한 Bitmap으로 받아와 사이즈를 줄이고 이미지버튼으로 만들어준다.
                //id 와 리스너 까지 부여해줘서 클릭시 핀치줌을 가능하게 만들었다. 2018-05-29
                Glide.with(getApplicationContext()).asBitmap().load(rbi.b_photos.get(i))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap bitmap = ReSizing( bitmapToByteArray( resource ) );

                                buttons.add(NewImageCrate.ReadnewImageCreate(activity, bitmap)); // 나중에 서버에서 받을땐 Bitmap 으로 바꿔야된다

                                Log.i("buttons 크기 : ", buttons.size()+"");
                                buttons.get(buttons.size()-1).setId(buttons.size()-1);
                                buttons.get(buttons.size()-1).setOnClickListener(listener);

                                Store.readboard_image.add(bitmap);
                                imglist.addView(buttons.get(buttons.size() - 1));
                            }
                        });


            }
        }

        //gps 텍스트뷰 : 위치받아오기 완료되면 위치값 넣어주기 (위도 경도 받아오기)

        if(rbi.latitude != 0.0 && rbi.longitude != 0.0){
            gps.setText( AddressTransformation.getAddress(this,rbi.latitude,rbi.longitude));
        }else{
            gps.setText("위치 불안정");
        }

        time.setText(rbi.date);
        like_count.setText(String.valueOf(rbi.good));

        //실험


        count_state = 0; //DB에서 자기가 이 글에 대해 좋아요 눌렀는 지, 안눌렀는지 알아와야됩니다. (0대신 가져온 값/ 0: FLASE , 1: TRUE)
        total_count = 0; //화면 시작할때 DB에서 좋아요 개수를 불러와 넣기

        try {
            user_info = data.read_myPage(rbi.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(user_info.user_photo!=null) {
            Glide.with(getApplicationContext()).asBitmap().load(user_info.user_photo)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            roundedBitmapDrawable.setCircular(true);

                            profile.setImageDrawable(roundedBitmapDrawable);
                        }
                    });
        }


        back.setOnClickListener(this);
        comment.setOnClickListener(this);
        like_button.setOnClickListener(this);
        change.setOnClickListener(this);
        delete.setOnClickListener(this);
        profile.setOnClickListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        if (0 <= v.getId() && v.getId() <= 9) { //0 ~ 9 번 째 클릭시
            Log.i("Click", "클릭 하셨습니다.");

            Intent intent = new Intent(this, ReadBoard_Image_Activity.class);
            startActivity(intent);
        }

        switch (v.getId()) {

            case R.id.profile:
                Intent intent1 = new Intent(this, YourPageActivity.class);
                intent1.putExtra("id",rbi.user_id);
                startActivity(intent1);

                break;

            case R.id.change:
                Intent intent2 = new Intent(this, ChangeBoard.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("글 삭제")
                        .setMessage("정말 글을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    data.delete_board(Store.board_num);
                                    Store.board_num = -1;
                                    Toast.makeText(getApplicationContext(),"글 삭제완료!",Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(),"글 삭제실패!",Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                break;

            case R.id.back:
                Store.board_num = 0;
                finish();
                break;

            case R.id.comment:
                Intent intent_comment = new Intent(this, CommentActivity.class);
                startActivity(intent_comment);
                break;

            case R.id.like_button:
                ani = AnimationUtils.loadAnimation(this, R.anim.button_anim);
                like_button.startAnimation(ani);

                try {
                    data.plus_good(Store.board_num);

                    int likecount = Integer.parseInt(like_count.getText().toString());
                    like_count.setText(String.valueOf((likecount+1)));

                    Toast.makeText(getApplicationContext(), "좋아요!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*if (count_state == 0) {
                    count_state = 1;
                    Toast.makeText(getApplicationContext(), "좋아요!", Toast.LENGTH_SHORT).show();
                    total_count += 1;
                    like_count.setText(String.valueOf(total_count));
                } else if (count_state == 1) {
                    count_state = 0;
                    Toast.makeText(getApplicationContext(), "좋아요 취소!", Toast.LENGTH_SHORT).show();
                    total_count -= 1;
                    like_count.setText(String.valueOf(total_count));
                }*/
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Store.board_num = 0;
        finish();
        super.onBackPressed();
    }

    //서버에서 이미지를 받아 ImageView에 넣으니 아웃오브메모리 뜬다. 고쳐야됨
    static public Bitmap ReSizing(byte[] bytes){
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

    static public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }
}