package com.mini_mo.viewpager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;


import com.mini_mo.viewpager.DAO.ListViewItemData;

import java.util.ArrayList;

/*
 * Created by Administrator on 2018-05-15.
 */

public class Store {

    public static int board_num = 0;
    public static RoundedBitmapDrawable myprofile_img=null;

    public static ArrayList<Bitmap> readboard_image = new ArrayList<>();
    public static ArrayList<ListViewItemData> sendboard = new ArrayList<>();
    public static ArrayList<ListViewItemData> sendcluster = new ArrayList<>();
    public static boolean setlist = false;
    public static String userid = "";

    public static double latitude=0.0 , longitude=0.0;
    public static String content = "";
    public static ArrayList<Uri> arr_uri = new ArrayList<>();
}
