package com.mini_mo.viewpager.DAO;


import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Data {


    public Data() {


    }

    public String test(String photo_url)
    {
        Log.i("test",photo_url);
        new Imagehttp(photo_url, "14", "board_num").execute();
        Log.i("test",photo_url+"asdasdafas");

        return "-12";
    }


    public ReadBoardInfo readBoardInfo(String board_num) throws JSONException
    {
        ReadBoardInfo rbi = new ReadBoardInfo();

        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject b_n = new JSONObject();
        obj.put("flag","ReadBoardInfo");
        b_n.put("Board_num",board_num);
        obj.put("Board_Info_data",b_n);

        try
        {
            result = new JSONObject(new ConHttpJson().execute(obj).get().toString());

            result.getString("result");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /*
        j.put("board_num",rs.getInt(1));
        j.put("content",rs.getString(2));
        j.put("date",rs.getString(3));
        j.put("good",rs.getInt(4));
        j.put("hits",rs.getInt(5));
        j.put("latitude",rs.getDouble(6));
        j.put("longitude",rs.getDouble(7));
        j.put("user_id",rs.getString(8));
        j.put("user_photo",rs.getString(9));

*/

         String userid;
         String profile;
         int boardnumber;
         String title;
         int imgcount;
         ArrayList<Bitmap> imglist;
         String location; // EX: 대구광역시 북구 복현동
         String date; //YYYY-MM-DD HH:MM
         int like_count;

        if(result != null)
        {
            rbi.board_num = result.getInt("board_num");
            rbi.content = result.getString("content");
            rbi.date = result.getString("date");
            rbi.good = result.getInt("good");
            rbi.hits = result.getInt("hits");
            rbi.latitude = result.getDouble("latitude");
            rbi.longitude = result.getDouble("longitude");
            rbi.user_id = result.getString("user_id");
            rbi.user_photo = result.getString("user_photo");

            if(result.getString("b_photos")!=null)
            {
                JSONArray ja = new JSONArray(result.getString("b_photos"));
                for(int i = 0;ja.getJSONObject(i) != null;i++)
                {
                    rbi.b_photos.add(ja.getJSONObject(i).getString("board_photo"));  //ja.getJSONObject(i)
                }
            }
        }

        return rbi;
    }



    public String writeBorard(String Content, String user_name, String tag, double latitude ,double longitute, ArrayList<String> urls) throws JSONException {
        String r = "-3";
        String board_number = "-30";

        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject b_d = new JSONObject();
        obj.put("flag","write_board");
        b_d.put("content", Content);
        b_d.put("user_name",user_name);
        b_d.put("tag",tag);
        b_d.put("latitude",latitude);
        b_d.put("longitude",longitute);
        obj.put("Board_data", b_d);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(result != null)
        {

            board_number = result.getString("result");
            if((Integer.parseInt(board_number) > 0))
            {
                for(String s : urls)
                {
                    new Imagehttp(s, board_number, "board_num").execute();
                }
                r = "1";
            }
        }

        return r;
    }



    public String login(String ID, String passwd) throws JSONException
    {
        JSONObject jobj = new JSONObject();
        JSONObject result = null;
        String r = "-3";

        JSONObject login_data = new JSONObject();

        login_data.put("ID",ID);
        login_data.put("passwd",passwd);

        jobj.put("flag","Login");
        jobj.put("Login_data", login_data);

        try {
            result = new ConHttpJson().execute(jobj).get();

        }
        catch (Exception e)
        {

        }

        if(result != null)
        {

            r = result.getString("result");
        }

        return r;
    }

    public Bitmap test_bitmap(Bitmap bit)
    {



        return bit;
    }



}
