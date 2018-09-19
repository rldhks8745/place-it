package com.mini_mo.viewpager.Setting;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mini_mo.viewpager.BootReceiver;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.Push;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.YourPageActivity;

/**
 * Created by userForGame on 2018-08-09.
 */

public class AlarmSetting extends AppCompatActivity {

    public static AlarmSetting instance;

    // 알람 설정에 필요한 변수들
    public static boolean alarmOn = false; // 알람을 사용하나, 안하나 ( Service 실행여부 )
    public static int selectedCategory = 0; // 카테고리
    public static int selectedDistance = 0; // 반경(m) 거리
    public static int selectedTime = 0; // 시간(분)


    // XML 객체들
    private RadioGroup onOffRadioGroup;

    private Spinner category;
    private String[] categoryItem;

    private Spinner around;
    private String[] aroundItem;

    private Spinner time;
    private String[] timeItem;

    public static AlarmSetting getInstance(){ return instance; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_alarm);
        instance = this;

        /** 알람 온,오프 라디오 그룹 설정 **/
        onOffRadioGroup = (RadioGroup)findViewById( R.id.setting_alarm_onoff_group );
        onOffRadioGroup.setOnCheckedChangeListener( radioGroupButtonChangeListener );
        // 체크되있는 상태
        if( !alarmOn )
            onOffRadioGroup.check( R.id.setting_alarm_off ); // 디폴트로 Off에 체크 되어있음.
        else
            onOffRadioGroup.check( R.id.setting_alarm_on ); // 디폴트로 Off에 체크 되어있음.

        /** 카테고리 설정 **/
        categoryItem = new String[]{"기타","의류","뷰티","잡화","가구","생활","건강","음식"};
        category = (Spinner)findViewById(R.id.setting_alarm_category);
        category.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                selectedCategory = position;

                category.setSelection( selectedCategory );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, categoryItem );
        categoryAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        category.setAdapter( categoryAdapter );
        category.setSelection( selectedCategory ); // 어댑터 연결 후 지정해야함

        /** 범위지정 **/
        aroundItem = new String[]{ "10", "25", "50", "100" };
        around = (Spinner)findViewById(R.id.setting_alarm_round);
        around.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                selectedDistance = position;

                around.setSelection( selectedDistance );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );

        SpinnerAdapter aroundAdapter = new SpinnerAdapter( this, android.R.layout.simple_spinner_item, aroundItem );

        around.setAdapter( aroundAdapter );
        around.setSelection( selectedDistance ); // 어댑터 연결 후 지정해야함


        /** 시간지정 **/
        timeItem = new String[]{ "1", "5", "10", "30", "60" };
        time = (Spinner)findViewById(R.id.setting_alarm_time);
        time.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                selectedTime = position;

                time.setSelection( selectedTime );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );

        SpinnerAdapter timeAdapter = new SpinnerAdapter( this, android.R.layout.simple_spinner_item, timeItem );

        time.setAdapter( timeAdapter );
        time.setSelection( selectedTime ); // 어댑터 연결 후 지정해야함


        // 만약 알람이 off면 선택사항 모두 비활성화 메소드
        setAlarmEnabled( alarmOn );
    }

    public void setAlarmEnabled( boolean on ){

        if( !on ) {
            if( category != null)
                category.setEnabled(false);
            if( around != null)
                around.setEnabled(false);
            if( time != null)
                time.setEnabled(false);
        }
        else {

            if( category != null)
                category.setEnabled(true);
            if( around != null)
                around.setEnabled(true);
            if( time != null)
                time.setEnabled(true);
        }
    }

    // 알람 온,오프 라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if( i == R.id.setting_alarm_on ){
                alarmOn = true;
            }
            else if( i == R.id.setting_alarm_off ){
                alarmOn = false;
            }

            // 만약 알람이 off면
            setAlarmEnabled( alarmOn );
        }
    };

    /*
        커스텀 카테고리 클래스
     */
    public class SpinnerAdapter extends ArrayAdapter<String> {
        Context context;
        String[] items = new String[] {};

        public SpinnerAdapter(final Context context,
                              final int textViewResourceId, final String[] objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.context = context;
        }

        /**
         * 스피너 클릭시 보여지는 View의 정의
         */
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_dropdown_item, parent, false);
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(items[position]);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(20);
            tv.setHeight(30);
            return convertView;
        }

        /**
         * 기본 스피너 View 정의
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(
                        android.R.layout.simple_spinner_item, parent, false);
            }

            TextView tv = (TextView) convertView
                    .findViewById(android.R.id.text1);
            tv.setText( items[ position ] );

            tv.setTextSize(12);
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {

        Log.i("AlarmSetting", "onDestroy_start");
        super.onDestroy();

        if(!isServiceRunningCheck()) {
            if (alarmOn) {
                Intent intentMyService = new Intent(this, Push.class);
                startService(intentMyService);
                Log.i("PushService", "Start");
            }
            /* 여기에다가 서버로 현재 사용자의 알람설정 보내기 */
        }
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("service_name", service.service.getClassName());
            if ("com.mini_mo.viewpager.Push".equals(service.service.getClassName())) {
                Log.i("isServiceRunningCheck()", "true");
                return true;
            }
        }
        Log.i("isServiceRunningCheck()", "false");
        return false;
    }


}
