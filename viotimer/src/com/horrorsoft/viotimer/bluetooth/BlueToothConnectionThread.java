package com.horrorsoft.viotimer.bluetooth;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 31.03.14
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
public class BlueToothConnectionThread extends Thread {
    private String mMacAddress;

    public BlueToothConnectionThread(String macAddress) {
        mMacAddress = macAddress;
    }

    @Override
    public void run() {

    }
}
