package com.horrorsoft.viotimer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockActivity;

public class MyActivity extends SherlockActivity implements View.OnClickListener{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btn = (Button)findViewById(R.id.button);
        if (btn != null) {
            btn.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.button): {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
            }
            break;
        }


    }
}
