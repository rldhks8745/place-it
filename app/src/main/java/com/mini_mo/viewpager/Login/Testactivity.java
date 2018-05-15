package com.mini_mo.viewpager.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mini_mo.viewpager.R;

public class Testactivity extends AppCompatActivity {

    Button logout;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.lyh_test);

        logout = (Button)findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE); //로그아웃 버튼 클릭시
                SharedPreferences.Editor editor = auto.edit();
                editor.clear(); //안에 내용 다 지움
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class); // 로그인 액티비티로 이동
                startActivity(intent);
                finish();
            }
        });



    }
}
