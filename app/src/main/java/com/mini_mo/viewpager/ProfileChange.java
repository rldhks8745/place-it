package com.mini_mo.viewpager;

import android.Manifest;
import android.content.ClipData;
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
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.ReadAndWrite.ImageResizing;
import com.mini_mo.viewpager.ReadAndWrite.NewImageCrate;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileChange extends AppCompatActivity implements View.OnClickListener{

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    final int IMAGE_CODE = 100;
    Animation ani=null;

    ImageButton profile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_profilechange);

       profile = (ImageButton)findViewById(R.id.profileimg);

        /*if(rbi.b_photos != null) {
            //서버에서 이미지를 받아 ImageView에 넣으니 아웃오브메모리 뜬다. 고쳐야됨
            for (int i = 0; i < rbi.b_photos.size(); i++) {
                //실험용

                //서버에서 이미지를 Glide를 이용한 Bitmap으로 받아와 사이즈를 줄이고 이미지버튼으로 만들어준다.
                //id 와 리스너 까지 부여해줘서 클릭시 핀치줌을 가능하게 만들었다. 2018-05-29
                Glide.with(getApplicationContext()).asBitmap().load(rbi.b_photos.get(i))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap bitmap = ReSizing(bitmapToByteArray(resource));

                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                                roundedBitmapDrawable.setCircular(true);
                            }
                        });

            }
        }*/
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.profile:

                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck == PackageManager.PERMISSION_DENIED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        // 이 권한을 필요한 이유를 설명해야하는가?
                        //if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다

                        // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        //} else {


                        // 필요한 권한과 요청 코드를 넣어서 권한허가요청에 대한 결과를 받아야 합니다

                        //}
                    }
                } else {
                    ani = AnimationUtils.loadAnimation(this, R.anim.button_anim);
                    profile.startAnimation(ani);
                    //사진 추가하기
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_CODE);

                }

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
                                realPath.add( getRealPathFromURI(data.getData())); //갤러리에서 받아온 uri를 절대경로로 변경 해준다.

                                Uri uri = data.getData(); //갤러리 사진을 uri로 받아온다.

                                try {
                                    Bitmap bitmap = ReSizing(bitmapToByteArray(MediaStore.Images.Media.getBitmap(getContentResolver(), uri)));

                                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                                    roundedBitmapDrawable.setCircular(true);

                                    profile.setImageDrawable(roundedBitmapDrawable);

                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                profile.setImageURI(uri);
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
}
