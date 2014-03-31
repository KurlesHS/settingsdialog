package com.horrorsoft.viotimer.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.horrorsoft.viotimer.common.ApplicationData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 31.03.14
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
public class BlueToothConnectionThread extends Thread {

    // Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use);
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String mMacAddress;
    private Handler mHandler = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;
    OutputStream outputStream = null;
    InputStream inputStream = null;

    public BlueToothConnectionThread(String macAddress, Handler handler, BluetoothAdapter bluetoothAdapter) {
        mMacAddress = macAddress;
        mHandler = handler;
        mBluetoothAdapter = bluetoothAdapter;
    }

    public void writeIntoBlueTooth(byte[] array) {
        if (outputStream != null) {
            try {
                outputStream.write(array);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            btSocket = null;
            outputStream = null;
            inputStream = null;
            Message msg = Message.obtain();
            if (msg != null) {
                msg.what = ApplicationData.CONNECTION_FAILED;
                mHandler.sendMessage(msg);
            }


        }
    }

    @Override
    public void run() {
        boolean mConnectionStatus = true;
        try {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mMacAddress);
            try {
                btSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                mConnectionStatus = false;
            }
        }catch (IllegalArgumentException e) {
            mConnectionStatus = false;
        }
        mBluetoothAdapter.cancelDiscovery();

        try {
            btSocket.connect();
        } catch (IOException e1) {
            mConnectionStatus = false;
            try {
                btSocket.close();
            } catch (IOException e2) {
                // something Wrong :(
            }
        }

        try {
            outputStream = btSocket.getOutputStream();
            inputStream = btSocket.getInputStream();
        } catch (IOException e2) {
            mConnectionStatus = false;
        }
        Message msg = Message.obtain();
        if (msg != null) {
            if (mConnectionStatus) {
                msg.what = ApplicationData.CONNECTION_ESTABLISHED;

            } else {
                msg.what = ApplicationData.CONNECTION_FAILED;
            }

            Log.d(ApplicationData.LOG_TAG, mHandler == null ? "null 2" : "not null 2");
            mHandler.sendMessage(msg);
        }

        if (mConnectionStatus) {
            byte buffer[] = new byte[1024];
            buffer[0] = 56;
            buffer[1] = 57;
            buffer[2] = 58;
            buffer[3] = 59;
            try {
                outputStream.write(buffer, 0, 4);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while(true){
                try {
                    int len = inputStream.read(buffer);

                    if (len <= 0) {
                        break;
                    } else {
                        msg = Message.obtain();
                        if (msg != null) {
                            msg.what = ApplicationData.NEW_DATA_ARRIVED;
                            Bundle bundle = new Bundle();
                            byte outArray[] = new byte[len];
                            System.arraycopy(buffer, 0, outArray, 0, len);
                            bundle.putByteArray("data", outArray);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        mHandler.sendEmptyMessage(ApplicationData.EXIT_CONNECTION_THREAD);
    }
}
