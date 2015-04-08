package com.horrorsoft.abctimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 23.10.13
 * Time: 23:45
 */
public interface ICommonData {
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_NUMERIC = 1;
    public static final int TYPE_COMBOBOX = 2;
    public static final int TYPE_RADIOBUTTON = 3;
    public static final int TYPE_SEPARATOR = 4;

    public boolean isEditable();
    public int getPointer();
    public int getSize();
    public String getDescription();
    public String getDataDescription();
    public byte[] getBinaryData();
    public void setCurrentValueByBinaryData(byte[] binaryData);
    public int getCurrentValue();
    public int getType();
}
