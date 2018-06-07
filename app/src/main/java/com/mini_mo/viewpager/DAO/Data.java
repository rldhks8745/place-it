package com.mini_mo.viewpager.DAO;


import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Data {


    public Data() {


    }


    public String count_friends(String user_name) throws JSONException
    {
        JSONObject jobj = new JSONObject();
        JSONObject result = null;
        String r = "-3";

        JSONObject login_data = new JSONObject();

        login_data.put("user_name",user_name);

        jobj.put("flag","count_friends");
        jobj.put("count_friends_data", login_data);

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

    public String change_board(int board_num, String content, String tag,ArrayList<String> addPhoto,ArrayList<String> delPhoto) throws JSONException {
        String r = "-3";
        String board_number = "-30";

        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject b_d = new JSONObject();
        obj.put("flag", "change_board");
        b_d.put("content", content);
        b_d.put("board_num", board_num);
        b_d.put("tag", tag);
        obj.put("change_board_data", b_d);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        r = "1";

        for (int i = 0; i < delPhoto.size() ; i++)
        {
            obj = new JSONObject();
            b_d = new JSONObject();
            obj.put("flag", "delete_board_photo");
            b_d.put("photo_name", delPhoto.get(i));
            obj.put("delete_board_photo_data", b_d);

            try {
                result = new ConHttpJson().execute(obj).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        for(int i = 0 ;i < addPhoto.size(); i++)
        {
            //board_number = result.getString("result");

            new Imagehttp(addPhoto.get(i), board_num+"", "board_num").execute();
        }

        return r;
    }


    public ArrayList<Save_List> load_locate (String id) throws JSONException
    {
        ArrayList<Save_List> fl = new ArrayList<Save_List>();
        int f_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "load_locate");
        u_n.put("id", id);

        obj.put("load_locate_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        f_cnt = result.getInt("save_locate_count");

        if (f_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("save_locate_list");
            for (int i = 0; i < f_cnt; i++)
            {
                Save_List t = new Save_List();
                JSONObject tjo = tmp.getJSONObject(i);
                t.save_number = tjo.getInt("save_number");
                t.latitude = tjo.getDouble("latitude");
                t.longitude = tjo.getDouble("longitude");
                t.massage = tjo.getString("massage");

                fl.add(t);
            }
        }

        return fl;

    }

    public String delete_locate(int save_num) throws JSONException
    {

        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject c_d = new JSONObject();

        obj.put("flag", "delete_locate");
        c_d.put("save_num",save_num);
        obj.put("delete_locate_data",c_d);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public String save_locate(String id, double latitude,double longitude,String massage) throws JSONException
    {
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject c_d = new JSONObject();

        obj.put("flag", "save_locate");
        c_d.put("id",id);
        c_d.put("lat", latitude);
        c_d.put("lon", longitude);
        c_d.put("massage", massage);
        obj.put("save_locate_data",c_d);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public ArrayList<ListViewItemData> read_board_camera (double latitude, double longitude) throws JSONException
    {
        ArrayList<ListViewItemData> fl = new ArrayList<ListViewItemData>();
        int f_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "read_board_camera");
        u_n.put("lat", latitude);
        u_n.put("lon", longitude);

        obj.put("read_board_camera_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        f_cnt = result.getInt("read_board_camera_count");

        if (f_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("read_board_list_camera");
            for (int i = 0; i < f_cnt; i++)
            {
                ListViewItemData t = new ListViewItemData();
                JSONObject tjo = tmp.getJSONObject(i);
                t.board_num = tjo.getInt("board_num");
                t.content = tjo.getString("content");
                t.date_board = tjo.getString("date_board");
                t.good = tjo.getInt("good");
                t.latitude = tjo.getDouble("board_latitude");
                t.longitude = tjo.getDouble("board_longitude");
                t.user_id = tjo.getString("user_id");
                t.user_photo = tjo.getString("user_photo");

                fl.add(t);
            }
        }

        return fl;

    }

    public String membership(String user_id, String passwd, String birth) throws JSONException
    {
        JSONObject jobj = new JSONObject();
        JSONObject result = null;
        String r = "-3";

        JSONObject login_data = new JSONObject();

        login_data.put("user_id",user_id);
        login_data.put("passwd",passwd);
        login_data.put("birth",birth);

        jobj.put("flag","membership");
        jobj.put("membership_data", login_data);

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


    public String check_Id(String ID) throws JSONException
    {
        JSONObject jobj = new JSONObject();
        JSONObject result = null;
        String r = "-3";

        JSONObject login_data = new JSONObject();

        login_data.put("ID",ID);

        jobj.put("flag","check_Id");
        jobj.put("check_Id_data", login_data);

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

    public User_Info read_myPage(String userId)  throws JSONException
    {
        User_Info fl = new User_Info();
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "read_myPage");
        u_n.put("user_name", userId);

        obj.put("read_myPage_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));


        fl.user_id = result.getString("user_id");
        fl.date_birth = result.getString("date_birth");
        fl.date_member = result.getString("date_member");
        fl.user_photo = result.getString("user_photo");
        fl.massage = result.getString("massage");



        return fl;
    }

    public ArrayList<ListViewItemData> read_myBoard (String userId, int limit) throws JSONException
    {
        ArrayList<ListViewItemData> fl = new ArrayList<ListViewItemData>();
        int f_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "read_myboard");
        u_n.put("user_name", userId);
        u_n.put("limit", limit);

        obj.put("read_myboard_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        f_cnt = result.getInt("read_board_list_count");

        if (f_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("read_board_list_data");
            for (int i = 0; i < f_cnt; i++)
            {
                ListViewItemData t = new ListViewItemData();
                JSONObject tjo = tmp.getJSONObject(i);
                t.board_num = tjo.getInt("board_num");
                t.content = tjo.getString("content");
                t.date_board = tjo.getString("date_board");
                t.good = tjo.getInt("good");
                t.latitude = tjo.getDouble("board_latitude");
                t.longitude = tjo.getDouble("board_longitude");
                t.user_id = tjo.getString("user_id");
                t.user_photo = tjo.getString("user_photo");

                fl.add(t);
            }
        }

        return fl;
    }

    public ArrayList<ListViewItemData> read_board_list (double min_lat, double min_lng, double max_lat, double max_lng) throws JSONException
    {
        ArrayList<ListViewItemData> fl = new ArrayList<ListViewItemData>();
        int f_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "read_board_list");
        u_n.put("min_lat", min_lat);
        u_n.put("max_lat", max_lat);
        u_n.put("min_lon", min_lng);
        u_n.put("max_lon", max_lng);

        obj.put("read_board_list_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        f_cnt = result.getInt("read_board_list_count");

        if (f_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("read_board_list_data");
            for (int i = 0; i < f_cnt; i++)
            {
                ListViewItemData t = new ListViewItemData();
                JSONObject tjo = tmp.getJSONObject(i);
                t.board_num = tjo.getInt("board_num");
                t.content = tjo.getString("content");
                t.date_board = tjo.getString("date_board");
                t.good = tjo.getInt("good");
                t.latitude = tjo.getDouble("board_latitude");
                t.longitude = tjo.getDouble("board_longitude");
                t.user_id = tjo.getString("user_id");
                t.user_photo = tjo.getString("user_photo");

                fl.add(t);
            }
        }

        return fl;

    }


    public ArrayList<Board_Location> read_board_location(double min_lat, double max_lat, double min_lon, double max_lon) throws JSONException
    {
        ArrayList<Board_Location> fl = new ArrayList<Board_Location>();
        int f_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "read_board_location");
        u_n.put("min_lat", min_lat);
        u_n.put("max_lat", max_lat);
        u_n.put("min_lon", min_lon);
        u_n.put("max_lon", max_lon);

        obj.put("read_board_location_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        f_cnt = result.getInt("read_board_location_count");

        if (f_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("read_board_location_data");
            for (int i = 0; i < f_cnt; i++)
            {
                Board_Location t = new Board_Location();
                JSONObject tjo = tmp.getJSONObject(i);
                t.latitude = tjo.getDouble("board_latitude");
                t.longitude = tjo.getDouble("board_longitude");
                t.board_count = tjo.getInt("board_count");

                fl.add(t);
            }
        }

        return fl;
    }



    public String add_friends(String id_applicant, String id_respondent) throws JSONException
    {
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject f_d = new JSONObject();

        obj.put("flag", "add_friends");
        f_d.put("id_applicant",id_applicant);
        f_d.put("id_respondent", id_respondent);
        obj.put("add_friends_data",f_d);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result.getString("result");
    }


    public ArrayList<FriendsList> readFriends(String user_name) throws JSONException
    {
        ArrayList<FriendsList> fl = new ArrayList<FriendsList>();
        int f_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject u_n = new JSONObject();

        obj.put("flag", "read_friends");
        u_n.put("user_name", user_name);
        obj.put("read_friends_data", u_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        f_cnt = result.getInt("friends_count");

        if (f_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("read_friends_data");
            for (int i = 0; i < f_cnt; i++)
            {
                FriendsList t = new FriendsList();
                JSONObject tjo = tmp.getJSONObject(i);
                t.user_id = tjo.getString("user_id");
                t.user_photo = tjo.getString("user_photo");
                t.message = tjo.getString("message");

                fl.add(t);
            }
        }

        return fl;
    }



    public ArrayList<ReadCommentInfo> readComment(String board_num) throws JSONException {
        ArrayList<ReadCommentInfo> rci = new ArrayList<ReadCommentInfo>();
        int com_cnt = 0;
        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject b_n = new JSONObject();

        obj.put("flag", "read_comment");
        b_n.put("Board_num", board_num);
        obj.put("read_comment_data", b_n);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        result = new JSONObject(result.getString("result"));

        com_cnt = result.getInt("com_count");

        if (com_cnt >0)
        {
            JSONArray tmp = result.getJSONArray("read_comment_data");
            for (int i = 0; i < com_cnt; i++)
            {
                ReadCommentInfo t = new ReadCommentInfo();
                JSONObject tjo = tmp.getJSONObject(i);
                t.board_num = tjo.getInt("board_num")+"";
                t.comment_num = tjo.getInt("comment_num")+"";
                t.comment_date = tjo.getString("comment_date");
                t.comment_content = tjo.getString("comment_content");
                t.comment_id = tjo.getString("comment_id");
                t.user_photo = tjo.getString("user_photo");

                rci.add(t);
            }
        }

        return rci;

    }


    public String writeComment(String board_num, String user_id, String content) throws JSONException
    {

        JSONObject result = null;
        JSONObject obj = new JSONObject();
        JSONObject c_d = new JSONObject();

        obj.put("flag", "write_comment");
        c_d.put("Board_num",board_num);
        c_d.put("user_name", user_id);
        c_d.put("content", content);
        obj.put("write_comment_data",c_d);

        try {
            result = new ConHttpJson().execute(obj).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result.toString();
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
            result = new ConHttpJson().execute(obj).get();

            //result.getString("result");
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

        result = new JSONObject(result.getString("result"));

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

            int count = result.getInt("img_count");

            if(count > 0)
            {
                JSONArray ja = new JSONArray(result.getString("b_photos"));
                rbi.b_photos = new ArrayList<String>();
                for(int i = 0; i < count;i++) //ja.getJSONObject(i)
                {
                    rbi.b_photos.add(ja.getJSONObject(i).getString("board_photo"));  //ja.getJSONObject(i)
                }
            }
        }


        return rbi;
    }



    public String writeBorard(String Content, String user_name, String tag, double latitude , double longitute, ArrayList<String> urls) throws JSONException {
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


}
