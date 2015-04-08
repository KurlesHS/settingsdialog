package com.horrorsoft.abctimer;

import android.content.Context;
import android.os.PowerManager;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.horrorsoft.abctimer.common.ApplicationData;

/**
 *  Created by Alexey on 01.04.2015.
 *  Confirm
 */
public abstract class ActivityWithBluetoothStatuses extends SherlockActivity {

    protected abstract ImageView getImageViewBluetoothStatus();
    protected abstract ApplicationData getCommonData();
    protected ChangeBluetoothStatusHelper changeBluetoothStatusHelper = new ChangeBluetoothStatusHelper();

    PowerManager.WakeLock wakeLock;

    @Override
    protected void onPause() {
        super.onPause();
        changeBluetoothStatusHelper.setCommonData(getCommonData());
        changeBluetoothStatusHelper.setImageViewBluetoothStatus(getImageViewBluetoothStatus());
        changeBluetoothStatusHelper.removeBluetoothStatusListeners();
        if (wakeLock != null) {
            wakeLock.release();
        }
        wakeLock = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        }
        wakeLock.acquire();
        changeBluetoothStatusHelper.setCommonData(getCommonData());
        changeBluetoothStatusHelper.setImageViewBluetoothStatus(getImageViewBluetoothStatus());
        changeBluetoothStatusHelper.initBlueToothListeners();

    }
}
