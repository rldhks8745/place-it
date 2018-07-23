package com.mini_mo.viewpager.ReadAndWrite;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

/**
 * Created by sasor on 2018-04-14.
 */

public class CustomListviewitem {

    private String board_number;
    private String comment_number;
    private String icon;
    private String title;
    private String id;

    public String getIcon() {
        return icon;
    }

    public String getTile() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getComment_number(){
        return comment_number;
    }

    public String getBoard_number(){
        return board_number;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(String photo){
        this.icon = photo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setComment_number(String comment_number){
        this.comment_number = comment_number;
    }

    public void setBoard_number(String board_number){
        this.board_number = board_number;
    }
}
