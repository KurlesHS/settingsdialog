package com.horrorsoft.viotimer;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import com.horrorsoft.viotimer.bluetooth.BlueToothStatusListener;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.horrorsoft.viotimer.common.CommonBlueToothStatusListener;

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
