package com.horrorsoft.viotimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockActivity;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import java.io.File;

public class MyActivity extends SherlockActivity implements View.OnClickListener {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        FileDialog fileDialog = new FileDialog();
        fileDialog.finish();
        Button btn = (Button) findViewById(R.id.button);
        if (btn != null) {
            btn.setOnClickListener(this);
        }
        btn = (Button) findViewById(R.id.buttonFileDialog);
        if (btn != null) {
            btn.setOnClickListener(this);
        }
        Log.d("MyTag", getApplicationContext().getFilesDir() + "/" + "hello.txt");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.button): {
                Intent intent = new Intent(this, SettingActivity.class);
                String xmlData = "";
                byte[] array = new byte[0x10000];
                intent.putExtra("xmlData", xmlData);
                intent.putExtra("array", array);
                startActivity(intent);
            }
            break;
            case (R.id.buttonFileDialog) : {
                Log.d("MyTag", "file dialog pushed");
                Intent intent = new Intent(getBaseContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, "/sdcard");
                intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

                //can user select directories or not
                intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

                //alternatively you can set file filter
                //intent.putExtra(FileDialog.FORMAT_FILTER, new String[] { "png" });

                startActivityForResult(intent, 123);
            }
            break;
        }
    }
}
