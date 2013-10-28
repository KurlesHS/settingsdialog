package com.horrorsoft.viotimer;

import android.app.Application;
import com.horrorsoft.viotimer.common.ApplicationData;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 22:01
 */
public class TimerVioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationData commonData = ApplicationData.getInstance();
        commonData.setBinaryData(new byte[0x10000]);
        // for testing purposes
        commonData.setJsonData(ApplicationData.readTextFileFromRawResource(R.raw.test_json, this));
    }
}
