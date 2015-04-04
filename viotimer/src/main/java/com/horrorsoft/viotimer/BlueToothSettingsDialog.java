package com.horrorsoft.viotimer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.horrorsoft.viotimer.dialogs.IDialogFragmentClickListener;

/**
 * Created by Admin on 04.04.2015.
 * Confirm
 */
public class BlueToothSettingsDialog extends SherlockDialogFragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bt_settings_layout, container);
        if (v != null) {
            getDialog().setTitle("Add item");
            Button applyButton = (Button) v.findViewById(R.id.applyButton);
            if (applyButton != null) {
                applyButton.setOnClickListener(this);
            }
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        if (getActivity() instanceof IDialogFragmentClickListener) {
            IDialogFragmentClickListener listener = (IDialogFragmentClickListener) getActivity();
            switch (v.getId()) {
                case R.id.applyButton:
                    listener.onClick(R.id.InsertAlgorithmDataUpperCurrentItem, null);
                    break;
            }
        }
        dismiss();
    }
}
