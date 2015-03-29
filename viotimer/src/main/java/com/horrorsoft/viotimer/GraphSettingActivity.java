package com.horrorsoft.viotimer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.horrorsoft.viotimer.bluetooth.ITimerCommandResultListener;
import com.horrorsoft.viotimer.bluetooth.TimerProtocol;
import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.*;

/**
 * Created by Alexey on 24.11.2014.
 *
 */
@Fullscreen
@EActivity(R.layout.activity_setup_graph)
public class GraphSettingActivity extends SherlockActivity {
    @Bean
    protected ApplicationData commonData;

    private ProgressDialog mProgressDialog = null;
    private ITimerCommandResultListener mReadFlightHistoryListener = null;

    @Click(R.id.showGraphButton)
    public void handleGraphButtonPushed() {
        if (commonData.getFlightHistoryData() == null) {
            Toast.makeText(this, "we has no flight data to visualize", Toast.LENGTH_SHORT).show();
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
                if (currentPos == 0) {
                    mProgressDialog.setMax(maxPos);
                }
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
}
