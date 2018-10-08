package com.mini_mo.viewpager.ReadAndWrite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import com.mini_mo.viewpager.R;

public class CommentDeleteActivity extends AppCompatActivity {

    EditText password;
    Button delete, cancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rnw_comment_delete);

        password = (EditText) findViewById(R.id.password);
        delete = (Button) findViewById(R.id.delete);
        cancel = (Button) findViewById(R.id.cancel);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
