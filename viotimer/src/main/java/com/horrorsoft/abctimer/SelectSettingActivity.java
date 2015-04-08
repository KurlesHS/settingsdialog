package com.horrorsoft.abctimer;

import android.content.Intent;
import android.widget.ImageView;
import com.horrorsoft.abctimer.common.ApplicationData;
import org.androidannotations.annotations.*;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 20:39
 */
@Fullscreen
@EActivity(R.layout.activity_setting)
public class SelectSettingActivity extends ActivityWithBluetoothStatuses {

    @Bean
    protected ApplicationData commonData;

    @ViewById(R.id.imageViewBluetoothStatus)
    protected ImageView imageViewBluetoothStatus;

    @Click(R.id.FlightSetButton)
    public void handleFlightSettings() {
        if (commonData.getAlgorithmData().getAlgorithmCount() == 0) {
            return;
        }
        Intent intent = new Intent(this, FlightSettingActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.GenSetButton)
    public void handleCommonSettings() {
        Intent intent = new Intent(this, SettingActivity_.class);
        startActivity(intent);
    }

    @Override
    protected ImageView getImageViewBluetoothStatus() {
        return imageViewBluetoothStatus;
    }

    @Override
    protected ApplicationData getCommonData() {
        return commonData;
    }
}
