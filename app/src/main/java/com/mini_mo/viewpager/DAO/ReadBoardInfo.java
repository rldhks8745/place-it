package com.mini_mo.viewpager.DAO;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by sasor on 2018-04-29.
 */

public class ReadBoardInfo {

    //글보기 클래스 : ReadActivity

    //써야될 자료들
    public String user_id;
    public String user_photo;
    public int board_num;
    public String content;
    public ArrayList<String> b_photos;
    public double latitude; // EX: 대구광역시 북구 복현동
    public double longitude;
    public String date; //YYYY-MM-DD HH:MM
    public int good;
    public int hits;

    //public ReadBoardInfo readBoardInfo(int 게시글번호){

    // return ReadBoardInfo;

    //}

}
