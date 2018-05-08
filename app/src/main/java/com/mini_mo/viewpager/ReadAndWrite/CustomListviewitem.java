package com.mini_mo.viewpager.ReadAndWrite;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

/**
 * Created by sasor on 2018-04-14.
 */

public class CustomListviewitem {
    private RoundedBitmapDrawable icon;
    private String title;
    private String date;

    public RoundedBitmapDrawable getIcon() {
        return icon;
    }

    public String getTile() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(RoundedBitmapDrawable b){
        this.icon = b;
    }


    public void setDate(String date) {
        this.date = date;
    }
}
