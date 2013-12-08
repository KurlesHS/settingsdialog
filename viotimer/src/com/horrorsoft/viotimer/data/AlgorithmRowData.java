package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 07.11.13
 * Time: 0:07
 */
public class AlgorithmRowData implements Comparable {

    private int delay;
    private int servoPos;
    private int position;
    private byte servoNumber;

    public AlgorithmRowData(byte servoNumber, int position, int delay, int servoPos) {
        this.position = position;
        this.delay = delay;
        this.servoPos = servoPos;
        this.servoNumber = servoNumber;

    }

    @Override
    public int compareTo(Object another) {
        AlgorithmRowData other = (AlgorithmRowData)another;
        int retVal = 0;
        if (getDelay() < other.getDelay())
            retVal = -1;
        else if (getDelay() > other.getDelay())
            retVal = 1;
        return retVal;
    }

    public byte getServoNumber() {
        return servoNumber;
    }

    public void setServoNumber(byte servoNumber) {
        this.servoNumber = servoNumber;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getServoPos() {
        return servoPos;
    }

    public void setServoPos(int servoPos) {
        this.servoPos = servoPos;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
