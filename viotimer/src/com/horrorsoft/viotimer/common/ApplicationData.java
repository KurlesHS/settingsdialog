package com.horrorsoft.viotimer.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.horrorsoft.viotimer.R;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import com.horrorsoft.viotimer.bluetooth.*;
import com.horrorsoft.viotimer.data.AlgorithmData;
import com.horrorsoft.viotimer.data.ICommonData;
import com.horrorsoft.viotimer.json.JsonSetting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 22:21
 */
@EBean(scope = EBean.Scope.Singleton)
public class ApplicationData {



    @RootContext
    protected Context context;

    public static final String LOG_TAG = "com.horrorsoft.viotimer";
    private String jsonData;
    private String firmwareId = "";
    private byte[] binaryData;
    private int firstFreeAddress;
    private AlgorithmData algorithmData;
    List<ICommonData> globalSettingData;
    private static float dividerForAlgorithmDelay;
    private static int globalMaxDelay;
    private static byte timer_ch1;
    private static byte timer_ch2;
    private ProgressDialog myProgressDialog;


    //  bluetooth stuff
    private BluetoothAdapter mBluetoothAdapter = null;
    private Handler mHandler = null;
    private BlueToothConnectionThread mConnectThread = null;
    private boolean mConnectionStatus = false;
    private ArrayList<BlueToothStatusListener> mBlueToothStatusListeners = new ArrayList<BlueToothStatusListener>();
    private ArrayList<BlueToothDataListener> mBlueToothDataListeners = new ArrayList<BlueToothDataListener>();
    private ArrayList<TimerStatusListener> mTimerStatusListeners = new ArrayList<TimerStatusListener>();
    private ArrayList<WriteSettingInTimerResultListener> mWriteSettingInTimerResultListeners = new ArrayList<WriteSettingInTimerResultListener>();

    @Bean
    protected TimerProtocol mTimerProtocol;

    public static final int NEW_DATA_ARRIVED = 0x03;
    public static final int CONNECTION_ESTABLISHED = 0x04;
    public static final int CONNECTION_FAILED = 0x05;
    public static final int EXIT_CONNECTION_THREAD = 0x06;

    public void tryToFlashSettings() {

    }

    public void addWriteSettingResultListener(WriteSettingInTimerResultListener listener) {
        if (!mWriteSettingInTimerResultListeners.contains(listener)) {
            for (WriteSettingInTimerResultListener l: mWriteSettingInTimerResultListeners) {
                if (l.id().equals(listener.id())) {
                    removeWriteSettingResultListener(l);
                    break;
                }
            }
            mWriteSettingInTimerResultListeners.add(listener);
        }
    }

    public void removeWriteSettingResultListener(WriteSettingInTimerResultListener listener) {
        mWriteSettingInTimerResultListeners.remove(listener);
    }

    public void writeSettingsIntoTimer() {
        mTimerProtocol.writeSettingsIntoTimer();
    }

    public void addTimerStatusListener(TimerStatusListener listener) {
        if (!mTimerStatusListeners.contains(listener)) {
            for (TimerStatusListener l: mTimerStatusListeners) {
                if (l.id().equals(listener.id())) {
                    removeTimerStatusListener(l);
                    break;
                }
            }
            mTimerStatusListeners.add(listener);
        }
    }

    public void removeTimerStatusListener(TimerStatusListener listener) {
        mTimerStatusListeners.remove(listener);
    }

    public void addBlueToothStatusListener(BlueToothStatusListener listener) {
        if (!mBlueToothStatusListeners.contains(listener)) {
            for (BlueToothStatusListener l: mBlueToothStatusListeners) {
                if (l.id().equals(listener.id())) {
                    removeBlueToothStatusListener(l);
                    break;
                }
            }
            mBlueToothStatusListeners.add(listener);
        }
    }

    public void removeBlueToothStatusListener(BlueToothStatusListener listener) {
        mBlueToothStatusListeners.remove(listener);
    }

    public void addBlueToothDataListener(BlueToothDataListener listener) {
        if (!mBlueToothDataListeners.contains(listener)) {
            for (BlueToothDataListener l: mBlueToothDataListeners) {
                if (l.id().equals(listener.id())) {
                    removeBlueToothDataListener(l);
                    break;
                }
            }
            mBlueToothDataListeners.add(listener);
        }
    }

    public void removeBlueToothDataListener(BlueToothDataListener listener) {
        mBlueToothDataListeners.remove(listener);
    }

    public void writeDataIntoBlueTooth(byte[] array) {
        if (mConnectThread != null) {
            mConnectThread.writeIntoBlueTooth(array);
        }
    }


    public boolean getBlueToothConnectionStatus() {
        return mConnectionStatus;
    }

    public boolean isBlueToothSupported() {
        return mBluetoothAdapter != null;
    }

    public boolean isBlueToothEnabled() {
        return isBlueToothSupported() && mBluetoothAdapter.isEnabled();
    }

    private void emitBlueToothStatusChanged(boolean status) {
        for (BlueToothStatusListener listener : mBlueToothStatusListeners) {
            listener.bluetoothStatusChanged(status);
        }
    }

    private void emitBlueToothIncomingData(byte buffer[]) {
        for (BlueToothDataListener listener : mBlueToothDataListeners) {
            listener.dataFromBluetooth(buffer);
        }
    }

    public List<ICommonData> getGlobalSettingData() {
        return globalSettingData;
    }

    public void setGlobalSettingData(List<ICommonData> globalSettingData) {
        this.globalSettingData = globalSettingData;
    }

    public int getFirstFreeAddress() {
        return firstFreeAddress;
    }

    public void setFirstFreeAddress(int firstFreeAddress) {
        this.firstFreeAddress = firstFreeAddress;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean loadConfigFromFile(String fileName) {
        boolean rawJson = fileName.endsWith(".vts_json");
        boolean success = true;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(fileName));
            String jsonData;
            byte[] read;
            if (rawJson) {
                int size = inputStream.available();
                read = new byte[size];
                inputStream.read(read, 0, size);
                jsonData = new String(read);
            } else {
                read = new byte[0x20];
                // читаем название прошивки
                inputStream.read(read, 0, 0x20);
                firmwareId = new String(read);
                inputStream.read(read, 0, 2);
                int xmlLength = byteToInt(read[0]) + byteToInt(read[1]) * 0x100;
                read = new byte[xmlLength];
                inputStream.read(read, 0, xmlLength);
                jsonData = new String(read);
            }

            initBinaryData();
            if (!rawJson) {
                inputStream.read(getBinaryData());
            }
            AlgorithmData algorithmDataByJson = JsonSetting.createAlgorithmDataByJson(jsonData);
            List<ICommonData> listOfDataByJson = JsonSetting.createListOfDataByJson(jsonData);
            if (algorithmDataByJson.getAlgorithmCount() > 0) {
                setJsonData(jsonData);
                setAlgorithmData(algorithmDataByJson);
                setGlobalSettingData(listOfDataByJson);
                // если читаем чистый json, то не надо из бинарных данных ничего доставать
                if (!rawJson) {
                    for (ICommonData commonData : getGlobalSettingData()) {
                        byte[] data = getByteArrayFromBinaryData(commonData.getPointer(), commonData.getSize());
                        if (data != null) {
                            commonData.setCurrentValueByBinaryData(data);
                        }
                    }
                    algorithmData.deserializeFromBinaryDat(new AlgorithmData.Deserealize() {
                        @Override
                        public byte[] getAlgorithmByteArrayFromPointer(int pointer) {
                            byte[] retVal = null;
                            pointer *= 2;
                            if (getBinaryData().length > pointer + 1) {
                                int realAdr = getBinaryData()[pointer] + getBinaryData()[pointer + 1] * 0x100;
                                int len = 0;
                                while (getBinaryData().length > realAdr + len && getBinaryData()[realAdr + len] != 0) {
                                    len += 4;
                                }
                                if (getBinaryData().length < realAdr + len) {
                                    len -= 4;
                                }
                                if (len > 0) {
                                    retVal = new byte[len];
                                    System.arraycopy(getBinaryData(), realAdr, retVal, 0, len);
                                }
                            }
                            return retVal;
                        }
                    });
                }
            } else {
                success = false;
            }
            algorithmData.recalculatePositions();
        } catch (Exception e) {
            success = false;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                // фигня какая-то
            }
        }
        freeBinaryData();
        return success;
    }

    public boolean saveConfigToFile(String fileName) {
        prepareBinaryDataToSaveOrUpload();
        boolean success = true;
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            byte[] bToW = new byte[0x20];
            if (firmwareId.length() > 0) {
                byte[] fw = firmwareId.getBytes();
                for (int i = 0; i < fw.length && i < 0x20; ++i) {
                    bToW[i] = fw[i];
                }
            }
            // пишем номер (версию, идентификатор) прошивки
            outputStream.write(bToW);
            bToW = new byte[0x02];
            int lenJson = getJsonData().getBytes().length;
            bToW[0] = (byte) (lenJson % 0x100);
            bToW[1] = (byte) ((lenJson >> 8) % 0x100);
            outputStream.write(bToW);
            outputStream.write(getJsonData().getBytes());
            outputStream.write(getBinaryData(), 0, getBinaryData().length);
            outputStream.close();
        } catch (Exception e) {
            success = false;
        }
        freeBinaryData();
        return success;
    }

    public byte[] getBinaryDataToUploadToDevice() {
        prepareBinaryDataToSaveOrUpload();
        int lenJson = getJsonData().getBytes().length;
        byte[] bToW = new byte[0x20];
        if (firmwareId.length() > 0) {
            byte[] fw = firmwareId.getBytes();
            for (int i = 0; i < fw.length && i < 0x20; ++i) {
                bToW[i] = fw[i];
            }
        }
        int totalLength = 0x22 + lenJson + getBinaryData().length;
        byte[] value = new byte[totalLength];
        System.arraycopy(bToW, 0, value, 0, 0x20);
        value[0x20] = (byte) (lenJson % 0x100);
        value[0x21] = (byte) ((lenJson >> 8) % 0x100);
        System.arraycopy(getJsonData().getBytes(), 0, value, 0x22, lenJson);
        System.arraycopy(getBinaryData(), 0, value, 0x22 + lenJson, getBinaryData().length);
        return value;
    }

    static public int byteToInt(byte b) {
        return b & 0xff;
    }

    private void prepareBinaryDataToSaveOrUpload() {
        initBinaryData();
        for (ICommonData commonData : getGlobalSettingData()) {
            int pointer = commonData.getPointer();
            writeBytesToBinaryData(pointer, commonData.getBinaryData());
        }
        getAlgorithmData().serializeToByteArray(new AlgorithmData.Serialize() {
            @Override
            public void serializeToByteArray(int pointer, byte[] value) {
                writeBytesToBinaryData(pointer, value);
            }
        });
        int binaryDataSize = binaryData.length - 2;
        short binaryDataCrc = TimerProtocol.calculateCrc16(binaryData, binaryDataSize);
        binaryData[binaryDataSize] = (byte)(binaryDataCrc % 0xff);
        binaryData[binaryDataSize + 1] = (byte)((binaryDataCrc >> 8) % 0xff);
    }

    public ApplicationData() {

    }

    @AfterInject
    public void init() {
        initBlueTooth();
    }

    private void initBlueTooth() {
        timer_ch1 = 0x1f;
        timer_ch2 = 0x2d;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // настраиваем протокол, что бы у него была возможность достучаться до блюпупа
        //mTimerProtocol = new TimerProtocol();
        //mTimerProtocol.setCommonData(this);
        mTimerProtocol.setBlueToothWriter(new BlueToothWriter() {
            @Override
            public void write(byte[] array) {
                writeDataIntoBlueTooth(array);
            }
        });
        addBlueToothDataListener(mTimerProtocol);
        addBlueToothStatusListener(mTimerProtocol);
        mTimerProtocol.setChBytes(timer_ch1, timer_ch2);
        mTimerProtocol.setWriteSettingInTimerResultListener(new WriteSettingInTimerResultListener() {
            @Override
            public void writeResult(int status) {
                for (WriteSettingInTimerResultListener l : mWriteSettingInTimerResultListeners)
                    l.writeResult(status);
            }

            @Override
            public void writeProcess(int currentPos, int maxPos) {
                for (WriteSettingInTimerResultListener l : mWriteSettingInTimerResultListeners)
                    l.writeProcess(currentPos, maxPos);
            }

            @Override
            public String id() {
                return "";
            }
        });
        mHandler = new Handler(Looper.getMainLooper()) {

            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEW_DATA_ARRIVED:
                    {
                        Bundle data = msg.getData();
                        if(data != null) {
                            byte[] array = data.getByteArray("data");
                            emitBlueToothIncomingData(array);
                        }
                    }
                    break;
                    case EXIT_CONNECTION_THREAD:
                    {
                        if (context != null)
                            Toast.makeText(context, "exit from thread", Toast.LENGTH_SHORT).show();
                        mConnectionStatus = false;
                        mConnectThread = null;
                        emitBlueToothStatusChanged(false);

                    }
                    break;
                    case CONNECTION_ESTABLISHED:
                    {
                        closeProgressDialog();
                        mConnectionStatus = true;
                        emitBlueToothStatusChanged(true);
                    }
                    break;
                    case CONNECTION_FAILED:
                    {
                        closeProgressDialog();
                        mConnectionStatus = false;
                        mConnectThread = null;
                        emitBlueToothStatusChanged(false);

                    }
                    break;

                    default:
                        break;
                }
            }
        };
    }

    private void closeProgressDialog() {
        if (myProgressDialog != null) {
            myProgressDialog.dismiss();
            myProgressDialog = null;
        }
    }

    public void connect(String macAddress, Activity activity) {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled())
        myProgressDialog = ProgressDialog.show(activity, activity.getResources().getString(R.string.pleaseWait), activity.getResources().getString(R.string.makingConnectionString), true);
        mConnectThread = new BlueToothConnectionThread(macAddress, mHandler, mBluetoothAdapter);
        mConnectThread.start();
    }

    public void disconnect() {
        if (mConnectThread != null) {
            mConnectThread.disconnect();
            mConnectThread = null;
        }
    }


    private void initBinaryData() {
        setFirstFreeAddress(0x0100);
        setBinaryData(new byte[0x2000]);
        for (int i = 0; i < getBinaryData().length; ++i) {
            getBinaryData()[i] = 0x00;
        }
    }

    private void freeBinaryData() {
        setBinaryData(null);
    }


    public static void setDividerForAlgorithmDelay(float divider) {
        dividerForAlgorithmDelay = divider;
    }

    public static void setGlobalMaxDelay(int delay) {
        globalMaxDelay = delay;
    }

    public static int getGlobalMinDelay() {
        return 0;
    }

    public static int getGlobalMaxDelay() {
        return globalMaxDelay;
    }

    public AlgorithmData getAlgorithmData() {
        return algorithmData;
    }

    public void setAlgorithmData(AlgorithmData algorithmData) {
        this.algorithmData = algorithmData;
    }

    public static int parseAlgorithmDelay(String delay) throws NumberFormatException {
        float delayFloat = Float.parseFloat(delay);
        return (int) (delayFloat * dividerForAlgorithmDelay);
    }

    public static String addZeros(String res, int numOfCharactersExpected) {
        String retStr = res;
        int countOfZeroToAdd = numOfCharactersExpected - retStr.length();
        while (countOfZeroToAdd > 0) {
            retStr = "0" + retStr;
            --countOfZeroToAdd;
        }
        return retStr;
    }

    public static String getDelayText(int delay) {
        return getDelayText(delay, true);
    }

    public static String getServoPosString(int servoPos) {
        return getServoPosString(servoPos, true);
    }

    public static String getDelayText(int delay, boolean adjustSize) {
        if (adjustSize) {
            return ApplicationData.doubleToString(delay / dividerForAlgorithmDelay, 2, 6);
        } else {
            return ApplicationData.doubleToString(delay / dividerForAlgorithmDelay, 2);
        }
    }

    public static String getServoPosString(int servoPos, boolean adjustSize) {
        if (adjustSize) {
            return ApplicationData.doubleToString(servoPos / 1.0, 0, 3);
        } else {
            return ApplicationData.doubleToString(servoPos / 1.0, 0);
        }
    }

    public static String doubleToString(double value, int precision, int numOfCharactersExpected) {
        String retStr = doubleToString(value, precision);
        if (numOfCharactersExpected < 0)
            return retStr;
        return addZeros(retStr, numOfCharactersExpected);
    }

    public static String doubleToString(double value, int precision) {
        String formatString = "%." + precision + "f";
        String retStr = String.format(formatString, value);
        retStr = retStr.replace(',', '.');
        return retStr;
    }


    public static String readTextFileFromRawResource(int resId, Context application) {
        InputStream inputStream = application.getResources().openRawResource(resId);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputReader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return "";
        }
        return text.toString();
    }

    public String getJsonData() {

        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public boolean writeBytesToBinaryData(int pointer, byte[] value) {
        boolean success = false;
        pointer *= 2;
        if (pointer >= 0 && pointer < 0x100 && value.length > 0) {
            success = true;
            int realAdr = getBinaryData()[pointer] + getBinaryData()[pointer + 1] * 0x100;
            if (realAdr < 0x0100) {
                realAdr = getFirstFreeAddress();
                setFirstFreeAddress(realAdr + value.length);
                success = writeIntToByteArray(getBinaryData(), pointer, 2, realAdr);
            }
            if (success) {
                success = writeBytesToByteArray(getBinaryData(), realAdr, value);
            }
        }
        return success;
    }

    private boolean writeBytesToByteArray(byte[] byteArray, int offset, byte[] bytes) {
        boolean success = false;
        if (byteArray.length >= offset + bytes.length) {
            success = true;
            System.arraycopy(bytes, 0, byteArray, offset, bytes.length);
        }
        return success;
    }

    private boolean writeIntToByteArray(byte[] byteArray, int offset, int size, int value) {
        boolean success = false;
        if (byteArray.length >= offset + size) {
            success = true;
            for (int i = 0; i < size; ++i) {
                byteArray[offset + i] = (byte) ((value >> (i * 8)) % 0x100);
            }
        }
        return success;
    }

    private byte[] getByteArrayFromBinaryData(int pointer, int size) {
        byte[] retVal = null;
        pointer *= 2;
        if (getBinaryData().length > pointer + 1) {
            int realAdr = getBinaryData()[pointer] + getBinaryData()[pointer + 1] * 0x100;
            if (getBinaryData().length >= realAdr + size) {
                retVal = new byte[size];
                for (int i = 0; i < size; ++i) {
                    retVal[i] = getBinaryData()[realAdr + i];
                }
            }
        }
        return retVal;
    }
}
