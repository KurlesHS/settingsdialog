package com.horrorsoft.viotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Fullscreen;

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
