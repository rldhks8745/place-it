package com.mini_mo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.mini_mo.viewpager.DAO.Data;

import org.json.JSONException;

public class SearchActivity extends AppCompatActivity {

    Data data;

    ImageButton searchbutton;

    EditText searchline;

    ListView listView;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchactivity);

        searchbutton = (ImageButton)findViewById(R.id.searchbutton);
        searchline = (EditText)findViewById(R.id.searchline);

        listView = (ListView)findViewById(R.id.listview);

        try {
            data.search_board(searchline.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
