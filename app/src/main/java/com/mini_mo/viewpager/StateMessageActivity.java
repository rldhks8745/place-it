package com.mini_mo.viewpager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mini_mo.viewpager.DAO.Data;

import org.json.JSONException;

/**
 * Created by userForGame on 2018-06-09.
 */

public class StateMessageActivity extends Activity {

    EditText edit;

    Button btChange;
    Button btCancel;

    Intent getIntent;

    String loginId;
    String yourId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_statemessage);

        getIntent = getIntent();
        loginId = getIntent.getStringExtra("loginId"); // 사용자 id

        edit = (EditText)findViewById(R.id.editText_popup);
        edit.setText( getIntent.getStringExtra( "stateMessage" ) );

        btChange = (Button)findViewById(R.id.change_popup);
        btCancel = (Button)findViewById(R.id.cancel_popup);

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String str = edit.getText().toString();

                try {
                    new Data().change_massage( str, loginId );
                    Toast.makeText( StateMessageActivity.this, "상태메세지 변경 완료", Toast.LENGTH_SHORT);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( StateMessageActivity.this, "상태메세지 변경 실패", Toast.LENGTH_SHORT);
                }

                StateMessageActivity.this.finish();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                StateMessageActivity.this.finish();
            }
        });
    }
}
