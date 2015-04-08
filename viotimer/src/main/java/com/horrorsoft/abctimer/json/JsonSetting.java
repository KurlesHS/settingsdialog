package com.horrorsoft.abctimer.json;

import com.horrorsoft.abctimer.data.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 18:47
 */
public class JsonSetting {
    final static String COMMON_SETTING_ROOT_TAG = "common";
    final static String POINTER_JSON_TAG = "pointer";
    final static String DESCRIPTION_JSON_TAG = "desc";
    final static String DATA_DESCRIPTION_JSON_TAG = "formDesc";
    final static String SIZE_OF_DATA_JSON_TAG = "size";
    final static String DEFAULT_VALUE_JSON_TAG = "defValue";
    final static String PRECISION_JSON_TAG = "precision";
    final static String TYPE_OF_DATA_JSON_TAG = "type";
    final static String PREFIX_JSON_TAG = "prefix";
    final static String SUFFIX_JSON_TAG = "suffix";
    final static String DIVIDER_JSON_TAG = "divider";
    final static String MIN_VALUE_JSON_TAG = "min";
    final static String MAX_VALUE_JSON_TAG = "max";
    final static String STEP_JSON_TAG = "step";
    final static String DATA_FOR_COMBOBOX_RADIOBUTTON_JSON_TAG = "data";
    final static String VALUE_FOR_COMBOBOX_RADIOBUTTON_ITEM_JSON_TAG = "value";
    final static String DESCRIPTION_FOR_COMBOBOX_RADIOBUTTON_ITEM_JSON_TAG = "desc";

    final static String ALGORITHM_SETTING_ROOT_JSON_TAG = "alg";
    final static String ALGORITHM_DESCRIPTION_JSON_TAG = "desc";
    final static String ALGORITHM_POINTER_JSON_TAG = "pointer";

    public static AlgorithmData createAlgorithmDataByJson(String json) {
        AlgorithmData algorithmData = new AlgorithmData();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONArray mainJsonArray = jsonObject.getJSONArray(ALGORITHM_SETTING_ROOT_JSON_TAG);
            for (int idx = 0; idx < mainJsonArray.length(); ++idx) {
                JSONObject object = mainJsonArray.optJSONObject(idx);
                String description = object.getString(ALGORITHM_DESCRIPTION_JSON_TAG);
                int pointer = object.getInt(ALGORITHM_POINTER_JSON_TAG);
                algorithmData.addAlgorithm(description, pointer);
            }
        } catch (JSONException e) {
            // при любой ошибке отменяем все изменеиня
            algorithmData = new AlgorithmData();
        }
        return algorithmData;
    }

    public static List<ICommonData> createListOfDataByJson(String jsonData) {
        List<ICommonData> retList = new ArrayList<ICommonData>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray mainJsonArray = jsonObject.getJSONArray(COMMON_SETTING_ROOT_TAG);
            for (int i = 0; i < mainJsonArray.length(); ++i) {
                JSONObject jsonObjectType = mainJsonArray.getJSONObject(i);
                int typeOfData = jsonObjectType.getInt(TYPE_OF_DATA_JSON_TAG);
                switch (typeOfData) {
                    case ICommonData.TYPE_NUMERIC: {
                        handleNumericType(jsonObjectType, retList);
                    }
                    break;
                    case ICommonData.TYPE_COMBOBOX:
                    case ICommonData.TYPE_RADIOBUTTON: {
                        handleComboboxOrRadiobuttonType(jsonObjectType, retList, typeOfData);
                    }
                    break;
                    case ICommonData.TYPE_SEPARATOR: {
                        handleSeparatorType(jsonObjectType, retList);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            retList = null;
        }
        return retList;
    }

    private static void handleSeparatorType(JSONObject jsonObjectType, List<ICommonData> retList) throws JSONException {
        String desc = jsonObjectType.getString(DESCRIPTION_JSON_TAG);
        Separator separator = new Separator();
        separator.setDescription(desc);
        retList.add(separator);
    }

    private static void handleComboboxOrRadiobuttonType(JSONObject jsonObjectType, List<ICommonData> retList, int type) throws JSONException {
        int size = jsonObjectType.getInt(SIZE_OF_DATA_JSON_TAG);
        int pointer = jsonObjectType.getInt(POINTER_JSON_TAG);
        int defValue = jsonObjectType.getInt(DEFAULT_VALUE_JSON_TAG);
        String desc = jsonObjectType.getString(DESCRIPTION_JSON_TAG);
        String formDesc = jsonObjectType.getString(DATA_DESCRIPTION_JSON_TAG);
        JSONArray jsonArray = jsonObjectType.getJSONArray(DATA_FOR_COMBOBOX_RADIOBUTTON_JSON_TAG);

        RadioButtonAndComboBoxData radioButtonAndComboBoxData = new RadioButtonAndComboBoxData(type);
        radioButtonAndComboBoxData.setSize(size);
        radioButtonAndComboBoxData.setPointer(pointer);
        radioButtonAndComboBoxData.setCurrentValue(defValue);
        radioButtonAndComboBoxData.setDescription(desc);
        radioButtonAndComboBoxData.setDataDescription(formDesc);

        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int value = jsonObject.getInt(VALUE_FOR_COMBOBOX_RADIOBUTTON_ITEM_JSON_TAG);
            String description = jsonObject.getString(DESCRIPTION_FOR_COMBOBOX_RADIOBUTTON_ITEM_JSON_TAG);
            radioButtonAndComboBoxData.addItem(value, description);
        }
        retList.add(radioButtonAndComboBoxData);
    }

    private static void handleNumericType(JSONObject jsonObjectType, List<ICommonData> retList) throws JSONException {
        int size = jsonObjectType.getInt(SIZE_OF_DATA_JSON_TAG);
        int divider = jsonObjectType.getInt(DIVIDER_JSON_TAG);
        int step = jsonObjectType.getInt(STEP_JSON_TAG);
        int min = jsonObjectType.getInt(MIN_VALUE_JSON_TAG);
        int max = jsonObjectType.getInt(MAX_VALUE_JSON_TAG);
        int defValue = jsonObjectType.getInt(DEFAULT_VALUE_JSON_TAG);
        int precision = jsonObjectType.getInt(PRECISION_JSON_TAG);
        int pointer = jsonObjectType.getInt(POINTER_JSON_TAG);
        String desc = jsonObjectType.getString(DESCRIPTION_JSON_TAG);
        String formDesc = jsonObjectType.getString(DATA_DESCRIPTION_JSON_TAG);
        String prefix = jsonObjectType.getString(PREFIX_JSON_TAG);
        String suffix = jsonObjectType.getString(SUFFIX_JSON_TAG);
        NumericData numericData = new NumericData();
        numericData.setSize(size);
        numericData.setDivider(divider);
        numericData.setStep(step);
        numericData.setMinValue(min);
        numericData.setMaxValue(max);
        numericData.setCurrentValue(defValue);
        numericData.setPrecision(precision);
        numericData.setPointer(pointer);
        numericData.setDescription(desc);
        numericData.setDataDescription(formDesc);
        numericData.setPrefix(prefix);
        numericData.setSuffix(suffix);
        retList.add(numericData);
    }
}
