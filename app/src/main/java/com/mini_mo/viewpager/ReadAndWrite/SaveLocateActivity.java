package com.mini_mo.viewpager.ReadAndWrite;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.Save_List;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.Store;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SaveLocateActivity extends AppCompatActivity implements View.OnClickListener {

    //GPS파트

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;

    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    private boolean isAccessFineLocation = false;

    private boolean isAccessCoarseLocation = false;

    private boolean isPermission = true;

    private GpsInfo gps;

    //GPS파트

    EditText title;
    Button save;
    ImageButton back;

    LocateListviewAdapter locateadapter;
    ListView locatelist;

    Data data;
    ArrayList<Save_List> save_lists;

    double latitude,longitude;
    boolean result;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_savelocate2);

        title = (EditText)findViewById(R.id.title);
        save = (Button)findViewById(R.id.save);
        back = (ImageButton)findViewById(R.id.back);

        data = new Data();

        latitude = 0.0;
        longitude = 0.0;

        locateadapter = new LocateListviewAdapter(this);
        locatelist = (ListView)findViewById(R.id.locatelist);

        try {
            save_lists =  data.load_locate(Store.userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<save_lists.size();i++){
            Save_List saveList = save_lists.get(i);
            locateadapter.addItem(saveList.latitude,saveList.longitude,saveList.save_number,saveList.massage);
        }

        locatelist.setAdapter(locateadapter);

        locatelist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {



                AlertDialog.Builder dialog = new AlertDialog.Builder(SaveLocateActivity.this);
                dialog  .setTitle("삭제 알림")
                        .setMessage("위치:" + (locateadapter.getItem(1).getMessage()) +"\n정말 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //위치리스트 삭제는 id도 필요할뜻
                                try {
                                    data.delete_locate(locateadapter.getSaveNumber(position));
                                    Toast.makeText(getApplicationContext(),"삭제 완료!",Toast.LENGTH_SHORT).show();
                                    result=true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"삭제 실패!",Toast.LENGTH_SHORT).show();
                                    result=false;
                                }

                                locateadapter.removeItem(position);
                                locateadapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SaveLocateActivity.this, "삭제 취소", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();

                return result;
            }

        });

        save.setOnClickListener(this);
        back.setOnClickListener(this);
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


                gps = new GpsInfo(SaveLocateActivity.this);

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

                    locateadapter = new LocateListviewAdapter(this);

                    try {
                        save_lists =  data.load_locate(Store.userid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(int i=0;i<save_lists.size();i++){
                        Save_List saveList = save_lists.get(i);
                        locateadapter.addItem(saveList.latitude,saveList.longitude,saveList.save_number,saveList.massage);
                    }

                    locatelist.setAdapter(locateadapter);

                    Toast.makeText(getApplicationContext(),"저장됬습니다.",Toast.LENGTH_SHORT).show();

                    title.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"저장 실패!",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.back:
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
