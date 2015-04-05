package com.horrorsoft.viotimer.bluetooth;


import android.util.Log;
import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.BackgroundExecutor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created by Admin on 03.04.2014.
 * Ух ты, сколько времени прошло :)
 */
@EBean
public class TimerProtocol implements BlueToothDataListener, BlueToothStatusListener {

    @Bean
    protected ApplicationData mCommonData;

    private BlueToothWriter mBlueToothWriter = null;
    private boolean mCurrentBlueToothStatus;
    private int mCurrentState;
    private int mCurrentStateForReadSetting;
    private int mCurrentStateForWriteSetting;
    private int mCurrentWriteSettingOffset;
    private int mCurrentReadPacketNum;
    private int mTotalReadPacketNum;
    private byte[] mBufferForSettings = null;
    private byte[] mBufferForFlightHistory = null;
    private byte[] mBufferForIncomingData = new byte[0x100];
    private byte[] mBufferForSendingData = new byte[0x22];
    private int mLengthBufferForIncomingData = 0;
    private int mCh1;
    private int mCh2;

    private static final int mFlightHistoryPacketTotalCount = 200;

    private static final String DELAY_ID_FOR_WAIT_RESPONSE = "delay_id";

    private static final int COMMON_TIMER_STATE = 0x00;
    private static final int WRITING_SETTINGS_STATE = 0x01;
    private static final int READING_SETTINGS_STATE = 0x02;
    private static final int READING_ALTIMETER_DATA = 0x03;
    private static final int READING_TELEMETRY_DATA = 0x04;
    private static final int FLASH_BLUETOOTH_SETTINGS = 0x05;

    private static final int WAIT_READY_STATE = 0x00;
    private static final int WAIT_GOOD_STATE = 0x01;
    private static final int WAIT_PACKET_STATE = 0x02;

    public static final int RESULT_OK = 0x00;
    public static final int RESULT_FAIL = 0x01;

    WriteSettingInTimerResultListener mWriteSettingInTimerResultListener = null;
    ReadSettingFromTimerResultListener mReadSettingFromTimerResultListener = null;
    ITimerCommandResultListener mReadFlightHistoryFromTimerResultListener = null;
    ITelemetryListener mTelemetryListener = null;
    IFlashBluetoothSettingsListener mFlashBluetoothSettingsListener = null;

    public TimerProtocol() {
        mCurrentBlueToothStatus = false;
        mCurrentState = COMMON_TIMER_STATE;
    }

    public void setCommonData(ApplicationData commonData) {
        mCommonData = commonData;
    }

    public void setFlashBluetoothSettingsListener(IFlashBluetoothSettingsListener listener) {
        mFlashBluetoothSettingsListener = listener;
    }

    public void setTelemetryListener(ITelemetryListener listener) {
        mTelemetryListener = listener;
    }

    public void setBlueToothWriter(BlueToothWriter blueToothWriter) {
        mBlueToothWriter = blueToothWriter;
    }

    public void setReadFlightHistoryFromTimerResultListener(ITimerCommandResultListener listener) {
        mReadFlightHistoryFromTimerResultListener = listener;
    }

    public void setWriteSettingInTimerResultListener(WriteSettingInTimerResultListener listener) {
        mWriteSettingInTimerResultListener = listener;
    }

    public void setReadSettingFromTimerResultListener(ReadSettingFromTimerResultListener listener) {
        mReadSettingFromTimerResultListener = listener;
    }

    @UiThread
    public void readFlightHistory() {
        if (!mCurrentBlueToothStatus || mCurrentState != COMMON_TIMER_STATE) {
            sendReadFlightHistoryResultWhitDelay(RESULT_FAIL);
            return;
        }
        mBufferForFlightHistory = new byte[0x20 * mFlightHistoryPacketTotalCount];
        byte[] command = getCommand((byte) 0x85);
        mCurrentState = READING_ALTIMETER_DATA;
        mCurrentReadPacketNum = 0x00;
        mBlueToothWriter.write(command);
        startDelayTimer();
    }

    @UiThread
    public void writeSettingsIntoTimer() {
        if (!mCurrentBlueToothStatus || mCurrentState != COMMON_TIMER_STATE) {
            sendWriteResultWithDelay(RESULT_FAIL);
            return;
        }
        byte[] command = getCommandForStartFlashing();
        mCurrentState = WRITING_SETTINGS_STATE;
        mCurrentStateForWriteSetting = WAIT_READY_STATE;
        mBlueToothWriter.write(command);
        mLengthBufferForIncomingData = 0x00;
        startDelayTimer();
    }

    @UiThread
    public void readSettingsFromTimer() {
        if (!mCurrentBlueToothStatus || mCurrentState != COMMON_TIMER_STATE) {
            sendReadResultWithDelay(RESULT_FAIL);
            return;
        }
        byte[] readSettingCommand = getCommand((byte) 0x84);
        mCurrentState = READING_SETTINGS_STATE;
        mCurrentReadPacketNum = 0;
        mBufferForSettings = new byte[0xffff];
        mCurrentStateForReadSetting = WAIT_PACKET_STATE;
        mBlueToothWriter.write(readSettingCommand);
        mLengthBufferForIncomingData = 0x00;
        startDelayTimer();
    }

    private byte[] resizeArray(byte[] array, int len, byte fillChar) {
        if (array.length == len) {
            return array;
        }
        byte[] tmp = new byte[len];
        Arrays.fill(tmp, fillChar);
        System.arraycopy(array, 0, tmp, 0, Math.min(len, array.length));
        return tmp;
    }

    @UiThread
    public void flashNewBluetoothSettings(String newPin, String newBluetoothName) {

        if (!mCurrentBlueToothStatus) {
            return;
        }
        byte[] pin = newPin.getBytes();
        byte[] name = newBluetoothName.getBytes();

        pin = resizeArray(pin, 0x04, (byte) 0x30);
        name = resizeArray(name, 0x10, (byte) 0x00);

        byte[] lastTwelveBytes = new byte[]{0x67, 0x45, 0x23, 0x01, (byte) 0xef, (byte) 0xcd,
                (byte) 0xab, (byte) 0x89, 0x00, 0x00, 0x00, 0x00};

        byte[] cmd = new byte[0x22];
        System.arraycopy(name, 0, cmd, 0, 0x10);
        System.arraycopy(pin, 0, cmd, 0x10, 0x04);
        System.arraycopy(lastTwelveBytes, 0, cmd, 0x14, 0x0c);
        short crc16 = crcForFirmware(cmd, 0x20);
        cmd[0x21] = (byte) (crc16 & 0xff);
        cmd[0x20] = (byte) (((crc16 & 0xffff) >> 8) & 0xff);
        mCurrentState = FLASH_BLUETOOTH_SETTINGS;

        mBlueToothWriter.write(cmd);
        startDelayTimer();
    }

    @UiThread(delay = 300)
    protected void broadcastErrorTelemetryWithDelay() {
        broadcastErrorTelemetry();
    }

    @UiThread
    public void getTelemetry() {
        if (!mCurrentBlueToothStatus || mCurrentState != COMMON_TIMER_STATE) {
            broadcastErrorTelemetryWithDelay();
            return;
        }
        byte[] telemetryCommand = getCommand((byte) 0xff);
        mCurrentState = READING_TELEMETRY_DATA;
        mBlueToothWriter.write(telemetryCommand);
        mLengthBufferForIncomingData = 0x00;
        startDelayTimer();

    }

    @Background(delay = 2000, id = DELAY_ID_FOR_WAIT_RESPONSE)
    protected void startDelayTimer() {
        if (mCurrentState == WRITING_SETTINGS_STATE) {
            sendWriteResult(RESULT_FAIL);
        } else if (mCurrentState == READING_SETTINGS_STATE) {
            sendReadResult(RESULT_FAIL);
        } else if (mCurrentState == READING_ALTIMETER_DATA) {
            sendReadFlightHistoryResult(RESULT_FAIL);
        } else if (mCurrentState == READING_TELEMETRY_DATA) {
            broadcastErrorTelemetry();
        } else if (mCurrentState == FLASH_BLUETOOTH_SETTINGS) {
            broadcastFlashBluetoothResult(false);
        }
        mCurrentState = COMMON_TIMER_STATE;
    }

    @UiThread
    protected void broadcastFlashBluetoothResult(boolean result) {
        if (mFlashBluetoothSettingsListener != null) {
            mFlashBluetoothSettingsListener.flashBluetoothSettingResult(result);
        }
    }

    private void broadcastErrorTelemetry() {
        ITelemetryListener.TelemetryData telemetryData = new ITelemetryListener.TelemetryData();
        telemetryData.hasError = true;
        broadcastTelemetry(telemetryData);
    }

    private void broadcastTelemetry(ITelemetryListener.TelemetryData telemetryData) {
        if (mTelemetryListener != null) {
            mTelemetryListener.result(telemetryData);
        }
    }

    @UiThread(delay = 10)
    protected void sendReadResultWithDelay(int status) {
        sendReadResult(status);
    }

    @UiThread
    protected void sendReadResult(int status) {
        mCurrentState = COMMON_TIMER_STATE;
        mBufferForSettings = null;
        if (mReadSettingFromTimerResultListener != null) {
            mReadSettingFromTimerResultListener.readResult(status);
        }
    }

    @UiThread(delay = 10)
    protected void sendReadFlightHistoryResultWhitDelay(int status) {
        sendReadFlightHistoryResult(status);
    }

    @UiThread
    protected void sendReadFlightHistoryResult(int status) {
        mCurrentState = COMMON_TIMER_STATE;
        mBufferForFlightHistory = null;
        if (mReadFlightHistoryFromTimerResultListener != null) {
            mReadFlightHistoryFromTimerResultListener.result(status);
        }
    }

    @UiThread(delay = 10)
    protected void sendWriteResultWithDelay(int status) {
        sendWriteResult(status);
    }

    @UiThread
    protected void sendWriteResult(int status) {
        mCurrentState = COMMON_TIMER_STATE;
        mBufferForSettings = null;
        if (mWriteSettingInTimerResultListener == null) {
            return;
        }
        mWriteSettingInTimerResultListener.writeResult(status);
    }

    public void setChBytes(int ch1, int ch2) {
        mCh1 = ch1;
        mCh2 = ch2;
    }

    @Override
    public void dataFromBluetooth(byte[] buffer) {
        Log.d(ApplicationData.LOG_TAG, "from bluetooth: " + String.valueOf(buffer.length));
        int newBufferLen = mLengthBufferForIncomingData + buffer.length;
        int maxBufferLength = mBufferForIncomingData.length;
        if (newBufferLen > maxBufferLength)
            newBufferLen = maxBufferLength;

        int bytesToRead = newBufferLen - mLengthBufferForIncomingData;
        if (bytesToRead <= 0)
            return;
        System.arraycopy(buffer, 0, mBufferForIncomingData, mLengthBufferForIncomingData, bytesToRead);
        mLengthBufferForIncomingData = newBufferLen;

        switch (mCurrentState) {
            case COMMON_TIMER_STATE:
                handleCommonTimerState();
                break;
            case WRITING_SETTINGS_STATE:
                handleWriteSettingsState();
                break;
            case READING_SETTINGS_STATE:
                handleReadSettingsState();
                break;
            case READING_ALTIMETER_DATA:
                handleReadAltimeterState();
                break;
            case READING_TELEMETRY_DATA:
                handleReadTelemetryData();
                break;
            case FLASH_BLUETOOTH_SETTINGS:
                handleFlashBluetoothSettingData();
            default:
                break;
        }
    }

    private void handleFlashBluetoothSettingData() {
        stopDelayTimer();
        if (mLengthBufferForIncomingData < 0x05) {
            startDelayTimer();
            return;
        }
        mCurrentState = COMMON_TIMER_STATE;
        if (mLengthBufferForIncomingData > 0x05) {
            broadcastFlashBluetoothResult(false);
        } else {
            clearBufferForIncomingData();
            String response = new String(Arrays.copyOfRange(mBufferForIncomingData, 0x00, 0x05));
            broadcastFlashBluetoothResult(response.equals("GOOD "));
        }
    }

    private void handleReadTelemetryData() {
        stopDelayTimer();
        if (mLengthBufferForIncomingData < 0x11) {
            startDelayTimer();
            return;
        }
        mCurrentState = COMMON_TIMER_STATE;
        int dataLen = mLengthBufferForIncomingData;
        clearBufferForIncomingData();
        ITelemetryListener.TelemetryData telemetryData = new ITelemetryListener.TelemetryData();
        telemetryData.hasError = true;
        byte[] intBuffer = new byte[0x04];
        if (dataLen == 0x11) {
            byte crc8 = calculateCrc8(mBufferForIncomingData, 0x10);
            if (crc8 == mBufferForIncomingData[0x10]) {
                System.arraycopy(mBufferForIncomingData, 0x02, intBuffer, 0, 0x04);
                int firstInt = getIntFromBytes(intBuffer);
                System.arraycopy(mBufferForIncomingData, 0x06, intBuffer, 0, 0x04);
                int secondInt = getIntFromBytes(intBuffer);
                int t = ((firstInt) >>> 2) & 0x3ff;
                int v = ((firstInt) >>> 12) & 0x3ff;
                int h = ((firstInt) >>> 22) & 0x3ff;
                telemetryData.hasError = false;
                telemetryData.ch1 = mBufferForIncomingData[0x00];
                telemetryData.ch2 = mBufferForIncomingData[0x01];
                telemetryData.reservedA = mBufferForIncomingData[0x0a];
                telemetryData.act = mBufferForIncomingData[0x0c];
                telemetryData.reservedD = mBufferForIncomingData[0x0d];
                telemetryData.rssi = mBufferForIncomingData[0x0e];
                telemetryData.pwr = ((float)(mBufferForIncomingData[0x0f] & 0xff) + 100f) * 0.01f;
                telemetryData.rdtFlag = (firstInt & 0x01) != 0;
                telemetryData.dtFlag = (firstInt & 0x02) != 0;
                telemetryData.temperature = (float)t * 0.1f - 20f;
                telemetryData.height = h - 0x40;
                telemetryData.speed = (float)v * 0.01f;
                telemetryData.voltage = (float)((secondInt >>> 22) & 0x03ff) * 0.01f;
                telemetryData.timeToDt = (secondInt >>> 12) & 0x03ff;
                telemetryData.prediction = (secondInt >>> 2) & 0x03ff;
                telemetryData.blinkerOnFlag = (secondInt & 0x0002) != 0;
                telemetryData.servoOnFlag = (secondInt & 0x0001) != 0;
            }
        }
        broadcastTelemetry(telemetryData);
    }

    private void stopDelayTimer() {
        BackgroundExecutor.cancelAll(DELAY_ID_FOR_WAIT_RESPONSE, false);
    }

    private void handleReadAltimeterState() {
        stopDelayTimer();
        if (mLengthBufferForIncomingData < 0x22) {
            startDelayTimer();
            return;
        } else if (mLengthBufferForIncomingData != 0x22) {
            mCurrentState = COMMON_TIMER_STATE;
            sendReadFlightHistoryResult(RESULT_FAIL);
            return;
        }
        mLengthBufferForIncomingData = 0x00;
        if (checkPacketCrc(mBufferForIncomingData, 0x22)) {
            System.arraycopy(mBufferForIncomingData, 0, mBufferForFlightHistory, mCurrentReadPacketNum * 0x20, 0x20);

            ++mCurrentReadPacketNum;
            if (mReadFlightHistoryFromTimerResultListener != null) {
                mReadFlightHistoryFromTimerResultListener.process(mCurrentReadPacketNum, mFlightHistoryPacketTotalCount);
            }
            mBlueToothWriter.write("GOOD ".getBytes());
            if (mCurrentReadPacketNum == mFlightHistoryPacketTotalCount) {
                mCommonData.setFlightHistoryData(mBufferForFlightHistory);
                sendReadFlightHistoryResult(RESULT_OK);
            } else {
                startDelayTimer();
            }
        } else {
            mBlueToothWriter.write("BAD  ".getBytes());
            sendReadFlightHistoryResult(RESULT_FAIL);
        }
    }

    private void handleReadSettingsState() {
        stopDelayTimer();
        if (mLengthBufferForIncomingData < 0x22) {
            startDelayTimer();
            return;
        } else if (mLengthBufferForIncomingData != 0x22) {
            mCurrentState = COMMON_TIMER_STATE;
            sendWriteResult(RESULT_FAIL);
            return;
        }
        mLengthBufferForIncomingData = 0x00;
        byte[] packet = new byte[0x22];
        System.arraycopy(mBufferForIncomingData, 0, packet, 0, 0x22);
        switch (mCurrentStateForReadSetting) {
            case WAIT_PACKET_STATE: {
                handleWaitPacketState(packet);
            }
            break;
            default: {
                mBlueToothWriter.write("BAD  ".getBytes());
                sendReadResult(RESULT_FAIL);
            }
            break;
        }
    }

    private void sendReadResultFail() {
        mCurrentState = COMMON_TIMER_STATE;
        sendReadResult(RESULT_FAIL);
    }

    private void handleWaitPacketState(byte[] packet) {
        if (!checkPacketCrc(packet)) {
            sendReadResultFail();
            return;
        }
        System.arraycopy(packet, 0, mBufferForSettings, mCurrentReadPacketNum * 0x20, 0x20);
        ++mCurrentReadPacketNum;
        mBlueToothWriter.write("GOOD ".getBytes());
        if (mReadSettingFromTimerResultListener != null) {
            mReadSettingFromTimerResultListener.readProcess(mCurrentReadPacketNum, mTotalReadPacketNum);
        }
        if (mCurrentReadPacketNum == 0x01) {
            mTotalReadPacketNum = 0x101;
        } else if (mCurrentReadPacketNum > 2) {
            if (mCurrentReadPacketNum == mTotalReadPacketNum) {
                readSettingDone();
                Log.d(ApplicationData.LOG_TAG, "total count: " + String.valueOf(mTotalReadPacketNum));
                return;
            }
        } else if (mCurrentReadPacketNum == 2) {
            // во втором пакете первые 2 байта - длинна xml части
            int xmlLen = getIntFromBytes(Arrays.copyOfRange(packet, 0, 2));
            xmlLen += 2;
            int xmlPacketCount = (xmlLen / 0x20) & 0xff;
            // корректировка длинны xml части в пакетах, если нужно
            if (xmlLen % 0x20 != 0) {
                ++xmlPacketCount;
            }
            ++xmlPacketCount;
            mTotalReadPacketNum = xmlPacketCount + 0x0100; // кол-во полезной инфы - 256 пакетов
        }
        clearBufferForIncomingData();

        startDelayTimer();
    }

    private void readSettingDone() {
        mCurrentState = COMMON_TIMER_STATE;
        if (mCommonData.loadConfigFromByteArray(mBufferForSettings)) {
            sendReadResult(RESULT_OK);
        } else {
            sendReadResult(RESULT_FAIL);
        }
    }

    private void handleWriteSettingsState() {
        BackgroundExecutor.cancelAll(DELAY_ID_FOR_WAIT_RESPONSE, false);
        if (mLengthBufferForIncomingData < 5) {
            startDelayTimer();
            return;
        } else if (mLengthBufferForIncomingData > 0x80) {
            mCurrentState = COMMON_TIMER_STATE;
            sendWriteResult(RESULT_FAIL);
            return;
        }
        byte[] response = new byte[5];
        System.arraycopy(mBufferForIncomingData, mLengthBufferForIncomingData - 5, response, 0, 5);
        String strResponse = new String(response);

        switch (mCurrentStateForWriteSetting) {
            case WAIT_READY_STATE:
                handleWriteWaitReadyState(strResponse);
                break;
            case WAIT_GOOD_STATE:
                handleWriteWaitGoodState(strResponse);
                break;
            default:
                break;
        }
    }

    private void clearBufferForIncomingData() {
        mLengthBufferForIncomingData = 0x00;
    }

    private void handleWriteWaitGoodState(String strResponse) {
        if (strResponse.equals("GOOD ")) {
            clearByteArray(mBufferForSendingData, (byte) 0x00);
            int remainBytes = mBufferForSettings.length - mCurrentWriteSettingOffset;
            int numOfPackets = mBufferForSettings.length / 0x20;
            if (remainBytes <= 0) {
                mWriteSettingInTimerResultListener.writeProcess(numOfPackets, numOfPackets);
                sendWriteResult(RESULT_OK);
                return;
            }
            int currentPacket = mCurrentWriteSettingOffset / 0x20;
            mWriteSettingInTimerResultListener.writeProcess(currentPacket, numOfPackets);
            if (remainBytes > 0x20)
                remainBytes = 0x20;
            System.arraycopy(mBufferForSettings, mCurrentWriteSettingOffset, mBufferForSendingData, 0, remainBytes);
            appendCrc16ToSendingBuffer();
            mBlueToothWriter.write(mBufferForSendingData);
            mCurrentWriteSettingOffset += remainBytes;
            clearBufferForIncomingData();
        }
        startDelayTimer();
    }

    private void handleWriteWaitReadyState(String strResponse) {
        if (strResponse.equals("READY")) {
            mCurrentWriteSettingOffset = 0x00;
            mBufferForSettings = mCommonData.getBinaryDataToUploadToDevice();
            int numOfPackets = mBufferForSettings.length / 0x20;
            mWriteSettingInTimerResultListener.writeProcess(0, numOfPackets);
            clearByteArray(mBufferForSendingData, (byte) 0x00);
            System.arraycopy(mBufferForSettings, mCurrentWriteSettingOffset, mBufferForSendingData, 0, 0x20);
            appendCrc16ToSendingBuffer();
            mBlueToothWriter.write(mBufferForSendingData);
            mCurrentWriteSettingOffset += 0x20;
            mCurrentStateForWriteSetting = WAIT_GOOD_STATE;
            clearBufferForIncomingData();
        }
        startDelayTimer();
    }

    private void handleCommonTimerState() {

    }

    // счтает crc16 пакета (кроме последних 2 байт) и помещает это crc в последние два байта пакета
    private void calculateCrc16AndPutItInLastBytes(byte[] packet) {
        int dataLength = packet.length - 2;
        short crc16 = calculateCrc16(packet, dataLength);
        packet[dataLength] = (byte) (crc16 & 0xff);
        packet[dataLength + 1] = (byte) ((crc16 >> 8) & 0xff);
    }

    private void appendCrc16ToSendingBuffer() {
        calculateCrc16AndPutItInLastBytes(mBufferForSendingData);
    }

    @Override
    public void bluetoothStatusChanged(boolean status) {
        mCurrentBlueToothStatus = status;
    }

    @Override
    public String id() {
        return "66319302-f201-4ff8-bb1b-949907a393ee";
    }

    private byte[] getCommandForStartFlashing() {
        return getCommand((byte) 0x83);
    }

    private byte[] getCommand(byte cmd) {
        byte[] command = new byte[0x11];
        clearByteArray(command, (byte) 0x00);
        command[0] = (byte) mCh1;
        command[1] = (byte) mCh2;
        command[2] = cmd;
        command[0x10] = calculateCrc8(command, 0x10);
        return command;
    }

    private void clearByteArray(byte[] array, byte fillWith) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = fillWith;
        }
    }

    public static byte calculateCrc8(byte[] array, int size) {
        if (size < 0) {
            size = array.length;
        }
        byte crc = (byte) 0xFF;
        for (int x = 0; x < size; ++x) {
            crc ^= array[x];
            for (int i = 0; i < 8; i++)
                crc = (crc & (byte) 0x80) != (byte) 0x00 ? (byte) ((crc << 1) ^ 0x31) : (byte) (crc << 1);
        }
        return crc;
    }

    public static short calculateCrc16(byte[] array, int size) {
        if (size < 0) {
            size = array.length;
        }
        short crc = (short) 0xFFFF;
        for (int x = 0; x < size; ++x) {
            crc ^= (short) (array[x] << 8);
            for (int i = 0; i < 8; i++)
                crc = (crc & (short) 0x8000) != (short) 0x00 ? (short) ((crc << 1) ^ 0x1021) : (short) (crc << 1);
        }
        return crc;
    }

    private static boolean checkPacketCrc(byte[] packet, int len) {
        int dataLen = len - 2;
        if (len < 0) {
            return false;
        }
        short realCrc = calculateCrc16(packet, dataLen);
        short expectedCrc = getShortFromBytes(packet[dataLen], packet[dataLen + 1]);
        return realCrc == expectedCrc;
    }

    private static boolean checkPacketCrc(byte[] packet) {
        return checkPacketCrc(packet, packet.length);
    }

    private static short getShortFromBytes(byte low, byte high) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(low);
        bb.put(high);
        return bb.getShort(0);
    }

    private static int getIntFromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        if (bytes.length > 4) {
            bb.put(bytes, 0, 4);
        } else {
            bb.put(bytes);
        }
        return bb.getInt(0);
    }

    public static short crcForFirmware(byte[] array, int length) {
        long sum = 0;
        int i = 0;
        if (length == 0) length = array.length;
        while (i < length) {
            sum += ((long) (array[i++] % 0xff) << 8) + (array[i++] % 0xff);
        }
        sum = (sum >> 16) + (sum & 0xffff);
        sum += (sum >> 16);
        return (short) (~(sum & 0xffff));
    }
}

