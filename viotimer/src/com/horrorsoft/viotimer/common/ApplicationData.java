package com.horrorsoft.viotimer.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.horrorsoft.viotimer.R;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.api.Scope;
import com.horrorsoft.viotimer.bluetooth.BlueToothConnectionThread;
import com.horrorsoft.viotimer.bluetooth.BlueToothListener;
import com.horrorsoft.viotimer.data.AlgorithmData;
import com.horrorsoft.viotimer.data.ICommonData;
import com.horrorsoft.viotimer.json.JsonSetting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 22:21
 */
@EBean(scope = Scope.Singleton)
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
    private ProgressDialog myProgressDialog;


    //  bluetooth stuff
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private Handler mHandler = null;
    private BlueToothConnectionThread mConnectThread = null;
    Integer REQ_BT_ENABLE=1;

    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private boolean mConnectionStatus = false;
    private ArrayList<BlueToothListener> listeners = new ArrayList<BlueToothListener>();

    private static final int NEW_DATA_ARRIVED = 3;
    private static final int CONNECTION_ESTABLISHED = 4;
    private static final int CONNECTION_FAILED = 5;
    private static final int EXIT_CONNECTION_THREAD = 6;

    // Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use);
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public void addBlueToothListener(BlueToothListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeBlueToothListener(BlueToothListener listener) {
        listeners.remove(listener);
    }

    public boolean getBlueToothConnectionStatus() {
        return mConnectionStatus;
    }

    public boolean isBlueToothSupported() {
        return mBluetoothAdapter != null;
    }

    private void emitBlueToothStatusChanged(boolean status) {
        for (BlueToothListener listener : listeners) {
            listener.bluetoothStatusChanged(status);
        }
    }

    private void emitBlueToothIncomingData(byte buffer[]) {
        for (BlueToothListener listener : listeners) {
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
            outputStream.write(getBinaryData(), 0, 0x2f00);
            outputStream.close();
        } catch (Exception e) {
            success = false;
        }
        freeBinaryData();
        return success;
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
    }

    public ApplicationData() {
        initBlueTooth();
    }

    private void initBlueTooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler(Looper.getMainLooper()) {

            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEW_DATA_ARRIVED:
                    {
                        byte array[] = msg.getData().getByteArray("data");
                        emitBlueToothIncomingData(array);
                    }
                    break;
                    case EXIT_CONNECTION_THREAD:
                    {
                        if (context != null)
                            Toast.makeText(context, "exit from thread", Toast.LENGTH_SHORT).show();
                        mConnectionStatus = false;
                        emitBlueToothStatusChanged(mConnectionStatus);
                    }
                    break;
                    case CONNECTION_ESTABLISHED:
                    {
                        closeProgressDialog();
                        mConnectionStatus = true;
                        emitBlueToothStatusChanged(mConnectionStatus);

                    }
                    break;
                    case CONNECTION_FAILED:
                    {
                        closeProgressDialog();
                        mConnectionStatus = false;
                        emitBlueToothStatusChanged(mConnectionStatus);
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
        myProgressDialog = ProgressDialog.show(activity, activity.getResources().getString(R.string.pleaseWait), activity.getResources().getString(R.string.makingConnectionString), true);
        mConnectThread = new BlueToothConnectionThread(macAddress);
        mConnectThread.start();
    }

    public void disconnect() {
        if (outputStream != null) {
            try {
                mConnectionStatus = false;
                outputStream.close();
                btSocket.close();

                //CnBtn.setImageResource(R.drawable.con_scr);
            } catch (IOException e) {
            }
        }
    }


    private void initBinaryData() {
        setFirstFreeAddress(0x0100);
        setBinaryData(new byte[0x3000]);
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
            int realAddr = getBinaryData()[pointer] + getBinaryData()[pointer + 1] * 0x100;
            if (realAddr < 0x0100) {
                realAddr = getFirstFreeAddress();
                setFirstFreeAddress(realAddr + value.length);
                success = writeIntToByteArray(getBinaryData(), pointer, 2, realAddr);
            }
            if (success) {
                success = writeBytesToByteArray(getBinaryData(), realAddr, value);
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
            int realAddr = getBinaryData()[pointer] + getBinaryData()[pointer + 1] * 0x100;
            if (getBinaryData().length >= realAddr + size) {
                retVal = new byte[size];
                for (int i = 0; i < size; ++i) {
                    retVal[i] = getBinaryData()[realAddr + i];
                }
            }
        }
        return retVal;
    }
}
