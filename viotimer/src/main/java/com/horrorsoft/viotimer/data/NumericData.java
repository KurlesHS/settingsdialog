package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 23.10.13
 * Time: 23:55
 */
public class NumericData extends CommonData {

    private int divider;
    private int step;
    private int precision;
    private int minValue;
    private int maxValue;

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    private String suffix;
    private String prefix;

    public void setDivider(int divider) {
        this.divider = divider;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getDataDescription() {
        String formatString = "%s%." + getPrecision() + "f%s";
        String strValue = String.format(formatString, getPrefix(), (getCurrentValue() / 1.0) / getDivider(), getSuffix());
        return String.format(super.getDataDescription(), strValue);
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getDivider() {
        return divider;
    }

    public int getStep() {
        return step;
    }

    public int getPrecision() {
        return precision;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public String getSuffix() {
        if (suffix == null)
            return "";
        else
            return suffix;
    }

    public String getPrefix() {
        if (prefix == null)
            return "";
        else
            return prefix;
    }

    @Override
    public int getType() {
        return ICommonData.TYPE_NUMERIC;
    }
}
