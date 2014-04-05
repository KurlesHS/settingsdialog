package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 23.10.13
 * Time: 23:47
 */
public class CommonData implements ICommonData {

    private int size;
    private int pointer;
    private String description;
    private String dataDescription;
    private int currentValue;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDataDescription(String dataDescription) {
        this.dataDescription = dataDescription;
    }

    @Override
    public int getType() {
        return ICommonData.TYPE_UNKNOWN;
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public int getPointer() {
        return pointer;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDataDescription() {
        return dataDescription;
    }

    @Override
    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public byte[] getBinaryData() {
        return getBinaryData(getCurrentValue(), getSize());
    }

    private byte[] getBinaryData(int data, int size) {
        byte [] retVal = new byte[size];
        for (int i = 0; i < size; ++i) {
            retVal[i] = (byte)(data & 0xff);
            data >>= 8;
        }
        return retVal;
    }

    public void setCurrentValueByBinaryData(byte[] binaryData) {
        int value = 0;
        for (int i = 0; i < binaryData.length; ++i) {
            value += (binaryData[i] & 0xff) << (i * 8);
        }
        setCurrentValue(value);
    }
}

