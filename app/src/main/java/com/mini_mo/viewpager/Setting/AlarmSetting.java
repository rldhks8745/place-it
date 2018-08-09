package com.mini_mo.viewpager.Setting;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;
import com.mini_mo.viewpager.YourPageActivity;

/**
 * Created by userForGame on 2018-08-09.
 */

public class AlarmSetting extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static AlarmSetting instance;

    // 알람 설정에 필요한 변수들
    public static boolean alarmOn = false; // 알람을 사용하나, 안하나 ( Service 실행여부 )
    public static String selectedCategory = "전체";

    // XML 객체들
    private RadioGroup onOffRadioGroup;
    private Spinner category;
    private String[] categoryItem;

    public static AlarmSetting getInstance(){ return instance; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_alarm);
        instance = this;

        // 알람 온,오프 라디오 그룹 설정
        onOffRadioGroup = (RadioGroup)findViewById( R.id.setting_alarm_onoff_group );
        onOffRadioGroup.setOnCheckedChangeListener( radioGroupButtonChangeListener );
        onOffRadioGroup.check( R.id.setting_alarm_off ); // 디폴트로 Off에 체크 되어있음.

        // 카테고리 설정
        categoryItem = new String[]{"전체","의류","뷰티","잡화","가구","생활","건강","음식","기타"};
        category = (Spinner)findViewById(R.id.setting_alarm_category);
        category.setOnItemSelectedListener( this );

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_spinner_item, categoryItem );
        categoryAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        category.setAdapter( categoryAdapter );

        //
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
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = (String)category.getItemAtPosition( position );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
