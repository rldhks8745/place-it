package com.mini_mo.viewpager.ReadAndWrite;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by sasor on 2018-04-29.
 */

public class ImageList{

    private ArrayList<ImageView> imglist ;
    private int max;

    public ImageList(){
        imglist = new ArrayList<>();
        max = 0;
    }

    public ImageList(ArrayList<ImageView> arr , int count){
        imglist = arr;
        this.max = count;
    }

    public int getSize(){
        return imglist.size();
    }

    public ArrayList<ImageView> getList(){
        return imglist;
    }

    public ImageView getListresult(int i){
        return imglist.get(i);
    }

    public void addListresult(ImageView o){
        imglist.add(o);
    }
    public void removeListresult(int i){
        imglist.remove(i);
    }


}
