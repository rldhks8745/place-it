package com.mini_mo.viewpager.DAO;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownServiceException;

public class Imagehttp extends AsyncTask<String ,String ,String> {
    String urlString = null;
    String fileName = null;
    String num = null;
    String type = null;
    JSONObject j = null;
    HTTPURL url = new HTTPURL();

    public Imagehttp(String fileName, String num, String type) {
        Log.i("test","HTTP1");
        this.fileName = fileName;
        this.num = num;
        this.type = type;
        Log.i("test","HTTP생성자");
        j = new JSONObject();  // 플래그 json 으로 파싱
        Log.i("test","HTTP생성자2");
        try {
            j.put("flag", type);
            Log.i("test","HTTP생성자3");
            j.put("num",num);
        } catch (JSONException e) {
            Log.i("test","HTTP JSONError");
            e.printStackTrace();
        }
        Log.i("test","HTTP생성자4");

    }


    @Override
    protected String doInBackground(String... strings) {
        Log.i("test","HTTP2");
        String r = "-10";

        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        try {
            Log.i("test","HTTP2"+fileName);
            File sourceFile = new File(fileName);

            DataOutputStream dos;

            if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist :" + fileName);

            } else {

                FileInputStream mFileInputStream = new FileInputStream(sourceFile);

                URL connectUrl = new URL(url.addrs+url.imageurl);
                Log.i("test","connection miss");

                // open connection

                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();

                conn.setDoInput(true);

                conn.setDoOutput(true);

                conn.setUseCaches(false);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Accept-Charset","UTF-8");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                conn.setRequestProperty("json", j.toString());

                //int recode = conn.getResponseCode();

                // write data
               // OutputStream os = conn.getOutputStream();
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"json\""+lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(j.toString());
                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);


                int bytesAvailable = mFileInputStream.available();

                int maxBufferSize = 1024 * 1024;

                int bufferSize = Math.min(bytesAvailable, maxBufferSize);


                byte[] buffer = new byte[bufferSize];

                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                Log.i("test","HTTP4");


                // read image

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);

                    bytesAvailable = mFileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                }


                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                mFileInputStream.close();

                dos.flush(); // finish upload...

                Log.i("test","HTTP5");

                if (conn.getResponseCode() == 200) {

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while ((line = reader.readLine()) != null) {

                        stringBuffer.append(line);

                    }
                    r = stringBuffer.toString();


                }

                Log.i("test","HTTP6");
                mFileInputStream.close();

                dos.close();
                Log.i("test","HTTP7");
            }

        } catch (UnknownServiceException e) {

            Log.d("test",e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {

            Log.d("test",e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {

            Log.d("test",e.getMessage());
            e.printStackTrace();

        }
        return r;
    }
}


