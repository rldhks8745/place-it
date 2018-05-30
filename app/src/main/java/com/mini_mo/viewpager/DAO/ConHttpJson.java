package com.mini_mo.viewpager.DAO;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConHttpJson extends AsyncTask<JSONObject,JSONObject ,JSONObject>
{
    String sendMsg,receiveMsg="-2";
    HTTPURL jurl = new HTTPURL();

    @Override
    protected JSONObject doInBackground(JSONObject... jsonObjects)
    {
        /*
        JSONObject j = new JSONObject();

        try {
            j.put("ID","aaa");
            j.put("passwd","123");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    */
        try
        {
            String str;
            URL url = new URL(jurl.addrs+jurl.jsourl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            //conn.setRequestProperty("Accept","application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept-Charset","EUC-KR");
            conn.setRequestProperty("Content-Type","application/json; charset=EUC-KR");// charset=EUC-KR"
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(),"EUC-KR");


            //OutputStream osw = conn.getOutputStream();


            //sendMsg = j;
            osw.write(jsonObjects[0].toString());
            //osw.write(j.toString().getBytes("EUC-KR"));
            osw.flush();

            if(conn.getResponseCode() == conn.HTTP_OK)
            {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(),"EUC-KR");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                while ((str = reader.readLine()) != null)
                {
                    buffer.append(str);
                }

                receiveMsg = buffer.toString();
            }
            else
            {
                Log.i("통신결과",conn.getResponseCode()+" 에러");
            }
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        JSONObject jd = new JSONObject();
        try {
            jd.put("result",receiveMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jd;
    }

}
