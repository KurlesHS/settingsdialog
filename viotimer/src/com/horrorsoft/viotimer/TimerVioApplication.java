package com.horrorsoft.viotimer;

import android.app.Application;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EApplication;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.horrorsoft.viotimer.data.AlgorithmData;
import com.horrorsoft.viotimer.json.JsonSetting;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 22:01
 */
@EApplication
public class TimerVioApplication extends Application {

    @Bean
    protected ApplicationData commonData;

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationData.setGlobalMaxDelay(getResources().getInteger(R.integer.GlobalMaxDelay));
        ApplicationData.setDividerForAlgorithmDelay((float) getResources().getInteger(R.integer.DividerForAlgorithmDelay));
        commonData.setBinaryData(new byte[0x10000]);
        // for testing purposes
        commonData.setJsonData(ApplicationData.readTextFileFromRawResource(R.raw.test_json, this));
        AlgorithmData algorithmData = JsonSetting.createAlgorithmDataByJson(commonData.getJsonData());
        commonData.setAlgorithmData(algorithmData);
    }
}
