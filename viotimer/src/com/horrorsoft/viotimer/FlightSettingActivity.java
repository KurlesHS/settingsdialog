package com.horrorsoft.viotimer;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.*;
import com.horrorsoft.viotimer.adapters.AlgorithmAdapter;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.horrorsoft.viotimer.data.AlgorithmData;
import com.horrorsoft.viotimer.data.AlgorithmHandler;
import com.horrorsoft.viotimer.data.AlgorithmRowData;
import com.horrorsoft.viotimer.dialogs.EditAlgorithmDataDialog;
import com.horrorsoft.viotimer.dialogs.EditAlgorithmDataDialog_;
import com.horrorsoft.viotimer.dialogs.IDialogFragmentClickListener;
import com.horrorsoft.viotimer.dialogs.SelectItemPositionForAlgorithmTableDialog;
import com.horrorsoft.viotimer.json.JsonSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 20:57
 */
@Fullscreen
@EActivity(R.layout.activity_fligth_setting)
public class FlightSettingActivity extends SherlockFragmentActivity implements View.OnClickListener, IDialogFragmentClickListener {

    private static final int ALGORITHM_NUMBER_BUTTON_ID = 1;
    private static final int SERVO_NUMBER_BUTTON_ID = 2;

    private static final String CURRENT_ALGORITHM_NUMBER = "currentAlgorithmNumber";
    private static final String CURRENT_SERVO_NUMBER = "currentServoNumber";

    //private static final int POSITION_OF_DATA_ID = 2;
    //private static final int DELAY_OF_DATA_ID = 3;
    //private static final int SERVO_POSITION_OF_DATA_ID = 4;

    private float dividerForAlgorithmDelay;
    private int currentAlgorithmNumber;
    private int currentServoNumber;

    @Bean
    AlgorithmHandler algorithmHandler;

    @Bean
    protected ApplicationData applicationData;
    @Bean
    protected AlgorithmAdapter algorithmAdapter;

    @ViewById(R.id.listViewForAlgorithm)
    ListView algorithmListView;

    @ViewById(R.id.scrollViewForAlgorithmButtons)
    ScrollView scrollViewForAlgorithmButtons;

    @ViewById(R.id.linearLayoutForServoButtons)
    LinearLayout linearLayoutForServoButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentServoNumber = savedInstanceState.getInt(CURRENT_SERVO_NUMBER, 0);
            currentAlgorithmNumber = savedInstanceState.getInt(CURRENT_ALGORITHM_NUMBER, 0);
        } else {
            currentAlgorithmNumber = currentServoNumber = 0;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_ALGORITHM_NUMBER, currentAlgorithmNumber);
        outState.putInt(CURRENT_SERVO_NUMBER, currentServoNumber);
    }

    @AfterViews
    void init() {
        dividerForAlgorithmDelay = (float) getResources().getInteger(R.integer.DividerForAlgorithmDelay);
        algorithmListView.setScrollbarFadingEnabled(false);
        scrollViewForAlgorithmButtons.setScrollbarFadingEnabled(false);
        AlgorithmData algorithmData = JsonSetting.createAlgorithmDataByJson(applicationData.getJsonData());
        applicationData.setAlgorithmData(algorithmData);
        FillAlgorithmButton(algorithmData);
        algorithmAdapter.setAlgorithmHandler(algorithmHandler);
        algorithmListView.setAdapter(algorithmAdapter);
        int servoNum = 0;
        for (int r = 0; r < linearLayoutForServoButtons.getChildCount(); ++r) {
            View v = linearLayoutForServoButtons.getChildAt(r);
            if (v instanceof Button) {
                Button button = (Button) linearLayoutForServoButtons.getChildAt(r);
                if (button != null) {
                    if (servoNum == currentServoNumber) {
                        button.setSelected(true);
                    }
                    button.setTag(R.id.ServoNumberButton, servoNum++);
                    button.setId(SERVO_NUMBER_BUTTON_ID);
                    button.setOnClickListener(this);
                }
            }
        }
        //FillTestAlgorithmData();
    }

    @Click(R.id.buttonAdd)
    protected void onAddButtonClicked() {
        SelectItemPositionForAlgorithmTableDialog dlg = new SelectItemPositionForAlgorithmTableDialog();
        dlg.show(getSupportFragmentManager(), "selitemdlg");
    }

    private void FillAlgorithmButton(AlgorithmData algorithmData) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutForChangeAlgorithmStep);
        for (int i = 0; i < algorithmData.getAlgorithmCount(); ++i) {
            String description = algorithmData.getAlgorithmDescription(i);
            Button button = new Button(this);
            button.setBackgroundResource(R.drawable.selected_button);
            button.setText(description);
            button.setTag(R.id.AlgorithmNumberButton, i);
            button.setId(ALGORITHM_NUMBER_BUTTON_ID);
            button.setOnClickListener(this);
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, getResources().getInteger(R.integer.HeightChangeAlgorithmButtonInMm),
                    getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) px);
            button.setLayoutParams(layoutParams);
            if (i == currentAlgorithmNumber) {
                button.setSelected(true);
            }
            linearLayout.addView(button);
        }
    }

    private void unCheckAlgorithmButton(LinearLayout linearLayout) {
        for (int r = 0; r < linearLayout.getChildCount(); ++r) {
            View v = linearLayout.getChildAt(r);
            if (v != null && v.isSelected()) {
                v.setSelected(false);
            }
        }

    }

    @ItemClick(R.id.listViewForAlgorithm)
    void personListItemClicked(AlgorithmRowData data) {
        //makeText(this, data.getPosition() + " " + data.getPosition() + " " + data.getServoPos(), LENGTH_SHORT).show();
        algorithmAdapter.setSelectedRow(data.getPosition() - 1);
    }

    @ItemLongClick(R.id.listViewForAlgorithm)
    public boolean onItemLongClick1(AlgorithmRowData data) {
        int position = data.getPosition() - 1;
        algorithmAdapter.setSelectedRow(position);
        AlgorithmHandler.InfoAboutRow infoAboutRow = algorithmHandler.getInfoAboutRow(position);
        if (infoAboutRow != null) {
            EditAlgorithmDataDialog dlg = new EditAlgorithmDataDialog_();
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
            dlg.show(getSupportFragmentManager(), "editAlgDtaDlg");
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        long startMs = System.currentTimeMillis();
        boolean changeAlgorithm = false;
        switch (v.getId()) {
            case ALGORITHM_NUMBER_BUTTON_ID: {
                //TODO: хендлить переключение режимов алгоритма здесь
                Button button = (Button) v;
                if (button != null) {
                    int algorithmNumber = (Integer) v.getTag(R.id.AlgorithmNumberButton);
                    if (algorithmNumber != currentAlgorithmNumber) {
                        unCheckAlgorithmButton((LinearLayout) findViewById(R.id.layoutForChangeAlgorithmStep));
                        button.setSelected(true);
                        changeAlgorithm = true;
                        Log.d("MyTag", "ALGORITHM_NUMBER_BUTTON_ID: " + algorithmNumber);
                    }
                }
            }
            break;
            case SERVO_NUMBER_BUTTON_ID: {
                Button button = (Button) v;
                if (button != null) {
                    int servoNumber = (Integer) v.getTag(R.id.ServoNumberButton);
                    if (servoNumber != currentServoNumber) {
                        unCheckAlgorithmButton(linearLayoutForServoButtons);
                        button.setSelected(true);
                        Log.d("MyTag", "SERVO_NUMBER_BUTTON_ID: " + servoNumber);
                    }
                }
            }
            break;

            default:
                break;
        }
        if (changeAlgorithm) {

        }
    }

    @Override
    protected void onDestroy() {
        algorithmAdapter.setAlgorithmHandler(null);
        super.onDestroy();
    }

    @Override
    public void onClick(int buttonId, Bundle bundle) {
        Log.d("MyTag", "button pressed: " + buttonId);
        switch (buttonId) {
            case R.id.InsertAlgorithmDataBottomCurrentItem: {
                // обработать вставку ряда таблицы ниже текущего элемента
                insertAlgorithmDataBottomCurrentItem();
            }
            break;
            case R.id.InsertAlgorithmDataUpperCurrentItem: {
                // обработать вставку ряда таблицы выше текущего элемента
                insertAlgorithmDataUpperCurrentItem();
            }
            break;
            case R.id.AlgorithmDataChanged: {
                if (bundle != null) {
                    int servoPos = bundle.getInt(EditAlgorithmDataDialog.keyServoPos);
                    int delay = bundle.getInt(EditAlgorithmDataDialog.keyDelay);
                    int position = bundle.getInt(EditAlgorithmDataDialog.keyPosition);
                    algorithmHandler.updateAlgorithmData(algorithmAdapter.getSelectedRow(), position + 1, delay, servoPos);
                }
            }
            break;
            default:
                break;
        }
    }

    private void insertAlgorithmDataBottomCurrentItem() {
        int currentPos = algorithmAdapter.getSelectedRow();
        if (currentPos < 0)
            currentPos = 0;
        ++currentPos;
        if (currentPos > algorithmAdapter.getCount()) {
            currentPos = algorithmAdapter.getCount();
        }
        AlgorithmHandler.InfoAboutRow infoAboutRow = algorithmHandler.getInfoInsertingAboutRow(currentPos);
        if (infoAboutRow != null) {
            algorithmHandler.insertRow(currentPos, infoAboutRow.delay, infoAboutRow.servoPos, false);
            algorithmAdapter.setSelectedRow(currentPos);
        }
    }

    private void insertAlgorithmDataUpperCurrentItem() {
        int currentPos = algorithmAdapter.getSelectedRow();
        if (currentPos < 0)
            currentPos = 0;
        AlgorithmHandler.InfoAboutRow infoAboutRow = algorithmHandler.getInfoInsertingAboutRow(currentPos);
        if (infoAboutRow != null) {
            algorithmHandler.insertRow(currentPos, infoAboutRow.delay, infoAboutRow.servoPos);
        }
    }
}