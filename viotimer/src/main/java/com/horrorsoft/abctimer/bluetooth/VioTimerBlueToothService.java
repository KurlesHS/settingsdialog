package com.horrorsoft.abctimer.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.horrorsoft.abctimer.common.ApplicationData;

/**
 * Created by Admin on 05.04.2014.
 *
 */
public class VioTimerBlueToothService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Log.d(ApplicationData.LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ApplicationData.LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(ApplicationData.LOG_TAG, "onDestroy");
    }
}
