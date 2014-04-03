package com.horrorsoft.viotimer.bluetooth;

/**
 * Created by Admin on 03.04.2014.
 */
public interface WriteSettingInTimerResultListener {
    void writeResult(int status);
    void writeProcess(int currentPos, int maxPos);
}
