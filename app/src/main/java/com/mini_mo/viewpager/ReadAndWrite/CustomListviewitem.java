package com.mini_mo.viewpager.ReadAndWrite;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

/**
 * Created by sasor on 2018-04-14.
 */

public class CustomListviewitem {

    private String board_number;
    private String comment_number;
    private String comment_id;
    private String userphoto;
    private int guestphoto;
    private String nickname;
    private String title;
    private String date;
    private String photo;
    private String pass;

    public String getComment_id() {
        return comment_id;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public int getGuestphoto() {
        return guestphoto;
    }

    public String getTile() {
        return title;
    }

    public String getNickname() {
        return nickname;
    }

    public String getComment_number(){
        return comment_number;
    }

    public String getBoard_number(){
        return board_number;
    }

    public String getDate(){
        return date;
    }

    public String getPhoto(){
        return photo;
    }

    public String getPass(){
        return pass;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGuestphoto(int guestphoto){
        this.guestphoto = guestphoto;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setComment_number(String comment_number){
        this.comment_number = comment_number;
    }

    public void setBoard_number(String board_number){
        this.board_number = board_number;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setPass(String pass){
        this.pass = pass;
    }
}
