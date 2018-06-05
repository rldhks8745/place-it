package com.mini_mo.viewpager.ReadAndWrite;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveLoacateActivity extends AppCompatActivity implements View.OnClickListener {

    //GPS파트

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;

    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    private boolean isAccessFineLocation = false;

    private boolean isAccessCoarseLocation = false;

    private boolean isPermission = true;

    private GpsInfo gps;

    //GPS파트

    EditText title;
    Button save,cancle;

    Data data;

    double latitude,longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_savelocate);

        title = (EditText)findViewById(R.id.title);
        save = (Button)findViewById(R.id.save);
        cancle = (Button)findViewById(R.id.cancle);

        data = new Data();

        latitude = 0.0;
        longitude = 0.0;

        save.setOnClickListener(this);
        cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.save:

                if(title.getText() == null || title.getText().equals("")){
                    title.setText(getDate());
                }

                if (!isPermission) {
                    callPermission();
                    return;
                }


                gps = new GpsInfo(SaveLoacateActivity.this);

                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();


                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();

                }

                try {
                    String t = title.getText().toString();
                    String str = data.save_locate(Store.userid,latitude,longitude,t);

                    if(str.equals("1")){
                        Toast.makeText(getApplicationContext(),"저장됬습니다.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"저장 실패!",Toast.LENGTH_SHORT).show();
                    }

                    title.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.cancle:
                finish();
                break;

        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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

    public String getDate(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String formatDate = sdfNow.format(date);


        return formatDate;
    }
}
