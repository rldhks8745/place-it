package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class Util{

    public static void Toast(Activity activity, String content){
        Toast.makeText(activity.getApplicationContext(),content,Toast.LENGTH_SHORT).show();
    }

    public static void Log(String title, String content){
        Log.i(title,content);
    }
}
