package com.horrorsoft.viotimer.common;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.horrorsoft.viotimer.R;
import com.horrorsoft.viotimer.data.AlgorithmData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 22:21
 */
public class ApplicationData {
    public static final String LOG_TAG = "com.horrorsoft.viotimer";
    private static ApplicationData instance;
    private String jsonData;
    private byte[] binaryData;
    private AlgorithmData algorithmData;
    private static float dividerForAlgorithmDelay;
    private static int globalMaxDelay;

    public static void setDividerForAlgorithmDelay(float divider) {
        dividerForAlgorithmDelay = divider;
    }

    public static void setGlobalMaxDelay(int delay) {
        globalMaxDelay = delay;
    }

    public static int getGlobalMaxDelay() {
        return globalMaxDelay;
    }

    public AlgorithmData getAlgorithmData() {
        return algorithmData;
    }

    public void setAlgorithmData(AlgorithmData algorithmData) {
        this.algorithmData = algorithmData;
    }

    public static int parseAlgorithmDelay(String delay) throws NumberFormatException {
        float delayFloat = Float.parseFloat(delay);
        return (int) (delayFloat * dividerForAlgorithmDelay);
    }

    public static String addZeros(String res, int numOfCharactersExpected) {
        String retStr = res;
        int countOfZeroToAdd = numOfCharactersExpected - retStr.length();
        while (countOfZeroToAdd > 0) {
            retStr = "0" + retStr;
            --countOfZeroToAdd;
        }
        return retStr;
    }

    public static String getDelayText(int delay) {
        return getDelayText(delay, true);
    }

    public static String getServoPosString(int servoPos) {
        return getServoPosString(servoPos, true);
    }

    public static String getDelayText(int delay, boolean adjustSize) {
        if (adjustSize) {
            return ApplicationData.doubleToString(delay / dividerForAlgorithmDelay, 2, 6);
        } else {
            return ApplicationData.doubleToString(delay / dividerForAlgorithmDelay, 2);
        }
    }

    public static String getServoPosString(int servoPos, boolean adjustSize) {
        if (adjustSize) {
            return ApplicationData.doubleToString(servoPos / 1.0, 0, 3);
        } else {
            return ApplicationData.doubleToString(servoPos / 1.0, 0);
        }
    }

    public static String doubleToString(double value, int precision, int numOfCharactersExpected) {
        String retStr = doubleToString(value, precision);
        if (numOfCharactersExpected < 0)
            return retStr;
        return addZeros(retStr, numOfCharactersExpected);
    }

    public static String doubleToString(double value, int precision) {
        String formatString = "%." + precision + "f";
        String retStr = String.format(formatString, value);
        retStr = retStr.replace(',', '.');
        return retStr;
    }


    public static String readTextFileFromRawResource(int resId, Context application) {
        InputStream inputStream = application.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return "";
        }
        return text.toString();
    }

    public String getJsonData() {

        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    private ApplicationData() {
        super();
        if (instance == null)
            instance = this;
    }

    public static synchronized ApplicationData getInstance() {
        if (instance == null) {
            instance = new ApplicationData();
        }
        return instance;
    }
}
