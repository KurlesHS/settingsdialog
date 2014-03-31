package com.horrorsoft.viotimer.bluetooth;

public interface BlueToothListener {
	void bluetoothStatusChanged(boolean status);
	void dataFromBluetooth(byte buffer[]);
}
