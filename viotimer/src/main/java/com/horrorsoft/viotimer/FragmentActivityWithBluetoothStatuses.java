package com.horrorsoft.viotimer;

import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.horrorsoft.viotimer.common.ApplicationData;

/**
 *  Created by Alexey on 01.04.2015.
 */
public abstract class FragmentActivityWithBluetoothStatuses extends SherlockFragmentActivity{

    protected abstract ImageView getImageViewBluetoothStatus();
    protected abstract ApplicationData getCommonData();
    protected ChangeBluetoothStatusHelper changeBluetoothStatusHelper = new ChangeBluetoothStatusHelper();

    @Override
    protected void onPause() {
        changeBluetoothStatusHelper.setCommonData(getCommonData());
        changeBluetoothStatusHelper.setImageViewBluetoothStatus(getImageViewBluetoothStatus());
        changeBluetoothStatusHelper.removeBluetoothStatusListeners();
        super.onPause();
    }

    @Override
    protected void onResume() {
        changeBluetoothStatusHelper.setCommonData(getCommonData());
        changeBluetoothStatusHelper.setImageViewBluetoothStatus(getImageViewBluetoothStatus());
        changeBluetoothStatusHelper.initBlueToothListeners();
        super.onResume();
    }
}
