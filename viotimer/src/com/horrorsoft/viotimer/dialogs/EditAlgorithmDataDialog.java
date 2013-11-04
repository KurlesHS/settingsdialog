package com.horrorsoft.viotimer.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.horrorsoft.viotimer.R;
import com.horrorsoft.viotimer.common.ApplicationData;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 04.11.13
 * Time: 0:06
 */
public class EditAlgorithmDataDialog extends SherlockDialogFragment implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, TextView.OnEditorActionListener {

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

    private int autoPushButtonMode;
    int autoPushCount = 0;
    private static final int DELAY_MINUS_MODE = 0;
    private static final int DELAY_PLUS_MODE = 1;
    private static final int SERVO_POS_MINUS_MODE = 2;
    private static final int SERVO_POS_PLUS_MODE = 3;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            boolean stopTimer = false;
            int stepMultiplier = ++autoPushCount / 20 + 1;
            int newStep = stepMultiplier * stepMultiplier;
            switch (autoPushButtonMode) {
                case DELAY_MINUS_MODE: {
                    if (decreaseDelay(newStep)) {
                        updateTextDelay();
                    } else {
                        stopTimer = true;
                    }
                }
                break;
                case DELAY_PLUS_MODE: {
                    if (increaseDelay(newStep)) {
                        updateTextDelay();
                    } else {
                        stopTimer = true;
                    }
                }
                break;
                case SERVO_POS_MINUS_MODE: {
                    if (decreaseServoPos(newStep)) {
                        updateTextServoPos();
                    } else {
                        stopTimer = true;
                    }
                }
                break;
                case SERVO_POS_PLUS_MODE: {
                    if (increaseServoPos(newStep)) {
                        updateTextServoPos();
                    } else {
                        stopTimer = true;
                    }
                }
                break;
                default:
                    break;
            }
            if (stopTimer) {
                timerHandler.removeCallbacks(this);
            } else
                timerHandler.postDelayed(this, 50);
        }
    };

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
        buttonDelayDown.setOnLongClickListener(this);
        buttonDelayDown.setOnTouchListener(this);
        buttonDelayUp.setOnClickListener(this);
        buttonDelayUp.setOnLongClickListener(this);
        buttonDelayUp.setOnTouchListener(this);
        buttonServoPosDown.setOnClickListener(this);
        buttonServoPosDown.setOnLongClickListener(this);
        buttonServoPosDown.setOnTouchListener(this);
        buttonServoPosUp.setOnClickListener(this);
        buttonServoPosUp.setOnLongClickListener(this);
        buttonServoPosUp.setOnTouchListener(this);
        buttonOk.setOnClickListener(this);

        Bundle agrBundle = savedInstanceState != null ? savedInstanceState : getArguments();
        setDelay(agrBundle.getInt(keyDelay));
        setServoPos(agrBundle.getInt(keyServoPos));
        setMinDelay(agrBundle.getInt(keyMinDelay));
        setMaxDelay(agrBundle.getInt(keyMaxDelay));
        setPosition(agrBundle.getInt(keyPosition));

        EditText delayTextEdit = (EditText) currentView.findViewById(R.id.editTextDelay);
        EditText servoPosTextEdit = (EditText) currentView.findViewById(R.id.editTextServoPos);
        delayTextEdit.setText(getDelayText());
        servoPosTextEdit.setText(getServoPosString());
        delayTextEdit.setOnEditorActionListener(this);
        servoPosTextEdit.setOnEditorActionListener(this);
        return currentView;
    }

    private String getDelayText() {
        return ApplicationData.getDelayText(getDelay(), false);
    }

    private String getServoPosString() {
        return ApplicationData.getServoPosString(getServoPos(), false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private void updateTextServoPos() {
        EditText editTextServoPos = (EditText) currentView.findViewById(R.id.editTextServoPos);
        editTextServoPos.setText((getServoPosString()));
    }

    private void updateTextDelay() {
        EditText editTextDelay = (EditText) currentView.findViewById(R.id.editTextDelay);
        editTextDelay.setText(getDelayText());
    }

    private boolean increaseDelay(int delta) {
        if (getDelay() >= getMaxDelay())
            return false;
        int newDelay = getDelay() + delta;
        if (newDelay > getMaxDelay()) {
            newDelay = getMaxDelay();
        }
        setDelay(newDelay);
        return true;
    }

    private boolean decreaseDelay(int delta) {
        if (getDelay() <= getMinDelay())
            return false;
        int newDelay = getDelay() - delta;
        if (newDelay < getMinDelay()) {
            newDelay = getMinDelay();
        }
        setDelay(newDelay);
        return true;
    }

    private boolean increaseServoPos(int delta) {
        if (getServoPos() >= 255)
            return false;
        int newServoPos = getServoPos() + delta;
        if (newServoPos > 255) {
            newServoPos = 255;
        }
        setServoPos(newServoPos);
        return true;
    }

    private boolean decreaseServoPos(int delta) {
        if (getServoPos() <= 0)
            return false;
        int newServoPos = getServoPos() - delta;
        if (newServoPos < 0) {
            newServoPos = 0;
        }
        setServoPos(newServoPos);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDelayDown: {
                if (decreaseDelay(1)) {
                    updateTextDelay();
                }
            }
            break;
            case R.id.buttonDelayUp: {

                if (increaseDelay(1)) {
                    updateTextDelay();
                }
            }
            break;
            case R.id.buttonServoPosDown: {
                if (decreaseServoPos(1)) {
                    updateTextServoPos();
                }
            }
            break;
            case R.id.buttonServoPosUp: {
                if (increaseServoPos(1)) {
                    updateTextServoPos();
                }
            }
            break;
            case R.id.buttonOk: {
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

    // Выключаем автонажатие кнопки
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        boolean ok = true;
        switch (v.getId()) {
            case R.id.buttonDelayDown: {
                autoPushButtonMode = DELAY_MINUS_MODE;
            }
            break;
            case R.id.buttonDelayUp: {
                autoPushButtonMode = DELAY_PLUS_MODE;
            }
            break;
            case R.id.buttonServoPosDown: {
                autoPushButtonMode = SERVO_POS_MINUS_MODE;
            }
            break;
            case R.id.buttonServoPosUp: {
                autoPushButtonMode = SERVO_POS_PLUS_MODE;
            }
            break;
            default:
                ok = false;
        }
        if (ok) {
            autoPushCount = 0;
            timerHandler.postDelayed(timerRunnable, 50);
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            switch (v.getId()) {
                case R.id.editTextDelay: {
                    handleTextDelayChanged(v.getText().toString());
                }
                break;
                case R.id.editTextServoPos: {
                    handleTextServoPosChanged(v.getText().toString());
                }
                break;
                default:
                    break;
            }
        }
        return false;
    }


    private void handleTextServoPosChanged(String servoPosString) {
        try {
            int servoPos = Integer.parseInt(servoPosString);
            if (servoPos < 0) {
                servoPos = 0;
            }
            if (servoPos > 255) {
                servoPos = 255;
            }
            setServoPos(servoPos);
        } catch (NumberFormatException e) {
            Log.d(ApplicationData.LOG_TAG, "wrong servo pos number");
        } finally {
            updateTextServoPos();
        }
    }

    private void handleTextDelayChanged(String delayString) {
        try {
            int delay = ApplicationData.parseAlgorithmDelay(delayString);
            if (delay < getMinDelay()) {
                delay = getMinDelay();
            }
            if (delay > getMaxDelay()) {
                delay = getMaxDelay();
            }
            setDelay(delay);
        } catch (NumberFormatException e) {
            Log.d(ApplicationData.LOG_TAG, "wrong delay number");
        } finally {
            updateTextDelay();
        }

    }

}