package com.horrorsoft.abctimer.bluetooth;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 01.04.14
 * Time: 16:03
 */
public interface BlueToothStatusListener {
    void bluetoothStatusChanged(boolean status);
    String id();
}
