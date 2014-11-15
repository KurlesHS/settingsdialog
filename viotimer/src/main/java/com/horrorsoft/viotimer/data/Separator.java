package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 23.10.13
 * Time: 23:54
 */
public class Separator extends CommonData {
    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public int getType() {
        return ICommonData.TYPE_SEPARATOR;
    }
}
