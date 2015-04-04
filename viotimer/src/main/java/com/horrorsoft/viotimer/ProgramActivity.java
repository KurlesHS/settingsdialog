package com.horrorsoft.viotimer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.horrorsoft.viotimer.bluetooth.ReadSettingFromTimerResultListener;
import com.horrorsoft.viotimer.bluetooth.TimerProtocol;
import com.horrorsoft.viotimer.bluetooth.WriteSettingInTimerResultListener;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;
import org.androidannotations.annotations.*;

/**
 *
 * Created by Alexey on 07.12.13.
 */
@Fullscreen
@EActivity(R.layout.activity_program)
public class ProgramActivity extends ActivityWithBluetoothStatuses {

    @Bean
    protected ApplicationData commonData;

    @InstanceState
    protected WriteSettingInTimerResultListener mWriteSettingInTimerResultListener = null;

    @InstanceState
    protected ReadSettingFromTimerResultListener mReadSettingFromTimerResultListener = null;

    @ViewById(R.id.imageViewBluetoothStatus)
    protected ImageView imageViewBluetoothStatus;

    private static final int SAVE_FILE_ID = 0x01;
    private static final int LOAD_FILE_ID = 0x02;

    protected ProgressDialog mProgressDialog = null;

    @Click(R.id.settingButtonId)
    protected void handleSetting() {
        Intent intent = new Intent(this, SelectSettingActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.saveFileButtonId)
    protected void handleSaveFile() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[]{"vts"});
        startActivityForResult(intent, SAVE_FILE_ID);
    }

    @AfterViews
    protected void init() {

    }

    @Click(R.id.loadFileButtonId)
    protected void handleLoadFile() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[]{"vts", "vts_json"});
        startActivityForResult(intent, LOAD_FILE_ID);
    }

    @Click(R.id.ReadFromTimerButton)
    protected void  handleReadFromTimer() {
        /*
        ProgressWriteSettingsDialog_ dlg = new ProgressWriteSettingsDialog_();
        dlg.initMessage("Write setting in progress ...");
        dlg.initTitle("Write setting ...");
        dlg.show(getSupportFragmentManager(), "writeProgress");
        */
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Read setting ...");
        mProgressDialog.setMessage("Read setting in progress ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(0);
        mReadSettingFromTimerResultListener = new ReadSettingFromTimerResultListener() {
            @Override
            public void readResult(int status) {
                if (status == TimerProtocol.RESULT_OK) {
                    mProgressDialog.setMessage("Success");
                    closeProgressDialogAfterFiveSecond();
                }  else if (status == TimerProtocol.RESULT_FAIL) {
                    mProgressDialog.setMessage("Failure");
                    closeProgressDialogAfterFiveSecond();
                } else {
                    closeProgressDialog();
                }
                commonData.removeReadSettingResultListener(this);
                mReadSettingFromTimerResultListener = null;
            }

            @Override
            public void readProcess(int currentPos, int maxPos) {
                Log.d(ApplicationData.LOG_TAG, String.valueOf(currentPos));
                Log.d(ApplicationData.LOG_TAG, String.valueOf(maxPos));

                if (mProgressDialog == null)
                    return;
                mProgressDialog.setMax(maxPos);
                mProgressDialog.setProgress(currentPos);
            }

            @Override
            public String id() {
                return "1f48bf5c-1ae5-43c2-b96f-bd4d349b2dbf";
            }
        };
        commonData.addReadSettingResultListener(mReadSettingFromTimerResultListener);
        mProgressDialog.show();
        commonData.readSettingsFromTimer();


    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @UiThread(delay = 5000)
    protected void closeProgressDialogAfterFiveSecond() {
        closeProgressDialog();
    }

    @Click(R.id.WriteInTimerButton)
    protected void  handleWriteInTimer() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Write setting ...");
        mProgressDialog.setMessage("Write setting in progress ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(0);
        mWriteSettingInTimerResultListener = new WriteSettingInTimerResultListener() {
            @Override
            public void writeResult(int status) {
                if (status == TimerProtocol.RESULT_OK) {
                    mProgressDialog.setMessage("Success");
                    closeProgressDialogAfterFiveSecond();
                }  else if (status == TimerProtocol.RESULT_FAIL) {
                    mProgressDialog.setMessage("Failure");
                    closeProgressDialogAfterFiveSecond();
                } else {
                    closeProgressDialog();
                }
                commonData.removeWriteSettingResultListener(this);
                mWriteSettingInTimerResultListener = null;
            }

            @Override
            public void writeProcess(int currentPos, int maxPos) {
                Log.d(ApplicationData.LOG_TAG, String.valueOf(currentPos));
                Log.d(ApplicationData.LOG_TAG, String.valueOf(maxPos));

                if (mProgressDialog == null)
                    return;
                mProgressDialog.setMax(maxPos);
                mProgressDialog.setProgress(currentPos);
            }

            @Override
            public String id() {
                return "1f48bf5c-1ae5-43c2-b96f-bd4d349b2dbf";
            }
        };
        commonData.addWriteSettingResultListener(mWriteSettingInTimerResultListener);
        mProgressDialog.show();
        commonData.writeSettingsIntoTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(ApplicationData.LOG_TAG, "onStart");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Log.d(ApplicationData.LOG_TAG, "onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(ApplicationData.LOG_TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(ApplicationData.LOG_TAG, "onDestroy");
        if (mWriteSettingInTimerResultListener != null) {
            commonData.removeWriteSettingResultListener(mWriteSettingInTimerResultListener);
            mWriteSettingInTimerResultListener = null;
        }
    }

    @Override
    protected ImageView getImageViewBluetoothStatus() {
        return imageViewBluetoothStatus;
    }

    @Override
    protected ApplicationData getCommonData() {
        return commonData;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @UiThread(delay = 2000)
    protected void on2000Ms() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        boolean needExecBaseMethod = false;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SAVE_FILE_ID: {
                    String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
                    if (filePath != null) {
                        if (!filePath.endsWith(".vts")) {
                            filePath += ".vts";
                        }
                        if (commonData.saveConfigToFile(filePath)) {
                            Toast.makeText(this, "save data successfull", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "save data unsuccessfull", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
                case LOAD_FILE_ID: {
                    String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
                    if (commonData.loadConfigFromFile(filePath)) {
                        Toast.makeText(this, "load data successfull", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "load data unsuccessfull", Toast.LENGTH_LONG).show();
                    }

                }
                break;
                default:
                    needExecBaseMethod = true;
                    break;
            }
        } else {
            needExecBaseMethod = true;
        }
        if (needExecBaseMethod) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}