package com.horrorsoft.viotimer.dialogs;

import android.content.ComponentCallbacks2;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.horrorsoft.viotimer.R;
import com.horrorsoft.viotimer.common.ApplicationData;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 04.11.13
 * Time: 0:06
 */
public class EditAlgorithmDataDialog extends SherlockDialogFragment implements View.OnClickListener {

    private int delay;
    private int servoPos;
    private int minDelay;
    private int maxDelay;
    private int position;
    private View currentView;

    public static final String keyDelay = "delay";
    public static final String keyServoPos = "servoPos";
    public static final String keyMinDelay = "minDelay";
    public static final String keyMaxDelay = "maxDelay";
    public static final String keyPosition = "position";

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getServoPos() {
        return servoPos;
    }

    public void setServoPos(int servoPos) {
        this.servoPos = servoPos;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(int minDelay) {
        this.minDelay = minDelay;
    }

    public int getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(keyDelay, getDelay());
        outState.putInt(keyServoPos, getServoPos());
        outState.putInt(keyMaxDelay, getMaxDelay());
        outState.putInt(keyMinDelay, getMinDelay());
        outState.putInt(keyPosition, getPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.edit_algoritm_item, container);
        getDialog().setTitle("Edit algorithm data");
        Button buttonDelayUp = (Button) currentView.findViewById(R.id.buttonDelayUp);
        Button buttonDelayDown = (Button) currentView.findViewById(R.id.buttonDelayDown);
        Button buttonServoPosUp = (Button) currentView.findViewById(R.id.buttonServoPosUp);
        Button buttonServoPosDown = (Button) currentView.findViewById(R.id.buttonServoPosDown);
        Button buttonOk = (Button) currentView.findViewById(R.id.buttonOk);
        buttonDelayDown.setOnClickListener(this);
        buttonDelayUp.setOnClickListener(this);
        buttonServoPosDown.setOnClickListener(this);
        buttonServoPosUp.setOnClickListener(this);
        buttonOk.setOnClickListener(this);

        Bundle agrBundle = savedInstanceState != null ? savedInstanceState : getArguments();
        setDelay(agrBundle.getInt(keyDelay));
        setServoPos(agrBundle.getInt(keyServoPos));
        setMinDelay(agrBundle.getInt(keyMinDelay));
        setMaxDelay(agrBundle.getInt(keyMaxDelay));
        setPosition(agrBundle.getInt(keyPosition));

        EditText delayTextEdit  = (EditText) currentView.findViewById(R.id.editTextDelay);
        EditText servoPosTextEdit = (EditText) currentView.findViewById(R.id.editTextServoPos);
        delayTextEdit.setText(getDelayText());
        servoPosTextEdit.setText(getServoPosString());
        return currentView;
    }

    private String getDelayText() {
        return ApplicationData.getDelayText(getDelay());
    }

    private String getServoPosString() {
         return ApplicationData.getServoPosString(getServoPos());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(View v) {
        EditText editTextServoPos = (EditText) currentView.findViewById(R.id.editTextServoPos);
        EditText editTextDelay = (EditText) currentView.findViewById(R.id.editTextDelay);
        switch (v.getId()) {
              case R.id.buttonDelayDown:
              {
                  if (getDelay() > getMinDelay()) {
                      setDelay(getDelay() - 1);
                      editTextDelay.setText(getDelayText());
                  }
              }
              break;
              case R.id.buttonDelayUp:
              {
                  if (getDelay() < getMaxDelay()) {
                      setDelay(getDelay() + 1);
                      editTextDelay.setText(getDelayText());
                  }
              }
              break;
              case R.id.buttonServoPosDown:
              {
                  if (getServoPos() > 0) {
                      setServoPos(getServoPos() - 1);
                      editTextServoPos.setText(getServoPosString());
                  }
              }
              break;
              case R.id.buttonServoPosUp:
              {
                  if (getServoPos() < 255) {
                      setServoPos(getServoPos() + 1);
                      editTextServoPos.setText(getServoPosString());
                  }
              }
              break;
              case R.id.buttonOk:
              {
                  if (getActivity() instanceof IDialogFragmentClickListener) {
                      IDialogFragmentClickListener dfcl = (IDialogFragmentClickListener) getActivity();
                      Bundle bundle = new Bundle();
                      bundle.putInt(keyDelay, getDelay());
                      bundle.putInt(keyServoPos, getServoPos());
                      bundle.putInt(keyPosition, getPosition());
                      dfcl.onClick(R.id.AlgorithmDataChanged, bundle);
                  }
                  dismiss();
              }
              break;
              default:
                  break;
          }
    }
}
