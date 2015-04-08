package com.horrorsoft.abctimer;

import android.app.Application;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import com.horrorsoft.abctimer.common.ApplicationData;
import com.horrorsoft.abctimer.data.AlgorithmData;
import com.horrorsoft.abctimer.json.JsonSetting;

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
        // for testing purposes
        commonData.setJsonData(ApplicationData.readTextFileFromRawResource(R.raw.test_json, this));
        AlgorithmData algorithmData = JsonSetting.createAlgorithmDataByJson(commonData.getJsonData());
        commonData.setGlobalSettingData(JsonSetting.createListOfDataByJson(commonData.getJsonData()));
        commonData.setAlgorithmData(algorithmData);
    }
}
