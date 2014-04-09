package com.horrorsoft.viotimer.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.horrorsoft.viotimer.common.ApplicationData;

/**
 * Created by Admin on 05.04.2014.
 *
 */
public class VioTimerBlueToothService extends Service {

    private BlueToothConnectionThread mBlueToothConnectionThread;
    private Handler mHandler;
    private boolean mFirstTime;
    private int mFirstStartId;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        mFirstTime = true;
        Log.d(ApplicationData.LOG_TAG, "onCreate service");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ApplicationData.LOG_TAG, "onStartCommand service");
        if (mFirstTime) {
            mFirstTime = false;
            mFirstStartId = startId;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(ApplicationData.LOG_TAG, "onDestroy onDestroy");
    }
}
