package com.horrorsoft.viotimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.SherlockActivity;
import com.horrorsoft.viotimer.adapters.SettingDialogAdapter;
import com.horrorsoft.viotimer.data.ICommonData;
import com.horrorsoft.viotimer.data.NumericData;
import com.horrorsoft.viotimer.data.RadioButtonAndComboBoxData;
import com.horrorsoft.viotimer.data.Separator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 24.10.13
 * Time: 23:16
 */


public class SettingActivity extends SherlockActivity implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    //private static String LOG_TAG = "MyTag";
    final static int EDIT_SETTING_DIALOG = 1;
    final static int PLUS_OPERATION = 1;
    final static int MINUS_OPERATION = 2;
    List<ICommonData> listOfData;
    byte[] array;
    String xmlData;
    ICommonData dataForDialog;
    int lastPosition;
    int lastComboboxOrRadiobuttonIndexSelected;
    View viewForSpinBoxDialog;
    int plusOrMinusMode;
    int autoPushCount = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            boolean stopTimer = false;
            NumericData numericData = (NumericData) dataForDialog;
            if (numericData != null) {
                int currentValue = numericData.getCurrentValue();
                int step = numericData.getStep();
                int newStep = (int) Math.pow(step,((++autoPushCount / 20)));
                int minValue = numericData.getMinValue();
                int maxValue = numericData.getMaxValue();
                int newValue = minValue;
                boolean ok = false;
                switch (plusOrMinusMode) {
                    case MINUS_OPERATION: {
                        newValue = currentValue - newStep;
                        if (newValue < minValue) {
                            newValue = minValue;
                            stopTimer = true;
                        }
                        ok = true;
                    }
                    break;
                    case PLUS_OPERATION: {
                        newValue = currentValue + newStep;
                        if (newValue > maxValue) {
                            newValue = maxValue;
                            stopTimer = true;
                        }
                        ok = true;
                    }
                    break;
                    default:
                        break;
                }
                if (ok) {
                    numericData.setCurrentValue(newValue);
                    if (viewForSpinBoxDialog != null) {
                        EditText editText = (EditText) viewForSpinBoxDialog.findViewById(R.id.editTextSpinBox);
                        editText.setText(IntWithDividerAndPrecisionToString(newValue, numericData.getDivider(), numericData.getPrecision()));
                    }
                }
            }
            if (stopTimer)
                timerHandler.removeCallbacks(this);
            else
                timerHandler.postDelayed(this, 50);
        }
    };

    private DialogInterface.OnDismissListener myDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            removeDialog(EDIT_SETTING_DIALOG);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglayot);
        xmlData = getIntent().getStringExtra("xmlData");
        array = getIntent().getByteArrayExtra("array");
        fillData();
        SettingDialogAdapter settingDialogAdapter = new SettingDialogAdapter(this, listOfData);
        ListView lv = (ListView) findViewById(R.id.listViewForSettingsDialog);
        lv.setAdapter(settingDialogAdapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == EDIT_SETTING_DIALOG) {
            if (dataForDialog == null)
                return null;
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(dataForDialog.getDescription());
            adb.setPositiveButton("Ok", myClickListener);
            switch (dataForDialog.getType()) {
                case ICommonData.TYPE_RADIOBUTTON: {
                    CreateRadioButtonDialog(adb);
                }
                break;
                case ICommonData.TYPE_COMBOBOX: {
                    CreateComboboxDialog(adb);
                }
                break;
                case ICommonData.TYPE_NUMERIC: {

                    View view = getLayoutInflater().inflate(R.layout.editnumbers, null);
                    viewForSpinBoxDialog = view;
                    EditText editText = (EditText) view.findViewById(R.id.editTextSpinBox);
                    NumericData numericData = (NumericData) dataForDialog;
                    int currentValue = numericData.getCurrentValue();
                    int precision = numericData.getPrecision();
                    int divider = numericData.getDivider();
                    editText.setText(IntWithDividerAndPrecisionToString(currentValue, divider, precision));
                    editText.setOnEditorActionListener(this);
                    TextView textViewSuffix = (TextView) view.findViewById(R.id.textViewSpinboxSuffix);
                    TextView textViewPrefix = (TextView) view.findViewById(R.id.textViewSpinboxPrefix);
                    textViewPrefix.setText(numericData.getPrefix());
                    textViewSuffix.setText(numericData.getSuffix());
                    Button plus = (Button) view.findViewById(R.id.buttonSpinBoxPlus);
                    Button minus = (Button) view.findViewById(R.id.buttonSpinBoxMinus);
                    plus.setLongClickable(true);
                    plus.setOnClickListener(this);
                    plus.setOnLongClickListener(this);
                    plus.setOnTouchListener(this);
                    minus.setLongClickable(true);
                    minus.setOnClickListener(this);
                    minus.setOnLongClickListener(this);
                    minus.setOnTouchListener(this);

                    adb.setView(view);
                }
                break;
                case ICommonData.TYPE_SEPARATOR:
                case ICommonData.TYPE_UNKNOWN:
                    break;
            }
            Dialog dlg = adb.create();
            dlg.setOnDismissListener(myDismissListener);
            return dlg;
        }
        return null;
    }

    private String IntWithDividerAndPrecisionToString(int value, int divider, int precision) {
        return DoubleToString(value / 1.0 / divider, precision);
    }

    private String DoubleToString(double value, int precision) {
        String formatString = "%." + precision + "f";
        String retStr = String.format(formatString, value);
        retStr = retStr.replace(',', '.');
        return retStr;
    }

    private void CreateComboboxDialog(AlertDialog.Builder adb) {
        Spinner spinner = new Spinner(this);

        RadioButtonAndComboBoxData radioButtonData = (RadioButtonAndComboBoxData) dataForDialog;
        if (radioButtonData != null) {
            List<RadioButtonAndComboBoxData.ItemData> listOfItemData = radioButtonData.getListOfItemData();
            String[] listOfItemForCombobox = new String[listOfItemData.size()];
            for (int i = 0; i < listOfItemData.size(); ++i) {
                listOfItemForCombobox[i] = listOfItemData.get(i).description;
            }

            int currentIndex = radioButtonData.getCurrentIndex();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, listOfItemForCombobox);
            adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(currentIndex);
            spinner.setOnItemSelectedListener(myItemClickListener);
            lastComboboxOrRadiobuttonIndexSelected = currentIndex;
            adb.setView(spinner);
        }
    }

    AdapterView.OnItemSelectedListener myItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lastComboboxOrRadiobuttonIndexSelected = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };


    private void CreateRadioButtonDialog(AlertDialog.Builder adb) {
        RadioButtonAndComboBoxData radioButtonData = (RadioButtonAndComboBoxData) dataForDialog;
        if (radioButtonData != null) {
            List<RadioButtonAndComboBoxData.ItemData> listOfItemData = radioButtonData.getListOfItemData();
            String[] listOfItemForCombobox = new String[listOfItemData.size()];
            for (int i = 0; i < listOfItemData.size(); ++i) {
                listOfItemForCombobox[i] = listOfItemData.get(i).description;
            }

            int currentIndex = radioButtonData.getCurrentIndex();
            adb.setSingleChoiceItems(listOfItemForCombobox, currentIndex, myClickListener);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        super.onPrepareDialog(id, dialog);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dataForDialog = listOfData.get(position);
        lastPosition = position;
        showDialog(EDIT_SETTING_DIALOG);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            if (which == Dialog.BUTTON_POSITIVE) {
                timerHandler.removeCallbacks(timerRunnable);
                switch (dataForDialog.getType()) {
                    case ICommonData.TYPE_RADIOBUTTON: {
                        updateRadiobuttonAndComboboxData(lastComboboxOrRadiobuttonIndexSelected);
                    }
                    break;
                    case ICommonData.TYPE_COMBOBOX: {
                        updateRadiobuttonAndComboboxData(lastComboboxOrRadiobuttonIndexSelected);
                    }
                    break;
                    case ICommonData.TYPE_NUMERIC: {
                        updateCurrentItem();
                    }
                }
            } else {
                lastComboboxOrRadiobuttonIndexSelected = which;
            }
        }
    };

    private void updateRadiobuttonAndComboboxData(int selectedIndex) {
        RadioButtonAndComboBoxData radioButtonAndComboboxData = (RadioButtonAndComboBoxData) dataForDialog;
        if (radioButtonAndComboboxData != null) {
            radioButtonAndComboboxData.setCurrentValueByIndex(selectedIndex);
            updateCurrentItem();
        }
    }

    private void updateCurrentItem() {
        // поиск вьюхи по позиции. Подходит только для видимых элементов
        ListView lvs = (ListView) findViewById(R.id.listViewForSettingsDialog);
        View v = lvs.getChildAt(lastPosition - lvs.getFirstVisiblePosition());
        TextView tv = (TextView) v.findViewById(R.id.textViewDataDescription);
        tv.setText(dataForDialog.getDataDescription());
    }

    private void fillData() {
        listOfData = new ArrayList<ICommonData>();
        int forType = 0;
        boolean x = true;
        for (int i = 1; i <= 22; ++i) {
            switch (forType) {
                case 0: {
                    NumericData numericData = new NumericData();
                    numericData.setCurrentValue(i * 233 + i);
                    numericData.setSize(2);
                    numericData.setStep(10);
                    numericData.setDivider(127);
                    numericData.setPrecision(3);
                    numericData.setMaxValue(10000000);
                    numericData.setMinValue(0);
                    numericData.setPrefix("pref ");
                    numericData.setSuffix("sec");
                    numericData.setDataDescription("Numeric value: %s");
                    numericData.setDescription("Numeric data");
                    listOfData.add(numericData);
                }
                break;
                case 1: {
                    RadioButtonAndComboBoxData radioButtonAndComboBoxData =
                            new RadioButtonAndComboBoxData(x ? ICommonData.TYPE_RADIOBUTTON : ICommonData.TYPE_COMBOBOX);
                    String strType = x ? "RadioButton" : "Combobox";
                    x = !x;
                    radioButtonAndComboBoxData.setCurrentValue(15);
                    radioButtonAndComboBoxData.setDescription(String.format("%s data", strType));
                    radioButtonAndComboBoxData.setDataDescription(String.format("%s value: ", strType) + "%s");
                    radioButtonAndComboBoxData.addItem(13, "value number one");
                    radioButtonAndComboBoxData.addItem(14, "value number two");
                    radioButtonAndComboBoxData.addItem(15, "value number three");
                    radioButtonAndComboBoxData.addItem(16, "value number four");

                    listOfData.add(radioButtonAndComboBoxData);
                }
                break;
                case 2: {
                    Separator separator = new Separator();
                    separator.setDescription("Separator");
                    listOfData.add(separator);
                }
                break;
            }
            ++forType;
            forType %= 3;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (event == null) {
            NumericData numericData = (NumericData) dataForDialog;
            double dValue = Double.parseDouble(v.getText().toString());
            int value = (int) (dValue * numericData.getDivider());
            if (value < numericData.getMinValue()) {
                value = numericData.getMinValue();
            } else if (value > numericData.getMaxValue()) {
                value = numericData.getMaxValue();
            }
            numericData.setCurrentValue(value);
            v.setText(IntWithDividerAndPrecisionToString(value, numericData.getDivider(), numericData.getPrecision()));
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        NumericData numericData = (NumericData) dataForDialog;
        if (numericData == null) {
            return;
        }
        int currentValue = numericData.getCurrentValue();
        int step = numericData.getStep();
        int minValue = numericData.getMinValue();
        int maxValue = numericData.getMaxValue();
        int newValue;
        switch (v.getId()) {
            case R.id.buttonSpinBoxMinus: {
                newValue = currentValue - step;
                if (newValue < minValue)
                    newValue = minValue;
            }
            break;
            case R.id.buttonSpinBoxPlus: {
                newValue = currentValue + step;
                if (newValue > maxValue) {
                    newValue = maxValue;
                }
            }
            break;
            default:
                return;
        }
        numericData.setCurrentValue(newValue);
        if (viewForSpinBoxDialog != null) {
            EditText editText = (EditText) viewForSpinBoxDialog.findViewById(R.id.editTextSpinBox);
            editText.setText(IntWithDividerAndPrecisionToString(newValue, numericData.getDivider(), numericData.getPrecision()));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.buttonSpinBoxMinus) {
            plusOrMinusMode = MINUS_OPERATION;
        } else if (v.getId() == R.id.buttonSpinBoxPlus) {
            plusOrMinusMode = PLUS_OPERATION;
        } else {
            return false;
        }
        autoPushCount = 0;
        timerHandler.postDelayed(timerRunnable, 0);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}