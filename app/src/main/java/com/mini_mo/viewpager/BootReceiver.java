package com.mini_mo.viewpager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    public static final String ACTION_RESTART_PERSISTENTSERVICE
            = "ACTION.Restart.PersistentService";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)) {
            Log.i("BootReceiver", "ACTION_RESTART_PERSISTENTSERVICE");

            Intent i = new Intent(context, Push.class);
            context.startService(i);
        }


        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.i("BootReceiver", "ACTION_BOOT_COMPLETED");
            Intent i = new Intent(context, Push.class);
            context.startService(i);
        }
    }
}
