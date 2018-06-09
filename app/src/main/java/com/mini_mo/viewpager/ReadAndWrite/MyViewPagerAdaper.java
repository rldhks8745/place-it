package com.mini_mo.viewpager.ReadAndWrite;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.ImageView;

import com.google.android.gms.common.images.ImageRequest;
import com.mini_mo.viewpager.Store;

import java.util.ArrayList;

/**
 * Created by sasor on 2018-04-29.
 */

public class MyViewPagerAdaper extends FragmentPagerAdapter {

    ArrayList<Bitmap> image;

    public MyViewPagerAdaper(FragmentManager fm){
        super(fm);
        this.image = Store.readboard_image;
    }

    @Override
    public Fragment getItem(int position) {
        return ReadBoard_Image_Fragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return image.size();
    }

}
