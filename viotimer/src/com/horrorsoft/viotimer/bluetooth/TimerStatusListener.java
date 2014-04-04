package com.horrorsoft.viotimer.bluetooth;

/**
 * Created by Admin on 03.04.2014.
 *
 */
public interface TimerStatusListener {
    void status(int activity, int currentSignal, int prevSignal, int voltage);
    String id();
}
