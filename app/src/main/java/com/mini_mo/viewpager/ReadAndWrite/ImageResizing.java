package com.mini_mo.viewpager.ReadAndWrite;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018-05-16.
 */

public class ImageResizing {

    public static Bitmap ReSizing(String pathname){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        Bitmap bmp = BitmapFactory.decodeFile(pathname, options);

        return bmp;
    }

    public static Bitmap ReSizing(ContentResolver contentResolver, Uri uri){
        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inSampleSize = 6;
        opt.inDither = true;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inTempStorage = new byte[32 * 1024];
        Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, opt);

        return bmp;
    }


}
