package com.mini_mo.viewpager.ReadAndWrite;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018-05-15.
 */

public class AddressTransformation {

    //지오코더로 위도,경도 <-> 지역주소 바꿔주는 클래스

    static Geocoder geocoder = null;

    public static String getAddress(Activity activity,double latitude, double longitude){

        geocoder = new Geocoder(activity);

        List<Address> list = null;
        try {
            //위도 받기
            //경도 받기

            list = geocoder.getFromLocation(
                    latitude, // 위도
                    longitude, // 경도
                    10); // 얻어올 값의 개수

            if( list.size() != 0 ) {
                String mainlocal = (list.get(0).getLocality().toString()==null ? null:list.get(0).getLocality().toString());
                String sublocal = (list.get(0).getSubLocality()==null ? null:list.get(0).getSubLocality());
                String thoroughfare = (list.get(0).getThoroughfare().toString()==null ? null:list.get(0).getThoroughfare().toString());

                String str = ((mainlocal == null) ? "" : mainlocal) + " " + ((sublocal == null) ? "" : sublocal) + " " + ((thoroughfare == null) ? "" : thoroughfare);

                return str;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "server err - location read err");
        }

        return "위치 수신중";
    }
}
