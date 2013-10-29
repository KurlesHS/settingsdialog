package com.horrorsoft.viotimer;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
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
        addRow(2, 34, 23);
        addRow(3, 3454, 223);
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

    void addRow(int position, int delay, int value) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams tableLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(tableLayoutParams);
        int wightInPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                getResources().getInteger(R.integer.HeightRowOfTableWithAlgorithmInMm),
                getResources().getDisplayMetrics());

        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(Integer.toString(position));
        TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams(0, wightInPixel, 2f);
        textLayoutParams.gravity = Gravity.CENTER;
        tableRow.addView(textView, textLayoutParams);

        textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(ApplicationData.doubleToString(delay * 0.2f, 2, 6));
        textLayoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 6f);

        tableRow.addView(textView, textLayoutParams);

        textView = new TextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setText(ApplicationData.addZeros(Integer.toString(value), 3));
        textLayoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3f);
        textLayoutParams.gravity = Gravity.CENTER;
        tableRow.addView(textView, textLayoutParams);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        tableLayout.addView(tableRow);


    }
}