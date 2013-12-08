package com.horrorsoft.viotimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.*;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

/**
 * Created by Alexey on 07.12.13.
 */
@Fullscreen
@EActivity(R.layout.activity_program)
public class ProgramActivity extends SherlockActivity {

    @Bean
    protected ApplicationData commonData;

    private static final int SAVE_FILE_ID = 0x01;
    private static final int LOAD_FILE_ID = 0x02;

    @Click(R.id.settingButtonId)
    protected void handleSetting() {
        Intent intent = new Intent(this, SelectSettingActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.saveFileButtonId)
    protected void handleSaveFile() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "vts" });
        startActivityForResult(intent, SAVE_FILE_ID);
    }

    @Click(R.id.loadFileButtonId)
    protected void  handleLoadFile() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, "/sdcard");
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "vts" });
        startActivityForResult(intent, LOAD_FILE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean needExecBaseMethod = false;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SAVE_FILE_ID: {
                    String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
                    if (!filePath.endsWith(".vts")) {
                        filePath += ".vts";
                    }
                    if (commonData.saveConfigToFile(filePath)) {
                        Toast.makeText(getApplicationContext(), "save data successfull", Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "save data unsuccessfull", Toast.LENGTH_LONG);
                    }
                }
                break;
                case LOAD_FILE_ID: {
                    String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
                    if (commonData.loadConfigFromFile(filePath)) {
                        Toast.makeText(getApplicationContext(), "load data successfull", Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "load data unsuccessfull", Toast.LENGTH_LONG);
                    }

                }
                break;
                default:
                    needExecBaseMethod = true;
                    break;
            }
        } else {
            needExecBaseMethod = true;
        }
        if (needExecBaseMethod) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}