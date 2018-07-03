package com.mini_mo.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;


public class ProfileImageActivity extends AppCompatActivity {
    ImageView profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileImage = (ImageView)findViewById(R.id.profile_image);
        profileImage.setImageBitmap( YourPageActivity.getInstance().icon_bitmap );
    }
}
