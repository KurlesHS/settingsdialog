package com.horrorsoft.viotimer.bluetooth;


import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.BackgroundExecutor;

/**
 * Created by Admin on 03.04.2014.
 */
@EBean
public class TimerProtocol implements BlueToothDataListener, BlueToothStatusListener {

    @Bean
    protected ApplicationData mCommonData;

    private BlueToothWriter mBlueToothWriter = null;
    private boolean mCurrentBlueToothStatus;
    private int mCurrentState;
    private int mCurrentStateForWriteSetting;
    private int mCurrentWriteSettingOffset;
    private byte[] mBufferForSettings = null;
    private byte[] mBufferForIncomingData = new byte[0x100];
    private byte[] mBufferForSendingData = new byte[0x22];
    private int mLengthBufferForIncomingData = 0;
    private int mCh1;
    private int mCh2;

    private static final String DELAY_ID_FOR_WAIT_RESPONSE = "delay_id";

    private static final int COMMON_TIMER_STATE = 0x00;
    private static final int WRITING_SETTINGS_STATE = 0x01;
    private static final int READING_SETTINGS_STATE = 0x02;

    private static final int WAIT_READY_STATE = 0x00;
    private static final int WAIT_GOOD_STATE = 0x01;


    public static final int WRITE_RESULT_OK = 0x00;
    public static final int WRITE_RESULT_FAIL = 0x01;

    WriteSettingInTimerResultListener mWriteSettingInTimerResultListener = null;

    public TimerProtocol() {
        mCurrentBlueToothStatus = false;
        mCurrentState = COMMON_TIMER_STATE;
    }

    public void setCommonData(ApplicationData commonData) {
        mCommonData = commonData;
    }

    public void setBlueToothWriter(BlueToothWriter blueToothWriter) {
        mBlueToothWriter = blueToothWriter;
    }

    public void setWriteSettingInTimerResultListener(WriteSettingInTimerResultListener listener) {
        mWriteSettingInTimerResultListener = listener;
    }

    public void setServoState(int servoNumber, int servoState) {

    }

    public void writeSettingsIntoTimer() {
        if (!mCurrentBlueToothStatus || mCurrentState != COMMON_TIMER_STATE) {
            sendWriteResultWithDelay(WRITE_RESULT_FAIL);
            return;
        }
        byte[] command = getCommandForStartFlashing();
        mCurrentState = WRITING_SETTINGS_STATE;
        mCurrentStateForWriteSetting = WAIT_READY_STATE;
        mBlueToothWriter.write(command);
        mLengthBufferForIncomingData = 0x00;
        startDelayTimer();
    }


    @Background(delay = 2000, id = DELAY_ID_FOR_WAIT_RESPONSE)
    protected void startDelayTimer() {
        sendWriteResult(WRITE_RESULT_FAIL);
        mCurrentState = COMMON_TIMER_STATE;
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
            default:
                break;
        }


    }

    private void handleReadSettingsState() {

    }

    private void handleWriteSettingsState() {
        BackgroundExecutor.cancelAll(DELAY_ID_FOR_WAIT_RESPONSE, false);
        if (mLengthBufferForIncomingData < 5) {
            startDelayTimer();
            return;
        } else if (mLengthBufferForIncomingData > 0x80) {
            mCurrentState = COMMON_TIMER_STATE;
            sendWriteResult(WRITE_RESULT_FAIL);
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
                sendWriteResult(WRITE_RESULT_OK);
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

    private void appendCrc16ToSendingBuffer() {
        int dataLength = mBufferForSendingData.length - 2;
        short crc16 = calculateCrc16(mBufferForSendingData, dataLength);
        mBufferForSendingData[dataLength] = (byte) (crc16 & 0xff);
        mBufferForSendingData[dataLength + 1] = (byte) ((crc16 >> 8) & 0xff);
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
        byte[] command = new byte[0x11];
        clearByteArray(command, (byte) 0x00);
        command[0] = (byte) mCh1;
        command[1] = (byte) mCh2;
        command[2] = (byte) 0x83;
        command[0x10] = calculateCrc8(command, 0x10);
        return command;
    }

    private void clearByteArray(byte[] array, byte fillWith) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = fillWith;
        }
    }

    public byte calculateCrc8(byte[] array, int size) {
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
}

