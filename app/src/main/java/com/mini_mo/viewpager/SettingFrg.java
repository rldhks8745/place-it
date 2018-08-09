package com.mini_mo.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mini_mo.viewpager.Setting.AlarmSetting;

/**
 * Created by 노현민 on 2018-07-27.
 */

public class SettingFrg extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_setting, container, false);

        LinearLayout push = (LinearLayout)view.findViewById(R.id.push);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SettingFrg.this.getContext() , AlarmSetting.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
