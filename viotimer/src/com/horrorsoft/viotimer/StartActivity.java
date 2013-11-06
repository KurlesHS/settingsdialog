package com.horrorsoft.viotimer;

import android.content.Intent;
import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Fullscreen;

@Fullscreen
@EActivity(R.layout.activity_main)
public class StartActivity extends SherlockActivity {
    /**
     * Called when the activity is first created.
     */
    @AfterViews
    public void init() {

    }

    /*
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
    */

    @Click(R.id.GenSetButton)
    public void handleProgramButtonPushed() {
        Intent intent = new Intent(this, SelectSettingActivity.class);
        startActivity(intent);
    }
}
