package com.horrorsoft.viotimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import com.actionbarsherlock.app.SherlockActivity;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

public class StartActivity extends SherlockActivity implements OnClickListener {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ImageButton settingButton = (ImageButton) findViewById(R.id.GenSetButton);
        settingButton.setOnClickListener(this);
        // how to open file dialog
        //openFileDialog();
    }

    private void openFileDialog() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });
        startActivityForResult(intent, 123);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.GenSetButton: {
                // program button
                handleProgramButtonPushed();
            }
            break;
            default:
                break;
        }
    }

    private void handleProgramButtonPushed() {
        Intent intent = new Intent(this, SettingActivity.class);
        //TODO: заполнить правильными значемниями перед передачаей данных настроечной активити
        byte[] array = new byte[0x10000];
        String jsonData = "";
        intent.putExtra("array", array);
        intent.putExtra("jsonData", jsonData);
        startActivity(intent);
    }
}
