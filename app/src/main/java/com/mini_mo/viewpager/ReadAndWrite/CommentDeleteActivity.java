package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.R;

import org.json.JSONException;

public class CommentDeleteActivity extends Activity {

    EditText password;
    Button delete, cancel;
    String boardNum, commentNum, pass;

    Data data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.rnw_comment_delete);

        data = new Data();

        password = (EditText) findViewById(R.id.password);
        delete = (Button) findViewById(R.id.delete);
        cancel = (Button) findViewById(R.id.cancel);

        boardNum = getIntent().getStringExtra("board_num");
        commentNum = getIntent().getStringExtra("comment_num");
        pass = getIntent().getStringExtra("pass");

        Log.i("보드 넘", boardNum);
        Log.i("코멘트 넘", commentNum);
        Log.i("비밀번호", pass);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getpass = password.getText().toString();
                if(getpass.equals(pass)) {
                    try {
                        data.deleteComment(Integer.valueOf(boardNum), Integer.valueOf(commentNum));
                        Toast.makeText(getApplicationContext(), "댓글 삭제 완료!", Toast.LENGTH_SHORT).show();

                        setResult(777);

                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
