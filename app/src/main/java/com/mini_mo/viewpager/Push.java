package com.mini_mo.viewpager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mini_mo.viewpager.DAO.Data;
import com.mini_mo.viewpager.DAO.ListViewItemData;
import com.mini_mo.viewpager.Setting.AlarmSetting;

import org.json.JSONException;

import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Push extends Service implements Runnable {
    private Data data;
    private NotificationCompat.Builder mbuilder;
    private int category = 1; //알림띄울 카테고리
    private boolean is_push = false; //알림 기능 쓸지 여부.
    private double notification_region = 0.001; //알림 범위
    private int notification_time = 1; //알림 주기
    private double notification_distance =0.05; // 얼마나 이동했을 때 통신 할 것인지 설정.
    private double before_latitude = 0,before_longitude = 0; // 얼마나 움직였는지 비교 할 이전의 좌표
    private static HashMap<Integer,Date> push_check = new HashMap<Integer, Date>();
    private Handler mHandler;
    private boolean mIsRunning;
    private Bitmap mb;
    private int mStartId = 0;

    private static final int REBOOT_DELAY_TIMER = 10 * 1000;
    private static int LOCATION_UPDATE_DELAY = 1 * 60 * 1000; // 5 * 60 * 1000



    public Push() {
        // 나중에 옵션 구현하고 나면 여기나 onCreate에서 옵션있는 값 불러와서 처음값을 초기화 해 줌.
    }

    @Override
    public void onCreate()
    {
        Log.i("PersistentService", "onCreate()");
        unregisterRestartAlarm();

        super.onCreate();

        data = new Data();
        mIsRunning = false;
    }

    @Override
    public void onDestroy() {

        // 서비스가 죽었을때 알람 등록
        Log.i("PersistentService", "onDestroy()");
        registerRestartAlarm();

        super.onDestroy();

        mIsRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Log.i("PersistentService", "onStart()");
        super.onStartCommand(intent, flags, startId);

        mStartId = startId;

        // 5분후에 시작
        mHandler = new Handler();
        mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
        mIsRunning = true;


        return START_STICKY;
    }

    @Override
    public void run() {

        Log.i("Push_run", "run()");

        if(!mIsRunning)
        {
            Log.i("PersistentService", "run(), mIsRunning is false");
            Log.i("PersistentService", "run(), alarm service end");
            return;

        } else {

            Log.i("PersistentService", "run(), mIsRunning is true");
            Log.i("PersistentService", "run(), alarm repeat after five minutes");

            if(AlarmSetting.alarmOn)
            {
                Log.i("Run_alarmOn", "ture");
                function();
            }
            else
            {
                Log.i("Run_alarmOn", "false");
            }
            mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
            mIsRunning = true;
        }

    }

    private void function() {

        Log.i("function", "========================");
        Log.i("function", "function_start");
        notification_region = AlarmSetting.selectedDistance * 0.0001;


        double latitude=35.8968173,longitude=128.6214437; //매번 갱신해야 하기 때문에 이건 여기에 있는게 맞는 것 같음.

        ArrayList<ListViewItemData> lvi = new ArrayList<ListViewItemData>();

        //위치가 얼마나 바뀌었는지 확인하고 일정 범위를 벗어났으면 서버와 통신.


        Log.i("push 통신 전", "되나");

        try {
            lvi = data.read_board_list(latitude-notification_region, latitude + notification_region ,longitude - notification_region, longitude + notification_region);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("lvi결과", lvi.size()+"");

        deleteEntiredPush(push_check);

        //알림 삭제 시간. 글 번호. 새로 받은 정보에 현재 글 목록이 있으면 중복처리 해 주어야 함.

        String content=null,user_photo=null;
        int push_count = 0;

        for(int i = 0 ; i < lvi.size(); i++)
        {
            Log.i("lvi", i + "인덱스의 카테고리 = "+  lvi.get(i).category);
            if(lvi.get(i).category == category) //설정해 둔 카테고리와 같은지 비교
            {
                if(!push_check.containsKey(lvi.get(i).board_num)) //board_num이 알람이 떴었는지 체크하고 안떴었으면 안으로 들어감.
                {
                    Log.i("lvi", i + "인덱스의 board_num = " +lvi.get(i).board_num);
                    content = lvi.get(i).content;
                    user_photo = lvi.get(i).user_photo;
                    push_check.put(lvi.get(i).board_num, new Date());
                    push_count++;
                }
            }

        }
        if(push_count > 0)
        {
            sendNotification(content,user_photo,push_count);
        }


        //if문이나 등등으로 해당사항에 해당되면 팝업 ㄱㄱㄱ



        //sleep and 반복

        Log.i("function", "function_end");

    }



    private void sendNotification(String messageBody,String user_photo,int push_count)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Glide.with(getApplicationContext()).asBitmap().load(user_photo)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mb = resource;
                    }
                });

        NotificationManager mNotificaionManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent mp = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        if(Build.VERSION.SDK_INT >= 26)
        {
            NotificationChannel mChannel = new NotificationChannel("andokdcapp","andokdcapp", NotificationManager.IMPORTANCE_DEFAULT);
            mNotificaionManager.createNotificationChannel(mChannel);
            mbuilder = new NotificationCompat.Builder(this,mChannel.getId());
        }
        else
        {
            mbuilder = new NotificationCompat.Builder(this);
        }

        mbuilder.setSmallIcon(R.drawable.postit)
                .setContentTitle(push_count+"개의 알림이 있습니다.")
                .setContentText(messageBody)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLargeIcon(mb)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(mp);




        mNotificaionManager.notify(0, mbuilder.build());

        mb = null;
    }

    private void deleteEntiredPush(HashMap<Integer,Date> pc) //삭제할 항목들의 index를 반환
    {
        ArrayList<Integer> saveKey= new ArrayList<Integer>();
        for(Integer key : pc.keySet())
        {
            Date reqDateStr = pc.get(key);
            Log.i("reqDate", reqDateStr.toString());
            //현재시간 Date
            Date curDate = new Date();
            Log.i("curDate", curDate.toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//YYYYMMddHHmm
            //요청시간을 Date로 parsing 후 time가져오기
            Date reqDate = null;
            try {
              // curDate = dateFormat.parse(curDate.toString());

             //   Log.i("curDate", curDate.toString());

               // reqDate = dateFormat.parse(reqDateStr.toString());

              //  Log.i("reqDate", reqDate.toString());

            long reqDateTime = reqDateStr.getTime();

            //현재시간을 요청시간의 형태로 format 후 time 가져오기
          //  curDate = dateFormat.parse(dateFormat.format(curDate));
                long curDateTime = curDate.getTime();

            //분으로 표현
            //long minute = curDate.compareTo(reqDateStr);
                long minusTime = (curDateTime-reqDateTime)/1000;

                Log.i("minute", minusTime+"");
                Log.i("빼기",curDateTime-reqDateTime+"");

            if(minusTime >= (notification_time*60)) // minusTime이 초 단위기 때문에 * 60을 해줌
            {
                saveKey.add(key);
            }

            } catch (Exception e) {
                //ParseException e
                e.printStackTrace();
            }
        }

        for(int key : saveKey)
        {
            pc.remove(key);
            Log.i("deleteKey",key+"");
        }
    }

    private void registerRestartAlarm()
    {

        Log.i("PersistentService", "registerRestartAlarm()");

        Intent intent = new Intent(Push.this, BootReceiver.class);
        intent.setAction(BootReceiver.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(Push.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += REBOOT_DELAY_TIMER; // 10초 후에 알람이벤트 발생

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,REBOOT_DELAY_TIMER, sender);

    }
    private void unregisterRestartAlarm()
    {

        Log.i("PersistentService", "unregisterRestartAlarm()");
        Intent intent = new Intent(Push.this, BootReceiver.class);
        intent.setAction(BootReceiver.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(Push.this, 0, intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    public double getNotification_distance()
    {
        return notification_distance;
    }
    public void setNotification_distance(double distance)
    {
        notification_distance = distance;
    }
    public int getNotification_time()
    {
        return notification_time;
    }
    public void setNotification_time(int time)
    {
        notification_time = time;
    }
    public double getNotification_region()
    {
        return notification_region;
    }
    public void setNotification_region(double region)
    {
        notification_region = region;
    }
    public boolean getIs_push()
    {
        return is_push;
    }
    public int getCategory() {
        return category;
    }
    public void setIs_push(boolean push)
    {
        is_push = push;
    }
    public void setCategory(int category_num)
    {
        category = category_num;
    }

}
