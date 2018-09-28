package com.mini_mo.viewpager.ReadAndWrite;

import android.Manifest;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import java.util.ArrayList;

public class CommentWriteActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    final int IMAGE_CODE = 100;

    private Animation ani=null;
    private int permissionCheck;
    private Intent intent;

    private ImageView usericon;
    private ImageView back;
    private ImageView send;

    private EditText nickname;
    private EditText password;

    private EditText content;

    private ImageView image;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_comment_write_activity);

        usericon = (ImageView)findViewById(R.id.usericon);
        nickname = (EditText)findViewById(R.id.nickname);
        password = (EditText)findViewById(R.id.password);
        content = (EditText)findViewById(R.id.content);
        image = (ImageView)findViewById(R.id.img);
        back = (ImageView)findViewById(R.id.back);
        send = (ImageView)findViewById(R.id.send);

        if(!Store.userid.equals("Guest")){
            nickname.setText(Store.userid.toString());
        }

        image.setOnClickListener(this);
        image.setOnLongClickListener(this);
        back.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==R.id.img){

            Toast.makeText(getApplicationContext(),"삭제실행",Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.img:
                permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

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
                    ani = AnimationUtils.loadAnimation(this, R.anim.button_anim);
                    image.startAnimation(ani);

                    //사진 추가하기
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_CODE);
                }
                break;

            case R.id.back:

               intent = new Intent(this, CommentActivity.class);

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog  .setTitle("종료 알림")
                        .setMessage("작업했던 내용들이 사라집니다.\n정말 종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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

            case R.id.send:

                break;
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){
                switch (requestCode){
                    case IMAGE_CODE:
                        ArrayList realPath = new ArrayList();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clipData = data.getClipData();
                            if(data.getClipData() == null){
                                String photo = getRealPathFromURI(data.getData()); //갤러리에서 받아온 uri를 절대경로로 변경 해준다.

                                Uri uri = data.getData(); //갤러리 사진을 uri로 받아온다.
                                Store.arr_uri.add(uri);

                                image.setImageBitmap(ImageResizing.ReSizing(this.getContentResolver(),uri));
                            }

                        }
                        break;

                    default:
                        break;
                }
        }else{
            Toast.makeText(this,"사진 선택을 취소하셨습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    //이미지 절대경로
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

        intent = new Intent(this, CommentActivity.class);

        // Alert을 이용해 종료시키기
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog  .setTitle("종료 알림")
                .setMessage("작업했던 내용들이 사라집니다.\n정말 종료하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

}
