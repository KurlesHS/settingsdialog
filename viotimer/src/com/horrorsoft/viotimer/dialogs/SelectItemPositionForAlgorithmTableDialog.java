package com.horrorsoft.viotimer.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.horrorsoft.viotimer.R;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 31.10.13
 * Time: 1:28
 */


public class SelectItemPositionForAlgorithmTableDialog extends SherlockDialogFragment implements  View.OnClickListener {

    public static final int TOP_BUTTON = 1;
    public static final int BOTTOM_BUTTON = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.selectitempositionforalgorithmdata, container);
        getDialog().setTitle("Add item");
        Button buttonUp = (Button) v.findViewById(R.id.button_top);
        Button buttonDown = (Button) v.findViewById(R.id.button_below);
        buttonDown.setOnClickListener(this);
        buttonUp.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (getActivity() instanceof IDialogFragmentClickListener) {
            IDialogFragmentClickListener listener = (IDialogFragmentClickListener) getActivity();
            switch (v.getId()) {
                case R.id.button_top:
                    listener.onClick(R.id.InsertAlgorithmDataUpperCurrentItem, null);
                    break;
                case R.id.button_below:
                    listener.onClick(R.id.InsertAlgorithmDataBelowCurrentItem, null);
                    break;
            }
        }
        dismiss();
    }
}
