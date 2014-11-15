package com.horrorsoft.viotimer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.horrorsoft.viotimer.bluetooth.TimerProtocol;
import com.horrorsoft.viotimer.bluetooth.WriteSettingInTimerResultListener;
import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.*;

import java.util.UnknownFormatConversionException;

/**
 * Created by Admin on 04.04.2014.
 *
 */
@EFragment(R.layout.write_progress_dialog)
public class ProgressWriteSettingsDialog extends SherlockDialogFragment implements WriteSettingInTimerResultListener {

    @InstanceState
    protected String mTitle = "Title";

    @InstanceState
    protected String mDescription = "Description";

    @InstanceState
    protected boolean firstTime = true;

    @Bean
    protected ApplicationData commonData;

    @ViewById(R.id.textViewWriteProgressDialogDescription)
    protected TextView textViewDescription;

    @ViewById(R.id.progressBarWritingSettings)
    protected ProgressBar progressBar;

    @ViewById(R.id.textViewPercentForWritingResult)
    protected TextView textViewPerCent;

    public ProgressWriteSettingsDialog() {
        super();
        Log.d(ApplicationData.LOG_TAG, "ProgressWriteSettingsDialog.Constructor");
        setCancelable(true);
    }

    public void initTitle(String title) {
        mTitle = title;
    }

    public void initMessage(String description) {
        mDescription = description;
    }

    @AfterViews
    protected void init() {
        Log.d(ApplicationData.LOG_TAG, "ProgressWriteSettingsDialog.AfterViews" + (firstTime ? " firstTime" : " it isn't first time"));
        setTitle(mTitle);
        setMessage(mDescription);
        if (firstTime) {
            startFlashing();
            firstTime = false;
        } else {
            commonData.addWriteSettingResultListener(this);
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(ApplicationData.LOG_TAG, "ProgressWriteSettingsDialog.onCreate");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(ApplicationData.LOG_TAG, "ProgressWriteSettingsDialog.onSaveInstanceState");
        commonData.removeWriteSettingResultListener(this);
    }

    @Override
    public void onDestroy() {
        Log.d(ApplicationData.LOG_TAG, "ProgressWriteSettingsDialog.onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ApplicationData.LOG_TAG, "ProgressWriteSettingsDialog.onResume");
    }

    public void setTitle(String title) {
        mTitle = title;
        getDialog().setTitle(title);

    }

    public void setMessage(String description) {
        mDescription = description;
        textViewDescription.setText(description);
    }

    public void startFlashing() {
        commonData.addWriteSettingResultListener(this);
        commonData.writeSettingsIntoTimer();
    }

    @UiThread(delay = 5000)
    protected void closeProgressDialogAfterFiveSecond() {
        dismiss();
    }

    @Override
    public void writeResult(int status) {
        if (status == TimerProtocol.WRITE_RESULT_OK) {
            setMessage("Success");
            closeProgressDialogAfterFiveSecond();
        }  else if (status == TimerProtocol.WRITE_RESULT_FAIL) {
            setMessage("Failure");
            closeProgressDialogAfterFiveSecond();
        } else {
            dismiss();
        }
        commonData.removeWriteSettingResultListener(this);
    }

    @Override
    public void writeProcess(int currentPos, int maxPos) {
        Log.d(ApplicationData.LOG_TAG, String.valueOf(currentPos));
        Log.d(ApplicationData.LOG_TAG, String.valueOf(maxPos));

        if (progressBar == null)
            return;
        if (currentPos == 0) {
            progressBar.setMax(maxPos);
        }
        progressBar.setProgress(currentPos);
        int percent = progressBar.getProgress() * 100 / progressBar.getMax();
        try {
            textViewPerCent.setText(String.format("%d", percent) + " %");
        } catch (UnknownFormatConversionException r) {
            //
        }
    }

    @Override
    public String id() {
        return "50ccbd32-d006-48f2-8425-64037aa2195c";
    }
}
