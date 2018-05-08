package com.mini_mo.viewpager.ReadAndWrite;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by sasor on 2018-04-29.
 */

public class ReadBoardInfo {

    //글보기 클래스 : ReadActivity

    //써야될 자료들
    public String userid;
    public Bitmap profile;
    public int boardnumber;
    public String title;
    public int imgcount;
    public ArrayList<Bitmap> imglist;
    public String location; // EX: 대구광역시 북구 복현동
    public String date; //YYYY-MM-DD HH:MM
    public int like_count;

    //public ReadBoardInfo readBoardInfo(int 게시글번호){

    // return ReadBoardInfo;

    //}

}
