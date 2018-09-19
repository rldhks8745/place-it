package com.mini_mo.viewpager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
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
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SettingFrg.this.getContext() , AlarmSetting.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        /** Fab 클릭 이벤트 --> 코멘트 작성 액티비티로 전환 **/
        LinearLayout save = (LinearLayout)view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getActivity(), SaveLocateActivity.class);
               startActivity(intent);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
