package com.mini_mo.viewpager.ReadAndWrite;

import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by sasor on 2018-04-29.
 */

public class ImageList{

    private ArrayList<ImageButton> imglist ;
    private int max;

    public ImageList(){
        imglist = new ArrayList<>();
        max = 0;
    }

    public ImageList(ArrayList<ImageButton> arr , int count){
        imglist = arr;
        this.max = count;
    }

    public int getSize(){
        return imglist.size();
    }

    public ArrayList<ImageButton> getList(){
        return imglist;
    }

    public ImageView getImage(int i){
        return imglist.get(i);
    }

    public void addImage(ImageButton o){
        imglist.add(o);
    }
    public void removeImage(int i){
        imglist.remove(i);
    }


}
