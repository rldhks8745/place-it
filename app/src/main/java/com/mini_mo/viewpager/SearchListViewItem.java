package com.mini_mo.viewpager;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

public class SearchListViewItem{

    private int board_num;
    private String content;
    private String date_board;
    private int good;
    public double latitude;
    public double longitude;
    private String user_id;
    private String user_photo;
    private int comment;

    public SearchListViewItem(int board_num,String content,String date_board, int good, double latitude, double longitude, String user_id, String user_photo,int commentsize){
        this.board_num = board_num;
        this.content = content;
        this.date_board = date_board;
        this.good = good;
        this.latitude = latitude;
        this.longitude =longitude;
        this.user_id = user_id;
        this.user_photo = user_photo;
        comment = commentsize;
    }

    public int getBoard_num(){
        return board_num;
    }

    public String getContent(){
        return content;
    }

    public String getDate_board(){
        return date_board;
    }

    public int getGood(){
        return good;
    }

    public String getUser_id(){
        return user_id;
    }

    public String getUser_photo(){
        return user_photo;
    }

    public double[] getLocate(){

        double[] doubles = new double[2];

        doubles[0]=this.latitude;
        doubles[1]=this.longitude;

        return doubles;
    }

    public int getCommentSize(){
        return comment;
    }

}
