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


public class SelectItemPositionForAlgorithmTableDialog extends SherlockDialogFragment implements View.OnClickListener {
    static public interface ButtonClickedListener {
        static final int TOP_BUTTON = 0x01;
        static final int BOTTOM_BUTTON = 0x02;
        public void onClick(int buttonId);
    }

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
        if (getActivity() instanceof ButtonClickedListener) {
            ButtonClickedListener listener = (ButtonClickedListener) getActivity();
            switch (v.getId()) {
                case R.id.button_top:
                    listener.onClick(ButtonClickedListener.TOP_BUTTON);
                    break;
                case R.id.button_below:
                    listener.onClick(ButtonClickedListener.BOTTOM_BUTTON);
                    break;
            }
        }
        dismiss();
    }
}
