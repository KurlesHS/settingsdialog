package com.horrorsoft.abctimer.bluetooth;

import java.io.Serializable;

/**
 * Created by Admin on 03.04.2014.
 */
public interface WriteSettingInTimerResultListener extends Serializable {
    void writeResult(int status);
    void writeProcess(int currentPos, int maxPos);
    String id();
}
