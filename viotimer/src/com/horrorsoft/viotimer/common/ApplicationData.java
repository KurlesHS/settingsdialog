package com.horrorsoft.viotimer.common;

import android.app.Application;
import android.content.Context;

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
public class ApplicationData extends Application {
    private static ApplicationData instance;
    private String jsonData;
    private byte[] binaryData;

    public static String readTextFileFromRawResource(int resId, Context application) {
        InputStream inputStream = application.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while (( line = bufferedReader.readLine()) != null) {
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

    private ApplicationData(){}

    public static synchronized ApplicationData getInstance() {
        if (instance == null) {
            instance = new ApplicationData();
        }
        return instance;
    }
}
