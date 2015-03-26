package com.horrorsoft.viotimer.bluetooth;

import java.io.Serializable;

/**
 * Created by Alexey on 26.03.2015.
 */
public interface ReadSettingFromTimerResultListener extends Serializable {
    void readResult(int status);
    void readProcess(int currentPos, int maxPos);
    String id();
}
