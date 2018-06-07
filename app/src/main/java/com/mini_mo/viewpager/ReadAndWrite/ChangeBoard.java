package com.mini_mo.viewpager.ReadAndWrite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ReadBoardInfo;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-05-16.
 */

public class ChangeBoard extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    final int IMAGE_CODE = 100;

    HashtagSpans hashtagSpans;

    Data data;

    Activity activity;
    View.OnClickListener listener;
    View.OnLongClickListener longlistener;

    ArrayList<String> imgurl;
    ArrayList<Bitmap> bitmaplist;
    ArrayList<ImageButton> imgbuttonlist;
    ArrayList<String> origin_url;
    String[] split_url;

    ArrayList<String> arr_delete_url;

    ImageList imgarrlist;
    Animation ani=null;

    InputStream inputStream;

    ImageButton send,back,img;

    LinearLayout imglayout;

    EditText content;

    TextView imgcount;

    ReadBoardInfo rbi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_board_change);

        //서버연결 부분

        activity = this;
        Store.readboard_image.clear();

        data = new Data();

        bitmaplist = new ArrayList<>();
        imgbuttonlist = new ArrayList<>();
        arr_delete_url = new ArrayList<>();

        send = (ImageButton)findViewById(R.id.send);
        back = (ImageButton)findViewById(R.id.back);
        img = (ImageButton)findViewById(R.id.img);

        imglayout = (LinearLayout)findViewById(R.id.linear);

        imgarrlist = new ImageList();

        listener = this;
        longlistener = this;

        try {
            rbi = new Data().readBoardInfo(String.valueOf(Store.board_num));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(rbi.b_photos != null) {
            //서버에서 이미지를 받아 ImageView에 넣으니 아웃오브메모리 뜬다. 고쳐야됨
            for (int i = 0; i < rbi.b_photos.size(); i++) {
                //실험용
                origin_url.add(rbi.b_photos.get(i));

                //서버에서 이미지를 Glide를 이용한 Bitmap으로 받아와 사이즈를 줄이고 이미지버튼으로 만들어준다.
                //id 와 리스너 까지 부여해줘서 클릭시 핀치줌을 가능하게 만들었다. 2018-05-29
                Glide.with(getApplicationContext()).asBitmap().load(rbi.b_photos.get(i))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                Log.i("이미지 URL",rbi.b_photos.get(imgbuttonlist.size())+"");

                                Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                                imgbuttonlist.add(NewImageCrate.WritenewImageCreate(activity, bitmap)); // 나중에 서버에서 받을땐 Bitmap 으로 바꿔야된다

                                Log.i("buttons 크기 : ", imgbuttonlist.size()+"");
                                imgbuttonlist.get(imgbuttonlist.size()-1).setId(imgbuttonlist.size()-1);
                                imgbuttonlist.get(imgbuttonlist.size()-1).setOnLongClickListener(longlistener);
                                Store.readboard_image.add(bitmap);
                                imglayout.addView(imgbuttonlist.get(imgbuttonlist.size() - 1));
                            }
                        });


            }

            imgcount = (TextView)findViewById(R.id.imgcount);
            imgcount.setText(String.valueOf(rbi.b_photos.size()));
        }else{
            imgcount = (TextView)findViewById(R.id.imgcount);
            imgcount.setText("0");
        }
        //서버연결 부분



        content = (EditText)findViewById(R.id.content);
        content.setText(rbi.content);


        send.setOnClickListener(this);
        back.setOnClickListener(this);
        img.setOnClickListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onLongClick(View v) {

        if(0<=v.getId() && v.getId() <= 9){ //나중엔 0부터 이미지 담겨있는 arraylist의 사이즈-1 까지로 정해준다.

            Log.d("이미지 삭제 로그", String.valueOf(v.getId()));

            split_url = (origin_url.get(v.getId()).split("/"));

            arr_delete_url.add(split_url[split_url.length-1]);

            imgbuttonlist.remove(v.getId());
            imglayout.removeViewAt(v.getId()+1);

            for(int i=0;i<imgbuttonlist.size();i++){
                if(imgbuttonlist.get(i).getId() > v.getId()){
                    imgbuttonlist.get(i).setId(imgbuttonlist.get(i).getId()-1);
                }
            }

            imgcount.setText(String.valueOf(imgbuttonlist.size()));
            Toast.makeText(getApplicationContext(),"사진 삭제 완료",Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {



        switch (v.getId()){

            case R.id.send:

                hashtagSpans = new HashtagSpans(content.getText().toString(), '#');

                try {
                   String str = data.change_board(Store.board_num,content.getText().toString(),hashtagSpans.getHashtags().toString(),imgurl,arr_delete_url);

                   //리턴값에 따라서 글 수정이 성공인지 실패인지 알려준다.
                    if(str.equals("1")){
                        Toast.makeText(getApplicationContext(),"글이 등록되었습니다.",Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"글 등록이 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                finish();
                break;

            case R.id.back:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog  .setTitle("종료 알림")
                        .setMessage("작업했던 내용들이 사라집니다.\n정말 종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(activity,ReadActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                break;

            case R.id.img:

                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if(permissionCheck== PackageManager.PERMISSION_DENIED){

                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                        // 이 권한을 필요한 이유를 설명해야하는가?
                        //if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                        // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        //} else {
                        // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다
                        //}
                    }
                }else{

                    if(imgbuttonlist.size() <= 10) {
                        ani = AnimationUtils.loadAnimation(this, R.anim.button_anim);
                        img.startAnimation(ani);
                        //사진 추가하기
                        Intent imgadd = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);//사진을 여러개 선택할수 있도록 한다
                        imgadd.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        imgadd.setType("image/*");
                        startActivityForResult(Intent.createChooser(imgadd, "Select Picture"), IMAGE_CODE);
                    }else{
                        Toast.makeText(this,"사진은 10장까지만 추가 가능합니다.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){
            if(imglayout.getChildCount()<=10) {
                switch (requestCode){
                    case IMAGE_CODE:
                        ArrayList realPath = new ArrayList();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clipData = data.getClipData();
                            if(data.getClipData() == null){
                                if((imgbuttonlist.size()+1) > 10){
                                    Toast.makeText(this,"사진은 10장까지만 추가 가능합니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }else {
                                    realPath.add(getRealPathFromURI(data.getData())); //갤러리에서 받아온 uri를 절대경로로 변경 해준다.

                                    imgurl.add(realPath.get(0).toString()); //imgurl 이라는 arraylist에 절대경로를 넣어준다.

                                    Uri uri = data.getData(); //갤러리 사진을 uri로 받아온다.

                                    imgbuttonlist.add(NewImageCrate.WritenewImageCreate(this, ImageResizing.ReSizing(this.getContentResolver(), uri))); //uri로 만든 사진을 ReSizing() 메소드에 넣어 크기를 줄인 후 bitmap으로 반환 -> bitmap을 가지고 새로운 imageview 생성 후 imgarrlist에 추가
                                    imgbuttonlist.get(imgbuttonlist.size() - 1).setId(imgbuttonlist.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨 <클릭이벤트를 하기위함>
                                    imgbuttonlist.get(imgbuttonlist.size() - 1).setOnLongClickListener(longlistener); //추가해주는 이미지마다 클릭리스너 달아준다.


                                    imglayout.addView(imgbuttonlist.get(imgbuttonlist.size() - 1)); //imglist에 imgarrlist의 ImageView를 추가해준다.<imglist는 사진이 들어갈 LinearLayout 이다.>

                                    imgcount.setText(String.valueOf(imgbuttonlist.size())); //사진이 몇장 선택되있는 지 카운터로 나타내준다.
                                }
                            }else if(clipData.getItemCount() > 10){
                                Toast.makeText(this,"사진은 10장까지 선택 가능합니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }else if(clipData.getItemCount() == 1){

                                if((imgbuttonlist.size()+clipData.getItemCount()) > 10){
                                    Toast.makeText(this,"사진은 10장까지만 추가 가능합니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }else {

                                    realPath.add(getRealPathFromURI(clipData.getItemAt(0).getUri()));

                                    imgurl.add(realPath.get(0).toString());

                                    Uri uri = clipData.getItemAt(0).getUri();

                                    imgbuttonlist.add(NewImageCrate.WritenewImageCreate(this, ImageResizing.ReSizing(this.getContentResolver(), uri))); //uri로 만든 사진을 ReSizing() 메소드에 넣어 크기를 줄인 후 bitmap으로 반환 -> bitmap을 가지고 새로운 imageview 생성 후 imgarrlist에 추가
                                    imgbuttonlist.get(imgbuttonlist.size() - 1).setId(imgbuttonlist.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨 <클릭이벤트를 하기위함>
                                    imgbuttonlist.get(imgbuttonlist.size() - 1).setOnLongClickListener(longlistener); //추가해주는 이미지마다 클릭리스너 달아준다.


                                    imglayout.addView(imgbuttonlist.get(imgbuttonlist.size() - 1)); //imglist에 imgarrlist의 ImageView를 추가해준다.<imglist는 사진이 들어갈 LinearLayout 이다.>

                                    imgcount.setText(String.valueOf(imgbuttonlist.size())); //사진이 몇장 선택되있는 지 카운터로 나타내준다.
                                }
                            }else{
                                for(int i=0;i<clipData.getItemCount();i++){

                                    if((imgbuttonlist.size()+clipData.getItemCount()) > 10){
                                        Toast.makeText(this,"사진은 10장까지만 추가 가능합니다.",Toast.LENGTH_SHORT).show();
                                        return;
                                    }else {
                                        realPath.add( getRealPathFromURI(clipData.getItemAt(i).getUri()));

                                        imgurl.add(realPath.get(i).toString());

                                        Uri uri = clipData.getItemAt(i).getUri();

                                        imgbuttonlist.add(NewImageCrate.WritenewImageCreate(this,ImageResizing.ReSizing(this.getContentResolver(),uri))); //uri로 만든 사진을 ReSizing() 메소드에 넣어 크기를 줄인 후 bitmap으로 반환 -> bitmap을 가지고 새로운 imageview 생성 후 imgarrlist에 추가
                                        imgbuttonlist.get(imgbuttonlist.size() - 1).setId(imgbuttonlist.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨 <클릭이벤트를 하기위함>
                                        imgbuttonlist.get(imgbuttonlist.size() - 1).setOnLongClickListener(longlistener); //추가해주는 이미지마다 클릭리스너 달아준다.


                                        imglayout.addView(imgbuttonlist.get(imgbuttonlist.size()-1)); //imglist에 imgarrlist의 ImageView를 추가해준다.<imglist는 사진이 들어갈 LinearLayout 이다.>

                                        imgcount.setText(String.valueOf(imgbuttonlist.size())); //사진이 몇장 선택되있는 지 카운터로 나타내준다.
                                    }
                                }

                            }

                        }
                        break;

                    default:
                        break;
                }
            }else {
                Toast.makeText(this,"최대 10장까지 올리실 수 있어요!.",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"사진 선택을 취소하셨습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog  .setTitle("종료 알림")
                .setMessage("작업했던 내용들이 사라집니다.\n정말 종료하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    public Bitmap ReSizing(byte[] bytes){
        Bitmap bitmap;

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 12;
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
