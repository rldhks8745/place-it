package com.mini_mo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class ProfileImageActivity extends AppCompatActivity {
    ImageView profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileImage = (ImageView)findViewById(R.id.profile_image);

        Glide.with( this )
                .load( YourPageActivity.getInstance().user_info.user_photo )
                //.apply( new RequestOptions().placeholder( R.drawable.user ).error( R.drawable.user ))
                .into( profileImage );
    }
}
