package com.horrorsoft.viotimer;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.horrorsoft.viotimer.data.ICommonData;
import com.horrorsoft.viotimer.data.NumericData;
import com.horrorsoft.viotimer.data.RadioButtonAndComboBoxData;
import com.horrorsoft.viotimer.data.Separator;

import java.util.Arrays;

/**
 * This is a simple framework for a test_json of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test_json, you can type:
 * adb shell am instrument -w \
 * -e class com.horrorsoft.viotimer.MyActivityTest \
 * com.horrorsoft.viotimer.tests/android.test_json.InstrumentationTestRunner
 */
public class MyActivityTest extends ActivityInstrumentationTestCase2<MyActivity> {

    public MyActivityTest() {
        super(MyActivity.class);

    }

    public void test1() {
        assertEquals(true, true);
    }

    public void testSeparator() {
        Separator separator = new Separator();
        String descr = "I's supa separator, don't distrub me!";
        separator.setDescription(descr);
        assertEquals(descr, separator.getDescription());
    }

    public void testNumeric() {
        String description = "I'm numeric 8-bit size data!";
        String dataDescription = "I'm a numeric 8 bit size data, and my value is %s";
        String expectedDataDescription = "I'm a numeric 8 bit size data, and my value is x6,50s";
        String expectedDataDescription2 = "I'm a numeric 8 bit size data, and my value is x6.50s";

        int size = 1;
        int step = 1;
        int divider = 2;
        int pointer = 12;
        int value = 13;
        int precision = 2;
        int minValue = 0;
        int maxValue = 255;
        String suffix = "s";
        String prefix = "x";
        NumericData numericData = new NumericData();
        numericData.setDescription(description);
        numericData.setDataDescription(dataDescription);
        numericData.setPointer(pointer);
        numericData.setCurrentValue(value);
        numericData.setSize(size);
        numericData.setDivider(divider);
        numericData.setStep(step);
        numericData.setSuffix(suffix);
        numericData.setPrefix(prefix);
        numericData.setPrecision(precision);
        numericData.setMinValue(maxValue);
        numericData.setMaxValue(maxValue);

        ICommonData commonData = numericData;

        // Проверка описания занчения
        boolean firstTry = expectedDataDescription.equals(commonData.getDataDescription());
        boolean secondTry = expectedDataDescription2.equals(commonData.getDataDescription());
        assertTrue(firstTry || secondTry);

        value = 0x123456;
        size = 3;
        numericData.setCurrentValue(value);
        numericData.setSize(size);

        byte[] expectedBytes = new byte[]{0x56, 0x34, 0x12};
        // проверка выходящих данных.
        assertEquals(Arrays.toString(expectedBytes), Arrays.toString(numericData.getBinaryData()));
    }

    public void testRadioButtonAndComboboxData() {
        RadioButtonAndComboBoxData radioButtonAndComboBoxData =
                new RadioButtonAndComboBoxData(ICommonData.TYPE_COMBOBOX);
        int size = 2;
        int pointer = 34;
        String description = "I'm combobox or radiobutton";
        String dataDescription = "You have select next value: %s";

        radioButtonAndComboBoxData.setSize(size);
        radioButtonAndComboBoxData.setPointer(pointer);
        radioButtonAndComboBoxData.setDescription(description);
        radioButtonAndComboBoxData.setDataDescription(dataDescription);

        int itemValue = 12;
        String itemDescription = "ComboBoxDescription 1";
        radioButtonAndComboBoxData.addItem(itemValue, itemDescription);

        itemValue = 13;
        itemDescription = "ComboBoxDescription 2";
        radioButtonAndComboBoxData.addItem(itemValue, itemDescription);

        itemValue = 14;
        itemDescription = "ComboBoxDescription 3";
        radioButtonAndComboBoxData.addItem(itemValue, itemDescription);

        itemValue = 0x1234;
        itemDescription = "ComboBoxDescription 4";
        radioButtonAndComboBoxData.addItem(itemValue, itemDescription);

        int expectedIndex = 0;
        int value = 12;
        int index = radioButtonAndComboBoxData.getItemIndexByValue(value);
        assertEquals(expectedIndex, index);

        expectedIndex = 1;
        value = 13;
        index = radioButtonAndComboBoxData.getItemIndexByValue(value);
        assertEquals(expectedIndex, index);

        expectedIndex = 2;
        value = 14;
        index = radioButtonAndComboBoxData.getItemIndexByValue(value);
        assertEquals(expectedIndex, index);

        expectedIndex = -1;
        value = 15;
        index = radioButtonAndComboBoxData.getItemIndexByValue(value);
        assertEquals(expectedIndex, index);

        value = 0x1234;
        byte[] testByteArray = new byte[] {0x34, 0x12};
        radioButtonAndComboBoxData.setCurrentValueByBinaryData(testByteArray);

        assertEquals(value, radioButtonAndComboBoxData.getCurrentValue());

        value = 12;
        radioButtonAndComboBoxData.setCurrentValue(value);
        dataDescription = "You have select next value: %s";
        String expectedDataDescription = "You have select next value: ComboBoxDescription 1";
        radioButtonAndComboBoxData.setDataDescription(dataDescription);

        assertEquals(expectedDataDescription, radioButtonAndComboBoxData.getDataDescription());
    }
}
