package com.horrorsoft.viotimer;

import android.content.Intent;
import com.actionbarsherlock.app.SherlockActivity;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 20:39
 */
@Fullscreen
@EActivity(R.layout.activity_setting)
public class SelectSettingActivity extends SherlockActivity {

    @Click(R.id.FlightSetButton)
    public void handleFlightSettings() {
        Intent intent = new Intent(this, FlightSettingActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.GenSetButton)
    public void handleCommonSettings() {
        Intent intent = new Intent(this, SettingActivity_.class);
        startActivity(intent);
    }
}
