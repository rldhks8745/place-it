package com.mini_mo.viewpager.FriendListView;

/*
 * Created by 노현민 on 2018-04-21.
 */

import android.graphics.drawable.Drawable;

public class FriendData {

    private Drawable userIcon;
    private String userName;
    private String state;

    public void setUserIcon ( Drawable icon )
    {
        userIcon = icon;
    }
    public void setUserName ( String name )
    {
        userName = name;
    }
    public void setState ( String stateMessage )
    {
        state = stateMessage;
    }

    public Drawable getUserIcon ()
    {
        return userIcon;
    }
    public String getUserName ()
    {
        return userName;
    }
    public String getState ()
    {
        return state;
    }
}
