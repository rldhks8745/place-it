package com.mini_mo.viewpager.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.R;

import org.json.JSONException;

public class Membershipactivity extends AppCompatActivity {

    Button cbutton;
    Button accept,checkid;
    EditText mid , mpasswd,rpasswd,birth;
    boolean ch_id = false;
    boolean ch_pwd = false;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.lyh_membership);

        cbutton = (Button) findViewById(R.id.cbutton);
        accept = (Button) findViewById(R.id.accept);
        mid = (EditText) findViewById(R.id.mid);
        mpasswd = (EditText) findViewById(R.id.mpasswd);
        rpasswd = (EditText) findViewById(R.id.rpasswd);
        birth = (EditText) findViewById(R.id.birth);
        checkid = (Button) findViewById(R.id.checkid);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ch_id && ch_pwd && (birth.getText().toString() != null))
                {
                    try {
                        new Data().membership(mid.getText().toString(),mpasswd.getText().toString(),birth.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"아이디나 비밀번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        rpasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mpasswd.getText().toString().equals(rpasswd.getText().toString()))
                {
                    ch_pwd = true;
                }
                else
                {
                    ch_pwd = false;
                }

            }
        });

        mid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ch_id = false;

            }
        });


        cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        checkid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mid.getText().toString() != null)
                {
                    int i = 0;
                    try {
                        i = Integer.parseInt(new Data().check_Id(mid.getText().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if( i == 1)
                    {
                        ch_id = true;
                        Toast.makeText(getApplicationContext(),"사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
