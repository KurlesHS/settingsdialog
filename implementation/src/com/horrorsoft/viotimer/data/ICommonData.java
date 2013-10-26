package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 23.10.13
 * Time: 23:45
 * To change this template use File | Settings | File Templates.
 */
public interface ICommonData {
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_COMBOBOX = 1;
    public static final int TYPE_RADIOBUTTON = 2;
    public static final int TYPE_SEPARATOR = 3;
    public static final int TYPE_NUMERIC = 4;
    public boolean isSeparator();
    public int getPointer();
    public String getDescription();
    public String getDataDescription();
    public byte[] getBinaryData();
    public int getCurrentValue();
    public int getType();
}
