package com.horrorsoft.viotimer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
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
public class FlightSettingActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private static final int ALGORITHM_NUMBER_BUTTON_ID = 1;
    private static final int ALGORITHM_ROW_ID = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fligth_setting);
        AlgorithmData algorithmData = JsonSetting.createAlgorithmDataByJson(ApplicationData.getInstance().getJsonData());
        FillAlgorithmButton(algorithmData);
        FillTestAlgorithmData();
        Button addTop = (Button) findViewById(R.id.buttonAddOnTop);
        addTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureAlgorithmTableRowVisible(33);
            }
        });
    }

    private void ensureAlgorithmTableRowVisible(int row) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        int rowCount = tableLayout.getChildCount();
        if (rowCount > 0) {
            if (row >= rowCount)
                row = rowCount - 1;
            View v = tableLayout.getChildAt(row);
            Rect rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            tableLayout.requestRectangleOnScreen(rect);
        }
    }

    private void FillTestAlgorithmData() {
        for (int i = 1; i < 40; ++i) {
            addRow(i, i * 1000, i * 3);
        }

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

    void addRow(int position, int delay, int value) {
        TableRow tableRow = new TableRow(this);
        tableRow.setId(ALGORITHM_ROW_ID);
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
        tableRow.setOnClickListener(this);
        tableRow.setOnLongClickListener(this);


    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {
        long startMs = System.currentTimeMillis();
        switch (v.getId()) {
            case ALGORITHM_NUMBER_BUTTON_ID: {
                //TODO: хендлить переключение режимов алгоритма здесь

            }
            break;
            case ALGORITHM_ROW_ID: {
                TableRow tableRow = (TableRow) v;
                TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
                if (tableLayout != null) {
                    Object currentIndexObject = tableLayout.getTag(R.integer.CurrentIndexForAlgorithmTable);
                    if (currentIndexObject != null) {
                        int currentIndex = (Integer) currentIndexObject;
                        TableRow currentRow = (TableRow) tableLayout.getChildAt(currentIndex);
                        if (currentRow != null) {
                            currentRow.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                    int index = tableLayout.indexOfChild(tableRow);
                    tableLayout.setTag(R.integer.CurrentIndexForAlgorithmTable, index);
                    tableRow.setBackgroundColor(getResources().getColor(R.color.backgroundColorForSelectedRowInAlgorithmDataTable));
                }
            }
            break;
            default:
                break;
        }
        long deltaT = System.currentTimeMillis() - startMs;
        Log.d("MyTag", "deltaT on click = " + deltaT);
    }
}