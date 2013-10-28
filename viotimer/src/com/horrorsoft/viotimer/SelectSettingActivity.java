package com.horrorsoft.viotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 20:39
 */
public class SelectSettingActivity extends SherlockActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        ImageButton settingButton = (ImageButton) findViewById(R.id.GenSetButton);
        settingButton.setOnClickListener(this);
        settingButton = (ImageButton) findViewById(R.id.FlightSetButton);
        settingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.GenSetButton:
                handleCommonSettings();
                break;
            case R.id.FlightSetButton:
                 handleFlightSettings();
                break;
            default:
                break;
        }
    }

    private void handleFlightSettings() {
        Intent intent = new Intent(this, FlightSettingActivity.class);
        startActivity(intent);
    }

    private void handleCommonSettings() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
}
