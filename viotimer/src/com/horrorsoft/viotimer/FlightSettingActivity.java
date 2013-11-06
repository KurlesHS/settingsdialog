package com.horrorsoft.viotimer;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.horrorsoft.viotimer.data.AlgorithmData;
import com.horrorsoft.viotimer.dialogs.EditAlgorithmDataDialog;
import com.horrorsoft.viotimer.dialogs.EditAlgorithmDataDialog_;
import com.horrorsoft.viotimer.dialogs.IDialogFragmentClickListener;
import com.horrorsoft.viotimer.dialogs.SelectItemPositionForAlgorithmTableDialog;
import com.horrorsoft.viotimer.json.JsonSetting;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 20:57
 */
public class FlightSettingActivity extends SherlockFragmentActivity implements View.OnClickListener, View.OnLongClickListener, IDialogFragmentClickListener {

    private static final int ALGORITHM_NUMBER_BUTTON_ID = 1;
    private static final int ALGORITHM_ROW_ID = 2;
    private static final int POSITION_OF_DATA_ID = 3;
    private static final int DELAY_OF_DATA_ID = 4;
    private static final int SERVO_POSITION_OF_DATA_ID = 5;
    private float dividerForAlgorithmDelay;

    private int currentAlgorithmNumber;
    private int currentServoNumber;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentAlgorithmNumber = 0;
        currentServoNumber = 0;

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dividerForAlgorithmDelay = (float) getResources().getInteger(R.integer.DividerForAlgorithmDelay);
        setContentView(R.layout.activity_fligth_setting);
        ScrollView scrollView = (ScrollView) (findViewById(R.id.scrollViewForAlgorithmTable));
        scrollView.setScrollbarFadingEnabled(false);
        scrollView = (ScrollView) findViewById(R.id.scrollViewForAlgorithmButtons);
        scrollView.setScrollbarFadingEnabled(false);
        AlgorithmData algorithmData = JsonSetting.createAlgorithmDataByJson(ApplicationData.getInstance().getJsonData());
        ApplicationData.getInstance().setAlgorithmData(algorithmData);
        FillAlgorithmButton(algorithmData);
        //FillTestAlgorithmData();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectItemPositionForAlgorithmTableDialog dlg = new SelectItemPositionForAlgorithmTableDialog();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dlg.show(ft, "selitemdlg");
            }
        };
        Button add = (Button) findViewById(R.id.buttonAdd);
        add.setOnClickListener(onClickListener);
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

    private void FillAlgorithmButton(AlgorithmData algorithmData) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutForChangeAlgorithmStep);
        for (int i = 0; i < algorithmData.getAlgorithmCount(); ++i) {
            String description = algorithmData.getAlgorithmDescription(i);
            Button button = new Button(this);
            button.setText(description);
            button.setTag(R.id.AlgorithmNumberButton, i);
            button.setId(ALGORITHM_NUMBER_BUTTON_ID);
            button.setOnClickListener(this);
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, getResources().getInteger(R.integer.HeightChangeAlgorithmButtonInMm),
                    getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) px);
            button.setLayoutParams(layoutParams);
            linearLayout.addView(button);
        }
    }

    //TODO: К удалению
    /*
    void addRow(int position, int delay, int value) {
        TableRow tableRow = createTableRow(position, delay, value);
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithmData);
        tableLayout.addView(tableRow);
        tableRow.setOnClickListener(this);
        tableRow.setOnLongClickListener(this);
    }
      */
    void insertRow(int insertAtPosition, int position, int delay, int servoPos, boolean makeItSelected) {
        TableRow tableRow = createTableRow(position, delay, servoPos);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        tableLayout.addView(tableRow, insertAtPosition);
        tableRow.setOnClickListener(this);
        tableRow.setOnLongClickListener(this);
        if (makeItSelected) {
            tableRow.setBackgroundColor(getResources().getColor(R.color.backgroundColorForSelectedRowInAlgorithmDataTable));
        }

    }

    private TableRow createTableRow(int position, int delay, int value) {
        TableRow tableRow = new TableRow(this);
        tableRow.setId(ALGORITHM_ROW_ID);
        TableRow.LayoutParams tableLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(tableLayoutParams);
        int wightInPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                getResources().getInteger(R.integer.HeightRowOfTableWithAlgorithmInMm),
                getResources().getDisplayMetrics());

        TextView textView = new TextView(this);
        textView.setId(POSITION_OF_DATA_ID);
        textView.setGravity(Gravity.CENTER);
        textView.setText(Integer.toString(position));
        TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams(0, wightInPixel, 2f);
        textLayoutParams.gravity = Gravity.CENTER;
        tableRow.addView(textView, textLayoutParams);

        textView = new TextView(this);
        textView.setId(DELAY_OF_DATA_ID);
        textView.setGravity(Gravity.CENTER);
        textView.setText(ApplicationData.doubleToString(delay / dividerForAlgorithmDelay, 2, 6));
        textLayoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 6f);

        tableRow.addView(textView, textLayoutParams);

        textView = new TextView(this);
        textView.setId(SERVO_POSITION_OF_DATA_ID);
        textView.setGravity(Gravity.CENTER);
        textView.setText(ApplicationData.addZeros(Integer.toString(value), 3));
        textLayoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 3f);
        textLayoutParams.gravity = Gravity.CENTER;
        tableRow.addView(textView, textLayoutParams);
        return tableRow;
    }

    @Override
    public boolean onLongClick(View v) {

        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        int position = tableLayout.indexOfChild(v);
        if (position >= 0) {
            AlgorithmData.InfoAboutRow infoAboutRow = ApplicationData.getInstance().getAlgorithmData().getInfoAboutAlgorithm(currentAlgorithmNumber, currentServoNumber, position);
            if (infoAboutRow != null) {
                EditAlgorithmDataDialog_ dlg = new EditAlgorithmDataDialog_();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                int maxDelay = infoAboutRow.maxDelay;
                int minDelay = infoAboutRow.minDelay;
                int delay = infoAboutRow.delay;
                int servoPos = infoAboutRow.servoPos;
                Bundle argBundle = new Bundle();
                argBundle.putInt(EditAlgorithmDataDialog.keyMaxDelay, maxDelay);
                argBundle.putInt(EditAlgorithmDataDialog.keyMinDelay, minDelay);
                argBundle.putInt(EditAlgorithmDataDialog.keyDelay, delay);
                argBundle.putInt(EditAlgorithmDataDialog.keyServoPos, servoPos);
                argBundle.putInt(EditAlgorithmDataDialog.keyPosition, position);
                dlg.setArguments(argBundle);
                dlg.show(ft, "editAlgDtaDlg");
            }
        }
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
                handleClickOnAlgorithmTableRow(v);
            }
            break;
            default:
                break;
        }
        long deltaT = System.currentTimeMillis() - startMs;
        Log.d("MyTag", "deltaT on click = " + deltaT);
    }

    private void updateAlgorithmPositions() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        for (int index = 0; index < tableLayout.getChildCount(); ++index) {
            TableRow currentRow = (TableRow) tableLayout.getChildAt(index);
            if (currentRow != null) {
                TextView textView = (TextView) currentRow.findViewById(POSITION_OF_DATA_ID);
                if (textView != null) {
                    textView.setText(ApplicationData.doubleToString((double) (index + 1), 0, 2));
                }
            }
        }
    }

    private void handleClickOnAlgorithmTableRow(View v) {
        TableRow tableRow = (TableRow) v;
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        if (tableLayout != null) {
            Object currentIndexObject = tableLayout.getTag(R.id.CurrentIndexForAlgorithmTable);
            if (currentIndexObject != null) {
                int currentIndex = (Integer) currentIndexObject;
                TableRow currentRow = (TableRow) tableLayout.getChildAt(currentIndex);
                if (currentRow != null) {
                    currentRow.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            int index = tableLayout.indexOfChild(tableRow);
            tableLayout.setTag(R.id.CurrentIndexForAlgorithmTable, index);
            tableRow.setBackgroundColor(getResources().getColor(R.color.backgroundColorForSelectedRowInAlgorithmDataTable));
        }
    }

    @Override
    public void onClick(int buttonId, Bundle bundle) {
        Log.d("MyTag", "button pressed: " + buttonId);
        switch (buttonId) {
            case R.id.InsertAlgorithmDataBelowCurrentItem: {
                // обработать вставку ряда таблицы ниже текущего элемента
                insertAlgorithmDataBellowCurrentItem();
            }
            break;
            case R.id.InsertAlgorithmDataUpperCurrentItem: {
                // обработать вставку ряда таблицы выше текущего элемента
                insertAlgorithmDataUpperCurrentItem();
            }
            case R.id.AlgorithmDataChanged: {
                if (bundle != null) {
                    int servoPos = bundle.getInt(EditAlgorithmDataDialog.keyServoPos);
                    int delay = bundle.getInt(EditAlgorithmDataDialog.keyDelay);
                    int position = bundle.getInt(EditAlgorithmDataDialog.keyPosition);
                    AlgorithmData algorithmData = ApplicationData.getInstance().getAlgorithmData();
                    if (algorithmData.updateAlgorithmData(currentAlgorithmNumber, currentServoNumber, position, delay, servoPos)) {
                        updateAlgorithmTableRow(position, delay, servoPos);
                    }
                }
            }
        }
    }

    private void updateAlgorithmTableRow(int position, int delay, int servoPos) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        TableRow tableRow = (TableRow) tableLayout.getChildAt(position);
        if (tableRow != null) {
            TextView delayTextView = (TextView) tableRow.findViewById(DELAY_OF_DATA_ID);
            TextView servoPosTextView = (TextView) tableRow.findViewById(SERVO_POSITION_OF_DATA_ID);
            if (delayTextView != null && servoPosTextView != null) {
                delayTextView.setText(ApplicationData.getDelayText(delay));
                servoPosTextView.setText(ApplicationData.getServoPosString(servoPos));
            }
        }
    }

    private void insertAlgorithmDataBellowCurrentItem() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        Object currentIndexObject = tableLayout.getTag(R.id.CurrentIndexForAlgorithmTable);
        int currentIndex = 0;
        if (currentIndexObject != null) {
            currentIndex = (Integer) currentIndexObject;
        }
        TableRow currentRow = (TableRow) tableLayout.getChildAt(currentIndex);
        if (currentIndex <= 0) {
            currentIndex = 1;
        } else {
            ++currentIndex;
        }

        int childCount = tableLayout.getChildCount();
        if (currentIndex > childCount) {
            currentIndex = childCount;
        }
        insertAlgorithmData(tableLayout, currentIndex, currentRow);
    }

    private void insertAlgorithmDataUpperCurrentItem() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.TableLayoutForAlgorithData);
        Object currentIndexObject = tableLayout.getTag(R.id.CurrentIndexForAlgorithmTable);
        int currentIndex = 0;
        if (currentIndexObject != null) {
            currentIndex = (Integer) currentIndexObject;
        }
        TableRow currentRow = (TableRow) tableLayout.getChildAt(currentIndex);
        if (currentIndex < 0) {
            currentIndex = 0;
        }

        insertAlgorithmData(tableLayout, currentIndex, currentRow);
    }

    private void insertAlgorithmData(TableLayout tableLayout, int currentIndex, TableRow currentRow) {
        AlgorithmData algorithmData = ApplicationData.getInstance().getAlgorithmData();
        AlgorithmData.InfoAboutRow infoAboutInsertedRow = algorithmData.prepareInfoAboutInsertingNewRowIntoAlgorithm(currentAlgorithmNumber, currentServoNumber, currentIndex);
        if (infoAboutInsertedRow != null) {
            if (currentRow != null) {
                currentRow.setBackgroundColor(Color.TRANSPARENT);
            }
            algorithmData.insertNewRowIntoAlgorithm(currentAlgorithmNumber, currentServoNumber, currentIndex, infoAboutInsertedRow.delay, infoAboutInsertedRow.servoPos);
            ensureAlgorithmTableRowVisible(currentIndex);
            insertRow(currentIndex, infoAboutInsertedRow.position, infoAboutInsertedRow.delay, infoAboutInsertedRow.servoPos, true);
            tableLayout.setTag(R.id.CurrentIndexForAlgorithmTable, currentIndex);
            updateAlgorithmPositions();
        }
    }
}