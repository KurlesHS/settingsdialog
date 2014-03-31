package com.horrorsoft.viotimer;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.*;
import com.horrorsoft.viotimer.common.ApplicationData;


@Fullscreen
@EActivity(R.layout.activity_main)
public class StartActivity extends SherlockActivity {

    private static final int REQUEST_CONNECT_DEVICE = 1;

    /**
     * Called when the activity is first created.
     */
    @ViewById(R.id.imageViewBluetoothStatus)
    protected ImageView imageViewBluetoothStatus;

    @Bean
    protected ApplicationData commonData;

    @AfterViews
    public void init() {
        boolean btStatus = commonData.getBlueToothConnectionStatus();
        int resId = btStatus ? R.drawable.bt_con : R.drawable.bt_discon;
        imageViewBluetoothStatus.setImageResource(resId);
    }

    @Click(R.id.ProgrammButton)
    public void handleProgramButtonPushed() {
        Intent intent = new Intent(this, ProgramActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.ConnectButton)
    public void handleConnectButtonPushed() {
        if (!commonData.isBlueToothSupported()) {
            Toast.makeText(getApplicationContext(),
                    "Device doesn't support Bluetooth", Toast.LENGTH_LONG)
                    .show();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
