package com.mini_mo.viewpager.ReadAndWrite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sasor on 2018-04-29.
 */

public class MyViewPagerAdaper extends FragmentPagerAdapter {

    int[] image;

    public MyViewPagerAdaper(FragmentManager fm, int[] image){
        super(fm);
        this.image = image;
    }

    @Override
    public Fragment getItem(int position) {
        return ReadBoard_Image_Fragment.newInstance(image[position]);
    }

    @Override
    public int getCount() {
        return image.length;
    }

}
