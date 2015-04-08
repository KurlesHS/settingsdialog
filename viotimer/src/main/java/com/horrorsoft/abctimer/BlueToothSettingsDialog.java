package com.horrorsoft.abctimer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;

import java.util.Arrays;

/**
 * Created by Admin on 04.04.2015.
 * Confirm
 */
public class BlueToothSettingsDialog extends SherlockDialogFragment implements View.OnClickListener{


    private INewBluetoothSettingListener settingListener;
    private Button applyButton;
    private EditText pinTextEdit;
    private EditText nameTextEdit;

    public void setSettingListener(INewBluetoothSettingListener settingListener) {
        this.settingListener = settingListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bt_settings_layout, container);
        if (v != null) {
            getDialog().setTitle("Bluetooth settings");
            applyButton = (Button) v.findViewById(R.id.applyButton);
            pinTextEdit = (EditText) v.findViewById(R.id.editTextPin);
            nameTextEdit = (EditText) v.findViewById(R.id.editTextBluetoothName);

            if (applyButton != null) {
                applyButton.setOnClickListener(this);
                pinTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                String text = v.getText().toString();
                                int zeroToAdd = 4 - text.length();
                                if (zeroToAdd > 0) {
                                    char[] zeros = new char[zeroToAdd];
                                    Arrays.fill(zeros, '0');
                                    text = new String((zeros)) + text;
                                    v.setText(text);
                                }
                                return false; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                });
            }
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applyButton:
                if (settingListener != null && pinTextEdit != null && nameTextEdit != null) {
                    settingListener.newSettings(pinTextEdit.getText().toString(), nameTextEdit.getText().toString());
                }
                break;
        }

        dismiss();
    }
}
