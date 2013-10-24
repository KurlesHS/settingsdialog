package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 23.10.13
 * Time: 23:45
 * To change this template use File | Settings | File Templates.
 */
public interface ICommonData {
    public boolean isSeparator();
    public int getPointer();
    public String getDescription();
    public String getDataDescription();
    public byte[] getBinaryData();
    public int getCurrentValue();
}
