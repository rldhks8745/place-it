package com.mini_mo.viewpager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.NestedScrollView;
import android.widget.ImageView;


import com.google.android.gms.maps.model.LatLng;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.ListView.RecyclerListView;

import java.util.ArrayList;

/*
 * Created by Administrator on 2018-05-15.
 */

public class Store {
    public static double display_width = 0.0; //휴대폰의 가로
    public static double display_height = 0.0; //휴대폰의 세로

    public static boolean check = false;
    public static int board_num = 0; //게시글 번호
    public static int testint = 0;
    public static String myprofile_img=null; //로그인한 사람의 유저이미지

    public static ArrayList<Bitmap> readboard_image = new ArrayList<>();
    public static String comment_img = null;
    public static ArrayList<ListViewItemData> sendboard = new ArrayList<>();
    public static boolean setlist = false;
    public static String userid = "";

    public static double latitude=0.0 , longitude=0.0;
    public static String content = "";
    public static ArrayList<Uri> arr_uri = new ArrayList<>();
    public static LatLng point =null;

    public static RecyclerListView rlv=null;
    public static NestedScrollView nsv=null;
}
