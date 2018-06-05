package com.mini_mo.viewpager.ReadAndWrite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mini_mo.viewpager.R;

public class SaveLoacateActivity extends AppCompatActivity implements View.OnClickListener {

    EditText title;
    Button save,cancle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_activity_savelocate);

        title = (EditText)findViewById(R.id.title);
        save = (Button)findViewById(R.id.save);
        cancle = (Button)findViewById(R.id.cancle);

        save.setOnClickListener(this);
        cancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.save:

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
}
