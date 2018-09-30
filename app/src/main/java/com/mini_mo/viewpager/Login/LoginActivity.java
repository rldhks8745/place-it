package com.mini_mo.viewpager.Login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.MainActivity;
import com.mini_mo.viewpager.R;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    Button mbutton;
    Button lbutton,guest;
    EditText lid,lpasswd;
    String loginId,loginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyh_login);

        mbutton = (Button) findViewById(R.id.mbutton);
        lbutton = (Button) findViewById(R.id.lbutton);
        guest = (Button) findViewById(R.id.guest);
        lid = (EditText) findViewById(R.id.lid);
        lpasswd = (EditText) findViewById(R.id.lpasswd);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);

       if(loginId !=null && loginPwd != null && !loginId.equals("Guest"))  //자동 로그인할 정보가 있나 확인
        {
            int result=0;

            try {
                result = Integer.parseInt(new Data().login(loginId,loginPwd));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(result == 1)
            {
                Toast.makeText(getApplicationContext(),"기존 계정으로 로그인합니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                finish();

            }

        }

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lid.setText("Guest");
                lpasswd.setText("Guest");
                Login();


            }
        });

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Membershipactivity.class);
                startActivity(intent);

            }
        });

        lbutton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Login();
            }



         });
    }

    public void Login()
    {
        int result=0;
        try
        {
            result = Integer.parseInt(new Data().login(lid.getText().toString(),lpasswd.getText().toString()));

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"get 실패", Toast.LENGTH_SHORT).show();
        }
        if(1 == result) //아이디와 비밀번호가 일치 시
        {

            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE); //SharedPreferences에 id와 비밀번호 저장
            SharedPreferences.Editor autoLogin = auto.edit();
            autoLogin.putString("inputId", lid.getText().toString());
            autoLogin.putString("inputPwd", lpasswd.getText().toString());
            autoLogin.commit();

            Toast.makeText(getApplicationContext(),"로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            finish();
        }
        else //아이디 비밀번호 틀린 경우
        {
            Toast.makeText(getApplicationContext(),"로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
