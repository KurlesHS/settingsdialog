package com.horrorsoft.abctimer.bluetooth;

/**
 * Created by Alexey on 28.03.2015.
 */
public interface ITimerCommandResultListener {
    public static final int RESULT_OK = 0x00;
    public static final int RESULT_FAIL = 0x01;

    void result(int status);
    void process(int currentPos, int maxPos);
    String id();
}
