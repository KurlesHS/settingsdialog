package com.horrorsoft.abctimer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.horrorsoft.abctimer.bluetooth.BlueToothDataListener;
import com.horrorsoft.abctimer.bluetooth.DeviceListActivity;
import com.horrorsoft.abctimer.bluetooth.IFlashBluetoothSettingsListener;
import com.horrorsoft.abctimer.common.ApplicationData;
import org.androidannotations.annotations.*;


@Fullscreen
@EActivity(R.layout.activity_main)
public class StartActivity extends FragmentActivityWithBluetoothStatuses implements INewBluetoothSettingListener, IFlashBluetoothSettingsListener {

    private static final int REQUEST_CONNECT_DEVICE = 0x01;
    private static final int REQUEST_ENABLE_BLUETOOTH = 0x02;
    private Context applicationContext = null;

    private BlueToothDataListener blueToothDataListener = null;

    /**
     * Called when the activity is first created. vio was here
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
        Log.d(ApplicationData.LOG_TAG, "init-start-activity");
        applicationContext = getApplicationContext();

    }

    @Click(R.id.SaveButton)
    public void handleTelemetryButtonPushed() {
        Intent intent = new Intent(this, TelemetryActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.ProgrammButton)
    public void handleProgramButtonPushed() {
        Intent intent = new Intent(this, ProgramActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.GraphButton)
    public void handleGraphButtonPushed() {
        Intent intent = new Intent(this, GraphSettingActivity_.class);
        startActivity(intent);
    }

    @LongClick(R.id.ConnectButton)
    void handleConnectButtonLongClick() {
        if (commonData.getBlueToothConnectionStatus()) {
            BlueToothSettingsDialog dlg = new BlueToothSettingsDialog();
            dlg.setSettingListener(this);
            dlg.show(getSupportFragmentManager(), "bluetooth_setting_dlg");
        }
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

    @Override
    protected ImageView getImageViewBluetoothStatus() {
        return imageViewBluetoothStatus;
    }

    @Override
    protected ApplicationData getCommonData() {
        return commonData;
    }

    @Override
    public void newSettings(String pinCond, String bluetoothName) {
        commonData.setFlashBluetoothSettingsListener(this);
        commonData.flashNewBluetoothSettings(pinCond, bluetoothName);
    }

    @Override
    public void flashBluetoothSettingResult(boolean result) {
        Toast.makeText(this,
                result ? R.string.okFlashBluetoothSetting : R.string.errorFlashBluetoothSetting,
                Toast.LENGTH_SHORT).show();
        commonData.setFlashBluetoothSettingsListener(null);
    }
}
