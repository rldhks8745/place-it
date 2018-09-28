package com.mini_mo.viewpager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mini_mo.viewpager.ListView.RecyclerListView;
import com.mini_mo.viewpager.Login.LoginActivity;
import com.mini_mo.viewpager.ReadAndWrite.SaveLocateActivity;
import com.mini_mo.viewpager.ReadAndWrite.Util;
import com.mini_mo.viewpager.ReadAndWrite.WriteActivity;
import android.widget.LinearLayout;

import com.mini_mo.viewpager.Setting.AlarmSetting;

/**
 * Created by 노현민 on 2018-07-27.
 */

public class SettingFrg extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_setting, container, false);

        LinearLayout push = (LinearLayout)view.findViewById(R.id.push);
        LinearLayout logout = (LinearLayout)view.findViewById(R.id.logout);


        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SettingFrg.this.getContext() , AlarmSetting.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences auto = getContext().getSharedPreferences("auto", Activity.MODE_PRIVATE); //로그아웃 버튼 클릭시
                SharedPreferences.Editor editor = auto.edit();
                editor.clear(); //안에 내용 다 지움
                editor.commit();

                Toast.makeText(getActivity().getApplicationContext(),"로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent( SettingFrg.this.getContext() , LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }
}
