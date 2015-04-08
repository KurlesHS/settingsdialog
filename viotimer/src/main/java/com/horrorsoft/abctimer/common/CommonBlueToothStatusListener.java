package com.horrorsoft.abctimer.common;

import android.widget.ImageView;
import com.horrorsoft.abctimer.R;
import com.horrorsoft.abctimer.bluetooth.BlueToothStatusListener;

/**
 *  Created by Alexey on 01.04.2015.
 */
public class CommonBlueToothStatusListener implements BlueToothStatusListener {
    private static final String BLUETOOTH_LISTENER_UUID = "0d534e8c-c092-4eea-8425-da9a344d48de";
    private ImageView imageView;

    public CommonBlueToothStatusListener(ImageView imageView) {
        this.imageView = imageView;
    }
    @Override
    public void bluetoothStatusChanged(boolean status) {
        if (imageView == null) {
            return;
        }
        int resId = status ? R.drawable.bt_con : R.drawable.bt_discon;
        imageView.setImageResource(resId);
    }

    @Override
    public String id() {
        return BLUETOOTH_LISTENER_UUID;
    }
}
