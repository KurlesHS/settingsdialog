package com.horrorsoft.viotimer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.horrorsoft.viotimer.bluetooth.ITimerCommandResultListener;
import com.horrorsoft.viotimer.bluetooth.TimerProtocol;
import com.horrorsoft.viotimer.common.ApplicationData;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;
import org.androidannotations.annotations.*;

import java.io.*;

/**
 * Created by Alexey on 24.11.2014.
 *
 */
@Fullscreen
@EActivity(R.layout.activity_setup_graph)
public class GraphSettingActivity extends ActivityWithBluetoothStatuses {
    private static final int SAVE_FILE_ID = 0x01;
    private static final int LOAD_FILE_ID = 0x02;

    @Bean
    protected ApplicationData commonData;

    @ViewById(R.id.imageViewBluetoothStatus)
    protected ImageView imageViewBluetoothStatus;


    private ProgressDialog mProgressDialog = null;
    private ITimerCommandResultListener mReadFlightHistoryListener = null;

    @Click(R.id.loadGraphFileButtonId)
    protected void handleLoadButton() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_OPEN);

        //can user select directories or not
        intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

        //alternatively you can set file filter
        intent.putExtra(FileDialog.FORMAT_FILTER, new String[]{"vtf", "vtf_b", "VTF","VTF_B"});
        startActivityForResult(intent, LOAD_FILE_ID);
    }

    @Click(R.id.saveGraphFileButtonId)
    protected void handleSaveButton() {
        if (commonData.getFlightHistoryData() == null) {
            Toast.makeText(this, "we have no flight data to save", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getBaseContext(), FileDialog.class);
            intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());
            intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);

            //can user select directories or not
            intent.putExtra(FileDialog.CAN_SELECT_DIR, false);

            //alternatively you can set file filter
            intent.putExtra(FileDialog.FORMAT_FILTER, new String[]{"vtf", "VTF"});
            startActivityForResult(intent, SAVE_FILE_ID);
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
                        if (!filePath.endsWith(".vtf")) {
                            filePath += ".vtf";
                        }
                        saveFlightData(filePath);
                    }
                }
                break;
                case LOAD_FILE_ID: {
                    String filePath = data.getStringExtra(FileDialog.RESULT_PATH);
                    if (filePath != null) {
                        boolean rawFlightData = filePath.endsWith(".vtf_b");
                        loadFlightData(filePath, rawFlightData);
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

    private boolean loadFlightData(String filepath, boolean rawFlightData) {
        FileInputStream inputStream;
        boolean retVal = false;
        try {
            inputStream = new FileInputStream(new File(filepath));
            byte[] buffer = new byte[0x20 * 200];
            short crc16 = 0;
            while (true) {
                if (!rawFlightData) {
                    int bytes = inputStream.read(buffer, 0x00, 0x02);
                    if (bytes != 2) {
                        break;
                    }
                    crc16 = (short) ((buffer[0] & 0xff) | ((buffer[1] % 0xff) << 0x08));
                }
                int bytes = inputStream.read(buffer, 0, buffer.length);
                if (bytes != buffer.length) {
                    break;
                }
                if (rawFlightData) {
                    retVal = true;
                } else {
                    short realCrc = TimerProtocol.calculateCrc16(buffer, buffer.length);
                    retVal = realCrc == crc16;
                    //retVal = true;
                }
                if (retVal) {
                    commonData.setFlightHistoryData(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    private boolean saveFlightData(String filepath) {
        boolean retVal = false;
        byte[] dataToSave = commonData.getFlightHistoryData();
        if (dataToSave != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(filepath));
                short crc16 = TimerProtocol.calculateCrc16(dataToSave, dataToSave.length);
                int low = crc16 & 0xff;
                int hight = (crc16 >>> 0x08) & 0xff;
                outputStream.write(low);
                outputStream.write(hight);
                outputStream.write(dataToSave);
                outputStream.close();
                retVal = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    @Click(R.id.showGraphButton)
    public void handleGraphButtonPushed() {
        if (commonData.getFlightHistoryData() == null) {
            Toast.makeText(this, "we have no flight data to visualize", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, GraphActivity_.class);
        startActivity(intent);
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

    @Click(R.id.readGraphFromTimerButton)
    protected void handleReadGraphFromTimerButtonPushed() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Read setting ...");
        mProgressDialog.setMessage("Read setting in progress ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(0);
        mReadFlightHistoryListener = new ITimerCommandResultListener() {
            @Override
            public void result(int status) {
                if (status == TimerProtocol.RESULT_OK) {
                    mProgressDialog.setMessage("Success");
                    closeProgressDialogAfterFiveSecond();
                }  else if (status == TimerProtocol.RESULT_FAIL) {
                    mProgressDialog.setMessage("Failure");
                    closeProgressDialogAfterFiveSecond();
                } else {
                    closeProgressDialog();
                }
                commonData.removeReadFlightHistoryFromTimerResultListener(this);
                mReadFlightHistoryListener = null;
            }

            @Override
            public void process(int currentPos, int maxPos) {
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
        commonData.addReadFlightHistoryFromTimerResultListener(mReadFlightHistoryListener);
        mProgressDialog.show();
        commonData.readFlightHistoryFromTimer();
    }

    @Override
    protected ImageView getImageViewBluetoothStatus() {
        return imageViewBluetoothStatus;
    }

    @Override
    protected ApplicationData getCommonData() {
        return commonData;
    }
}
