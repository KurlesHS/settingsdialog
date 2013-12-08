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

    @Click(R.id.GenSetButton)
    public void handleProgramButtonPushed() {
        Intent intent = new Intent(this, ProgramActivity_.class);
        startActivity(intent);
    }
}
