package com.horrorsoft.viotimer;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.horrorsoft.viotimer.data.AlgorithmData;
import com.horrorsoft.viotimer.json.JsonSetting;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 20:57
 */
public class FlightSettingActivity extends Activity implements View.OnClickListener {

    private static final int ALGORITHM_NUMBER_BUTTON_ID = 1;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fligth_setting);
        AlgorithmData algorithmData = JsonSetting.createAlgorithmDataByJson(ApplicationData.getInstance().getJsonData());
        FillAlgorithmButton(algorithmData);
    }

    private void FillAlgorithmButton(AlgorithmData algorithmData) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutForChangeAlgorithmStep);
        for (int i = 0; i < algorithmData.getAlgorithmCount(); ++i) {
            String description = algorithmData.getAlgorithmDescription(i);
            Button button = new Button(this);
            button.setText(description);
            button.setTag(R.integer.AlgorithmNumberButton, i);
            button.setId(ALGORITHM_NUMBER_BUTTON_ID);
            button.setOnClickListener(this);
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, getResources().getInteger(R.integer.HeightChangeAlgorithmButtonInMm),
                    getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) px);
            button.setLayoutParams(layoutParams);
            linearLayout.addView(button);
        }
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
          case ALGORITHM_NUMBER_BUTTON_ID: {
               //TODO: хендлить переключение режимов алгоритма здесь
          }
          break;
          default:
              break;
      }
    }
}