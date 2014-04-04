package com.horrorsoft.viotimer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.*;
import com.horrorsoft.viotimer.bluetooth.BlueToothDataListener;
import com.horrorsoft.viotimer.bluetooth.BlueToothStatusListener;
import com.horrorsoft.viotimer.bluetooth.DeviceListActivity;
import com.horrorsoft.viotimer.common.ApplicationData;


@Fullscreen
@EActivity(R.layout.activity_main)
public class StartActivity extends SherlockActivity {

    private static final int REQUEST_CONNECT_DEVICE = 0x01;
    private static final int REQUEST_ENABLE_BLUETOOTH = 0x02;
    private static final String BLUETOOTH_LISTENER_UUID = "0d534e8c-c092-4eea-8425-da9a344d48de";
    private Context applicationContext = null;

    private BlueToothDataListener blueToothDataListener = null;
    private BlueToothStatusListener blueToothStatusListener = null;

    /**
     * Called when the activity is first created.
     */
    @ViewById(R.id.imageViewBluetoothStatus)
    protected ImageView imageViewBluetoothStatus;

    @Bean
    protected ApplicationData commonData;

    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (blueToothDataListener != null) {
            commonData.removeBlueToothDataListener(blueToothDataListener);
            blueToothDataListener = null;
        }
    }

    @AfterViews
    public void init() {
        applicationContext = getApplicationContext();
        boolean btStatus = commonData.getBlueToothConnectionStatus();
        int resId = btStatus ? R.drawable.bt_con : R.drawable.bt_discon;
        imageViewBluetoothStatus.setImageResource(resId);
        initBlueToothListeners();
    }

    private void initBlueToothListeners() {
        if (blueToothDataListener == null) {
            blueToothDataListener = new BlueToothDataListener() {

                @Override
                public void dataFromBluetooth(byte[] buffer) {
                    commonData.writeDataIntoBlueTooth(buffer);
                }

                @Override
                public String id() {
                    return BLUETOOTH_LISTENER_UUID;
                }
            };
            commonData.addBlueToothDataListener(blueToothDataListener);
        }

        if (blueToothStatusListener == null) {
            blueToothStatusListener = new BlueToothStatusListener() {
                @Override
                public void bluetoothStatusChanged(boolean status) {
                    int resId = status ? R.drawable.bt_con : R.drawable.bt_discon;
                    imageViewBluetoothStatus.setImageResource(resId);
                }

                @Override
                public String id() {
                    return  BLUETOOTH_LISTENER_UUID;
                }
            };
            commonData.addBlueToothStatusListener(blueToothStatusListener);
        }
    }

    @Click(R.id.ProgrammButton)
    public void handleProgramButtonPushed() {
        Intent intent = new Intent(this, ProgramActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.ConnectButton)
    public void handleConnectButtonPushed() {
        if (!commonData.isBlueToothSupported()) {
            if (applicationContext != null) {
                Toast.makeText(applicationContext,
                        "Device doesn't support Bluetooth", Toast.LENGTH_LONG)
                        .show();
            }
            return;
        }

        if (!commonData.isBlueToothEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            if (applicationContext != null) {
                Toast.makeText(applicationContext, "Enabling Bluetooth!!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            if (!commonData.getBlueToothConnectionStatus()) {
                connect();
            } else {
                commonData.disconnect();
            }

        }
    }

    private void connect() {
        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                if (resultCode == Activity.RESULT_OK) {
                    //
                    // Get the device MAC address
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        String deviceAddress = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        Toast.makeText(this, deviceAddress, Toast.LENGTH_SHORT).show();
                        commonData.connect(deviceAddress, this);

                    } else {
                        // Failure retrieving MAC address
                        Toast.makeText(this, R.string.macFailed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Failure retrieving MAC address
                    Toast.makeText(this, R.string.macFailed, Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case REQUEST_ENABLE_BLUETOOTH: {
                if (resultCode == Activity.RESULT_OK) {
                    connect();
                } else {
                    Toast.makeText(this, R.string.cancelButtonPressed, Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                break;
        }
    }
}
