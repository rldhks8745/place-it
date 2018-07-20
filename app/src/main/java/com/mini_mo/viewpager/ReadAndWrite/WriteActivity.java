package com.mini_mo.viewpager.ReadAndWrite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.maps.model.LatLng;
import com.mini_mo.viewpager.Camera.LoadingDialog;
import com.mini_mo.viewpager.Cluster.ClusterMap;
import com.mini_mo.viewpager.Cluster.Selectlocationmap;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.MainPageFragment;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener , View.OnLongClickListener{

    //GPS파트

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;

    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    private boolean isAccessFineLocation = false;

    private boolean isAccessCoarseLocation = false;

    private boolean isPermission = true;

    private GpsInfo gps;

    //GPS파트

    int permissionCheck;
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    final int IMAGE_CODE = 100;
    final int SELECT_VIDEO = 200;
    final int TAPMAP = 300;

    private String selectedPath;
    MediaController mc;

    //실험
    HashtagSpans hashtagSpans;

    double latitude;
    double longitude;
    //실험

    Data data;

    Geocoder geocoder = null;
    Animation ani=null;

    Spinner category;

    ImageView usericon,getlocation,history,video,img,send, back,tapmap;
    TextView location,userid;
    EditText content;

    LinearLayout imglist;
    ArrayList<View> viewarr;
    ArrayList<String> imgurl;

    int category_number;

    LoadingDialog loading = new LoadingDialog();

    public static WriteActivity instance;

    public static WriteActivity getInstance()
    {
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_writeboard);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        geocoder = new Geocoder(this);

        data = new Data();
        imgurl = new ArrayList<>();

        latitude = 0.0;
        longitude = 0.0;
        category_number = 0;

        mc = new MediaController(this);

        category = (Spinner)findViewById(R.id.spinner);

        send = (ImageView)findViewById(R.id.send);
        back = (ImageView)findViewById(R.id.back);
        usericon = (ImageView)findViewById(R.id.usericon);
        getlocation = (ImageView)findViewById(R.id.getlocation);
        history = (ImageView)findViewById(R.id.history);
        img = (ImageView)findViewById(R.id.img);
        video = (ImageView)findViewById(R.id.video);
        tapmap = (ImageView)findViewById(R.id.tapmap);

        imglist = (LinearLayout)findViewById(R.id.linear);

        location = (TextView)findViewById(R.id.location);
        userid = (TextView)findViewById(R.id.userid);
        content = (EditText)findViewById(R.id.content);

        viewarr = new ArrayList();

        Setting();

        send.setOnClickListener(this);
        back.setOnClickListener(this);
        img.setOnClickListener(this);
        video.setOnClickListener(this);
        getlocation.setOnClickListener(this);
        history.setOnClickListener(this);
        tapmap.setOnClickListener(this);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                category_number = position;
                Log.i("카테고리 넘버", position+"");            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onLongClick(View v) {

        if(0<=v.getId() && v.getId() <= 9){ //나중엔 0부터 이미지 담겨있는 arraylist의 사이즈-1 까지로 정해준다.

            Log.d("이미지 삭제 로그", String.valueOf(v.getId()));

            imgurl.remove(v.getId());
            viewarr.remove(v.getId());
            imglist.removeViewAt(v.getId()+2);

            for(int i=0;i<viewarr.size();i++){
                if(viewarr.get(i).getId() > v.getId()){
                    viewarr.get(i).setId(viewarr.get(i).getId()-1);
                }
            }

            Toast.makeText(getApplicationContext(),"사진 삭제 완료",Toast.LENGTH_SHORT).show();

            return true;
        }

        return false;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tapmap:
                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                tapmap.startAnimation(ani);


                Intent cintent = new Intent(this, Selectlocationmap.class);
                startActivityForResult(cintent,TAPMAP);

                break;

            case R.id.back:
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
                break;

            case R.id.send:
                /*LoadingDialog loadingDialog = new LoadingDialog();
                loadingDialog.progressON( this, "보내는 중..." );*/

                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                send.startAnimation(ani);

                hashtagSpans = new HashtagSpans(content.getText().toString(), '#');

               //hashtagSpans.getHashtags()  추출한 태그 String형

                Log.i("태그 ", (hashtagSpans.getHashtags().toString()+"#"));

                try {
                    String str;
                    if(Store.check){
                         str= data.writeBorard(content.getText().toString(), Store.userid, (hashtagSpans.getHashtags().toString() + "#"), Store.latitude,Store.longitude, imgurl);
                    }else {
                        str= data.writeBorard(content.getText().toString(), Store.userid, (hashtagSpans.getHashtags().toString() + "#"), latitude, longitude, imgurl);
                    }
                    if(str.equals("-3")){
                        Toast.makeText(getApplicationContext(),"글 등록이 실패하였습니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"글이 등록되었습니다.",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //loadingDialog.progressOFF();
                    finish();
                }

                //loadingDialog.progressOFF();
                finish();
                break;

            case R.id.getlocation:

                ani = AnimationUtils.loadAnimation(this,R.anim.button_anim);
                getlocation.startAnimation(ani);

                location.setText(AddressTransformation.getAddress(this, MainPageFragment.getInstance().latitude, MainPageFragment.getInstance().longitude));
                MainPageFragment.getInstance().getLocation( GpsInfo.WRITE );

                latitude = MainPageFragment.getInstance().latitude;
                longitude = MainPageFragment.getInstance().longitude;

                if( MainPageFragment.getInstance().latitude == 0.0 )
                    loading.progressON( this, "위치 수신 준비중");

                break;

            case R.id.history:

                Store.content = content.getText().toString();

                Intent intent = new Intent(this,LoadLocateActivity.class);
                startActivity(intent);
                finish();

                break;

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

                    if(viewarr.size() < 10) {
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

            case R.id.video:

                permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

                if(permissionCheck== PackageManager.PERMISSION_DENIED){

                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    }
                }else{

                    if(viewarr.size() < 10) {
                        ani = AnimationUtils.loadAnimation(this, R.anim.button_anim);
                        video.startAnimation(ani);
                        //사진 추가하기
                        Intent videointent = new Intent();
                        videointent.setType("video/*");
                        videointent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(videointent, "Select a Video "), SELECT_VIDEO);
                    }else{
                        Toast.makeText(this,"사진은 10장까지 선택 가능합니다.",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    public void setTextLocation( Location lo )
    {
        location.setText(AddressTransformation.getAddress(this, lo.getLatitude(), lo.getLongitude()));

        latitude = lo.getLatitude();
        longitude = lo.getLongitude();

        loading.progressOFF();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == TAPMAP){
                if(Store.point!=null) {
                    Log.i("위치 저장 값", Store.point.latitude + ", " + Store.point.longitude);
                    location.setText(AddressTransformation.getAddress(this, Store.point.latitude, Store.point.longitude));

                    latitude = Store.point.latitude;
                    longitude = Store.point.longitude;
                }else
                    Log.i("위치 저장 값", "널 포인트");
        }else if(resultCode == RESULT_OK){
            if(imglist.getChildCount()<=10) {
                switch (requestCode){

                    case SELECT_VIDEO:

                        ArrayList videoRealPath = new ArrayList();

                        System.out.println("SELECT_VIDEO");
                        Uri selectedImageUri = data.getData();
                        selectedPath = getPath(selectedImageUri);

                        videoRealPath.add(selectedPath);

                        imgurl.add(videoRealPath.get(0).toString());

                        viewarr.add(NewImageCrate.WritenewVideoCreate(this,selectedPath)); //uri로 만든 사진을 ReSizing() 메소드에 넣어 크기를 줄인 후 bitmap으로 반환 -> bitmap을 가지고 새로운 imageview 생성 후 imgarrlist에 추가
                        viewarr.get(viewarr.size() - 1).setId(viewarr.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨 <클릭이벤트를 하기위함>
                        viewarr.get(viewarr.size() - 1).setOnLongClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.

                        imglist.addView(viewarr.get(viewarr.size()-1)); //imglist에 imgarrlist의 ImageView를 추가해준다.<imglist는 사진이 들어갈 LinearLayout 이다.>
                        break;

                    case IMAGE_CODE:
                        ArrayList realPath = new ArrayList();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            ClipData clipData = data.getClipData();
                            if(data.getClipData() == null){
                                realPath.add( getRealPathFromURI(data.getData())); //갤러리에서 받아온 uri를 절대경로로 변경 해준다.


                                imgurl.add(realPath.get(0).toString()); //imgurl 이라는 arraylist에 절대경로를 넣어준다.

                                Uri uri = data.getData(); //갤러리 사진을 uri로 받아온다.

                                Store.arr_uri.add(uri);

                                viewarr.add(NewImageCrate.WritenewImageCreate(this,ImageResizing.ReSizing(this.getContentResolver(),uri))); //uri로 만든 사진을 ReSizing() 메소드에 넣어 크기를 줄인 후 bitmap으로 반환 -> bitmap을 가지고 새로운 imageview 생성 후 imgarrlist에 추가
                                viewarr.get(viewarr.size() - 1).setId(viewarr.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨 <클릭이벤트를 하기위함>
                                viewarr.get(viewarr.size() - 1).setOnLongClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.


                                imglist.addView(viewarr.get(viewarr.size()-1)); //imglist에 imgarrlist의 ImageView를 추가해준다.<imglist는 사진이 들어갈 LinearLayout 이다.>



                            }else if(clipData.getItemCount() > 10){
                                Toast.makeText(this,"사진은 10장까지 선택 가능합니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }else if(clipData.getItemCount() == 1){

                                realPath.add( getRealPathFromURI(clipData.getItemAt(0).getUri()));

                                imgurl.add(realPath.get(0).toString());

                                Uri uri = clipData.getItemAt(0).getUri();

                                Store.arr_uri.add(uri);

                                viewarr.add(NewImageCrate.WritenewImageCreate(this,ImageResizing.ReSizing(this.getContentResolver(),uri)));
                                viewarr.get(viewarr.size() - 1).setId(viewarr.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨
                                viewarr.get(viewarr.size() - 1).setOnLongClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.


                                imglist.addView(viewarr.get(viewarr.size()-1));

                            }else{
                                for(int i=0;i<clipData.getItemCount();i++){
                                    realPath.add( getRealPathFromURI(clipData.getItemAt(i).getUri()));

                                    imgurl.add(realPath.get(i).toString());

                                    Uri uri = clipData.getItemAt(i).getUri();

                                    Store.arr_uri.add(uri);

                                    viewarr.add(NewImageCrate.WritenewImageCreate(this,ImageResizing.ReSizing(this.getContentResolver(),uri)));
                                    viewarr.get(viewarr.size() - 1).setId(viewarr.size() - 1); // imarrlist의 0번째 값의 id를 정해준다. 여긴 나중에 arraylist의 크기를 바로 id로 정해주면 됨
                                    viewarr.get(viewarr.size() - 1).setOnLongClickListener(this); //추가해주는 이미지마다 클릭리스너 달아준다.


                                    imglist.addView(viewarr.get(viewarr.size()-1));
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
        // Alert을 이용해 종료시키기
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

    //비디오 절대경로
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
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

    public void Setting(){
        if(!Store.content.equals("")){
            content.setText(Store.content);
            Store.content="";
        }

        if(Store.latitude != 0.0 && Store.longitude != 0.0){
            location.setText( AddressTransformation.getAddress(this,Store.latitude,Store.longitude));
            Store.latitude = 0.0;
            Store.longitude = 0.0;
        }

        userid.setText(Store.userid.toString());

        if(Store.myprofile_img != null) {
            Glide.with(this).load(Store.myprofile_img).apply(bitmapTransform(new CircleCrop())).into(usericon);
        }else{
            Glide.with(this).load(R.drawable.user).apply(bitmapTransform(new CircleCrop())).into(usericon);
        }
    }

}

