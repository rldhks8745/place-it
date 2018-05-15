package com.mini_mo.viewpager.ReadAndWrite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mini_mo.viewpager.Camera.LoadingDialog;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sasor on 2018-04-25.
 */

public class WriteActivity extends AppCompatActivity implements View.OnClickListener{

    //GPS파트

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;

    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    private boolean isAccessFineLocation = false;

    private boolean isAccessCoarseLocation = false;

    private boolean isPermission = true;

    private GpsInfo gps;

    //GPS파트


    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    final int IMAGE_CODE = 100;

    //실험
    TextView imgcount;
    //실험

    Data data;

    Geocoder geocoder = null;
    Animation ani=null;

    ImageButton send, back,img,research;
    TextView location;
    LinearLayout imglist;
    ArrayList<Uri> arr_uri;
    String str= null;
    AssetFileDescriptor afd = null;
    Bitmap bmp = null;

    ImageList imgarrlist;
    ImageView tempimg;
    EditText content;
    InputStream inputStream;

    ArrayList<String> imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_write);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        geocoder = new Geocoder(this);

        data = new Data();
        imgurl = new ArrayList<>();

        //실험
        imgcount = (TextView)findViewById(R.id.imgcount);
        //실험

        send = (ImageButton)findViewById(R.id.send);
        back = (ImageButton)findViewById(R.id.back);
        research = (ImageButton)findViewById(R.id.research);
        imglist = (LinearLayout)findViewById(R.id.linear);
        img = (ImageButton)findViewById(R.id.img);
        location = (TextView)findViewById(R.id.location);
        content = (EditText)findViewById(R.id.content);

        imgarrlist = new ImageList();

        send.setOnClickListener(this);
        back.setOnClickListener(this);
        img.setOnClickListener(this);
        research.setOnClickListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.send:
                LoadingDialog loadingDialog = new LoadingDialog();
                loadingDialog.progressON( this, "보내는 중..." );

                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                research.startAnimation(ani);

                try {
                    data.writeBorard(content.getText().toString(), "aaa", "", 37.000 ,128.000, imgurl);
                } catch (JSONException e) {
                    e.printStackTrace();

                    loadingDialog.progressOFF();
                    finish();
                }

                loadingDialog.progressOFF();
                finish();
                break;

            case R.id.research:
                LoadingDialog resuarchDialog = new LoadingDialog();
                resuarchDialog.progressON( this, "위치 찾는 중..." );

                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                research.startAnimation(ani);



                // 권한 요청을 해야 함
                /*if (!isPermission) {
                    callPermission();
                    //Toast.makeText(getApplicationContext(),"aaaa",Toast.LENGTH_SHORT).show();
                    return;
                }*/


                gps = new GpsInfo(WriteActivity.this);

                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    location.setText( getAddress(latitude,longitude));


                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();

                }

                resuarchDialog.progressOFF();

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

                    if(imgarrlist.getSize() < 10) {
                        ani = AnimationUtils.loadAnimation(this, R.anim.button_anim);
                        img.startAnimation(ani);
                        //사진 추가하기
                        Intent imgadd = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);//사진을 여러개 선택할수 있도록 한다
                        imgadd.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        imgadd.setType("image/*");
                        startActivityForResult(Intent.createChooser(imgadd, "Select Picture"), IMAGE_CODE);
                    }else{
                        Toast.makeText(this,"사진은 10장까지 선택 가능합니다.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){
            if(imglist.getChildCount()<=10) {
                switch (requestCode){
                    case IMAGE_CODE:
                        ArrayList realPath = new ArrayList();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clipData = data.getClipData();
                            if(data.getClipData() == null){
                                realPath.add( getRealPathFromURI(data.getData())); //갤러리에서 받아온 uri를 절대경로로 변경 해준다.

                                imgurl.add(realPath.get(0).toString()); //imgurl 이라는 arraylist에 절대경로를 넣어준다.

                                Uri uri = data.getData(); //갤러리 사진을 uri로 받아온다.

                                imgarrlist.addListresult(newImageCreate(ReSizing(uri))); //uri로 만든 사진을 ReSizing() 메소드에 넣어 크기를 줄인 후 bitmap으로 반환 -> bitmap을 가지고 새로운 imageview 생성 후 imgarrlist에 추가
                                imgarrlist.getListresult(imgarrlist.getSize() - 1).setId(imgarrlist.getSize() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨 <클릭이벤트를 하기위함>
                                imgarrlist.getListresult(imgarrlist.getSize() - 1).setOnClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.


                                imglist.addView(imgarrlist.getListresult(imgarrlist.getSize()-1)); //imglist에 imgarrlist의 ImageView를 추가해준다.<imglist는 사진이 들어갈 LinearLayout 이다.>

                                imgcount.setText(String.valueOf(imgarrlist.getSize())); //사진이 몇장 선택되있는 지 카운터로 나타내준다.

                            }else if(clipData.getItemCount() > 10){
                                Toast.makeText(this,"사진은 10장까지 선택 가능합니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }else if(clipData.getItemCount() == 1){

                                realPath.add( getRealPathFromURI(clipData.getItemAt(0).getUri()));

                                imgurl.add(realPath.get(0).toString());

                                Uri uri = clipData.getItemAt(0).getUri();

                                imgarrlist.addListresult(newImageCreate(ReSizing(uri)));
                                imgarrlist.getListresult(imgarrlist.getSize() - 1).setId(imgarrlist.getSize() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨
                                imgarrlist.getListresult(imgarrlist.getSize() - 1).setOnClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.


                                imglist.addView(imgarrlist.getListresult(imgarrlist.getSize()-1));

                                imgcount.setText(String.valueOf(imgarrlist.getSize()));



                            }else{
                                for(int i=0;i<clipData.getItemCount();i++){
                                    realPath.add( getRealPathFromURI(clipData.getItemAt(0).getUri()));

                                    imgurl.add(realPath.get(0).toString());

                                    Uri uri = clipData.getItemAt(0).getUri();

                                    imgarrlist.addListresult(newImageCreate(ReSizing(uri)));
                                    imgarrlist.getListresult(imgarrlist.getSize() - 1).setId(imgarrlist.getSize() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨
                                    imgarrlist.getListresult(imgarrlist.getSize() - 1).setOnClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.


                                    imglist.addView(imgarrlist.getListresult(imgarrlist.getSize()-1));

                                    imgcount.setText(String.valueOf(imgarrlist.getSize()));

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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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

    public ImageButton newImageCreate(Bitmap bitmap){
        ImageButton imgv = new ImageButton(this);

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setImageBitmap(bitmap);
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imgv.getLayoutParams().width = 200;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public ImageView newImageCreate(ImageView imgv){

        imgv.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT));
        imgv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imgv.getLayoutParams().height = GridLayout.LayoutParams.MATCH_PARENT;
        //imgv.getLayoutParams().width = 400;

        newImageMargin(imgv,40,40,0,50);

        return imgv;
    }

    public ImageView newImageMargin(ImageView img,int left , int top, int right, int bottom){

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        img.setLayoutParams(new LinearLayout.LayoutParams(margin));

        return img;
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

    public Bitmap ReSizing(Uri uri){
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 12;
        opt.inDither = true;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inTempStorage = new byte[32 * 1024];
        bmp = BitmapFactory.decodeStream(inputStream, null, opt);

        return bmp;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        //GPS

        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }

        //GPS

        switch (requestCode) {


            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다
                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 2);
                }

                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 3);
                }

                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("퍼미션 ACCESS_NETWORK_STATE", " 퍼미션 허용");
                }

        }

    }

    // 전화번호 권한 요청

    private void callPermission() {

        // Check the SDK version and whether the permission is already granted or not.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {



            requestPermissions(

                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},

                    PERMISSIONS_ACCESS_FINE_LOCATION);



        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

                != PackageManager.PERMISSION_GRANTED){



            requestPermissions(

                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},

                    PERMISSIONS_ACCESS_COARSE_LOCATION);

        } else {

            isPermission = true;

        }

    }

}

