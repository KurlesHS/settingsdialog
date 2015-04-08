package com.horrorsoft.abctimer.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import org.androidannotations.annotations.*;
import com.horrorsoft.abctimer.R;
import com.horrorsoft.abctimer.common.ApplicationData;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 04.11.13
 * Time: 0:06
 */
@EFragment(R.layout.edit_algoritm_item)
public class EditAlgorithmDataDialog extends SherlockDialogFragment implements TextView.OnEditorActionListener {


    @Bean
    ApplicationData mCommonData;

    private boolean isActive;

    boolean stopTimerForAutoPushButton = false;
    @InstanceState
    protected int delay;
    @InstanceState
    protected int servoPos;
    @InstanceState
    protected int minDelay;
    @InstanceState
    protected int maxDelay;
    @InstanceState
    protected int position;
    @InstanceState
    protected int servoNumber;

    private long lastSecond = -1;

    @InstanceState
    protected Integer instanceObject;

    @ViewById(R.id.editTextDelay)
    EditText delayTextEdit;
    @ViewById(R.id.editTextServoPos)
    EditText servoPosTextEdit;
    @ViewById(R.id.editTextServoPos)
    EditText editTextServoPos;
    @ViewById(R.id.editTextDelay)
    EditText editTextDelay;


    public static final String keyDelay = "delay";
    public static final String keyServoPos = "servoPos";
    public static final String keyMinDelay = "minDelay";
    public static final String keyMaxDelay = "maxDelay";
    public static final String keyPosition = "position";
    public static final String keyServoNumber = "servoNumber";

    private int autoPushButtonMode;
    int autoPushCount = 0;
    private static final int DELAY_MINUS_MODE = 0;
    private static final int DELAY_PLUS_MODE = 1;
    private static final int SERVO_POS_MINUS_MODE = 2;
    private static final int SERVO_POS_PLUS_MODE = 3;

    @UiThread(delay = 50)
    protected void autoPushButtonCallback() {
        int stepMultiplier = ++autoPushCount / 20 + 1;
        int newStep = stepMultiplier * stepMultiplier;
        switch (autoPushButtonMode) {
            case DELAY_MINUS_MODE: {
                if (decreaseDelay(newStep)) {
                    updateTextDelay();
                } else {
                    stopTimerForAutoPushButton = true;
                }
            }
            break;
            case DELAY_PLUS_MODE: {
                if (increaseDelay(newStep)) {
                    updateTextDelay();
                } else {
                    stopTimerForAutoPushButton = true;
                }
            }
            break;
            case SERVO_POS_MINUS_MODE: {
                if (decreaseServoPos(newStep)) {
                    updateTextServoPos();
                } else {
                    stopTimerForAutoPushButton = true;
                }
            }
            break;
            case SERVO_POS_PLUS_MODE: {
                if (increaseServoPos(newStep)) {
                    updateTextServoPos();
                } else {
                    stopTimerForAutoPushButton = true;
                }
            }
            break;
            default:
                break;
        }
        if (!stopTimerForAutoPushButton) {
            autoPushButtonCallback();
        }
    }

    private void tryToSendServoPos() {
        long sec = System.currentTimeMillis() / 333L;
        if (sec > lastSecond) {
            mCommonData.changeServoPosition(getServoNumber(), getServoPos());
            lastSecond = sec;
        }

    }

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

    public void setServoNumber(int servoNumber) {
        this.servoNumber = servoNumber;
    }

    public int getServoNumber() {
        return servoNumber;
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

    @AfterViews
    void init() {
        getDialog().setTitle("Edit algorithm data");
        if (instanceObject == null) {
            instanceObject = 1;
            Bundle agrBundle = getArguments();
            setDelay(agrBundle.getInt(keyDelay));
            setServoPos(agrBundle.getInt(keyServoPos));
            setMinDelay(agrBundle.getInt(keyMinDelay));
            setMaxDelay(agrBundle.getInt(keyMaxDelay));
            setPosition(agrBundle.getInt(keyPosition));
            setServoNumber(agrBundle.getInt(keyServoNumber));
        }
        delayTextEdit.setText(getDelayText());
        servoPosTextEdit.setText(getServoPosString());
        delayTextEdit.setOnEditorActionListener(this);
        servoPosTextEdit.setOnEditorActionListener(this);
        delayTextEdit.setOnKeyListener(new View.OnKeyListener()
        {
            /**
             * This listens for the user to press the enter button on
             * the keyboard and then hides the virtual keyboard
             */
            public boolean onKey(View arg0, int arg1, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ( (event.getAction() == KeyEvent.ACTION_DOWN  ) &&
                        (arg1           == KeyEvent.KEYCODE_ENTER)   )
                {
                    InputMethodManager imm = (InputMethodManager)mCommonData.getRootContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(delayTextEdit.getWindowToken(), 0);
                    handleTextDelayChanged(delayTextEdit.getText().toString());
                    return true;

                }
                return false;
            }
        } );
        isActive = true;
        sendServoPosInTime();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
    }

    @SuppressWarnings("InfiniteRecursion")
    @UiThread(delay=333)
    protected void sendServoPosInTime() {
        tryToSendServoPos();
        if (isActive)
            sendServoPosInTime();
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
        editTextServoPos.setText((getServoPosString()));
    }

    private void updateTextDelay() {
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


            @Click(R.id.buttonDelayDown)
    void onButtonDelayDownPushed() {
        if (decreaseDelay(1)) {
            updateTextDelay();
        }
    }

    @Click(R.id.buttonDelayUp)
    void onButtonDelayUpPushed() {
        if (increaseDelay(1)) {
            updateTextDelay();
        }
    }

    @Click(R.id.buttonServoPosDown)
    void onButtonServoPosDownPushed() {
        if (decreaseServoPos(1)) {
            updateTextServoPos();
        }
    }

    @Click(R.id.buttonServoPosUp)
    void onButtonServoPosUpPushed() {
        if (increaseServoPos(1)) {
            updateTextServoPos();
        }
    }

    @Click(R.id.buttonOk)
    void onButtonOkPushed() {
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

    // Выключаем автонажатие кнопки
    @Touch({R.id.buttonDelayDown, R.id.buttonDelayUp})
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            stopTimerForAutoPushButton = true;
        }
        return false;
    }

    @Touch({R.id.buttonServoPosUp, R.id.buttonServoPosDown})
    public boolean onTouchEvent2(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            stopTimerForAutoPushButton = true;
        }
        return false;
    }



    @LongClick({R.id.buttonDelayDown, R.id.buttonDelayUp, R.id.buttonServoPosDown, R.id.buttonServoPosUp})
    public boolean onLongClickEvent(View v) {
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
            stopTimerForAutoPushButton = false;
            autoPushButtonCallback();
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.d(ApplicationData.LOG_TAG, "Editor acton: " + String.valueOf(actionId));
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            switch (v.getId()) {
                case R.id.editTextDelay: {
                    CharSequence charSequence = v.getText();
                    if (charSequence != null) {
                        handleTextDelayChanged(charSequence.toString());
                    }
                }
                break;
                case R.id.editTextServoPos: {
                    CharSequence charSequence = v.getText();
                    if (charSequence != null) {
                        handleTextServoPosChanged(charSequence.toString());
                    }
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