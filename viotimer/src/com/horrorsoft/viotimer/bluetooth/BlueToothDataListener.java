package com.horrorsoft.viotimer.bluetooth;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 01.04.14
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public interface BlueToothDataListener {
    void dataFromBluetooth(byte buffer[]);
    String id();
}
