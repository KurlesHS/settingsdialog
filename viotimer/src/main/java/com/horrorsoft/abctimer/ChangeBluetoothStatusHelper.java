package com.horrorsoft.abctimer;

import android.util.Log;
import android.widget.ImageView;
import com.horrorsoft.abctimer.bluetooth.BlueToothStatusListener;
import com.horrorsoft.abctimer.common.ApplicationData;
import com.horrorsoft.abctimer.common.CommonBlueToothStatusListener;

/**
 *  Created by Alexey on 01.04.2015.
 */
public class ChangeBluetoothStatusHelper {
    protected BlueToothStatusListener blueToothStatusListener = null;

    public ImageView getImageViewBluetoothStatus() {
        return imageView;
    }

    public void setImageViewBluetoothStatus(ImageView imageView) {
        this.imageView = imageView;
    }

    protected ImageView imageView;

    public ApplicationData getCommonData() {
        return commonData;
    }

    public void setCommonData(ApplicationData commonData) {
        this.commonData = commonData;
    }

    protected ApplicationData commonData;

    public void initBlueToothListeners() {
        Log.d(ApplicationData.LOG_TAG, "initBlueToothListeners");
        if (blueToothStatusListener == null) {
            ImageView imageView = getImageViewBluetoothStatus();
            blueToothStatusListener = new CommonBlueToothStatusListener(imageView);
            getCommonData().addBlueToothStatusListener(blueToothStatusListener);
            boolean btStatus = getCommonData().getBlueToothConnectionStatus();
            if (imageView != null) {
                int resId = btStatus ? R.drawable.bt_con : R.drawable.bt_discon;
                imageView.setBackgroundResource(resId);
            }
        }
    }

    public void removeBluetoothStatusListeners() {
        Log.d(ApplicationData.LOG_TAG, "removeBluetoothStatusListeners");
        if (blueToothStatusListener != null) {
            getCommonData().removeBlueToothStatusListener(blueToothStatusListener);
            blueToothStatusListener = null;
        }
    }
}
