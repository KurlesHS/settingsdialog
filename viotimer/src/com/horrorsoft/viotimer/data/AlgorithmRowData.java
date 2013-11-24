package com.horrorsoft.viotimer.data;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 07.11.13
 * Time: 0:07
 */
public class AlgorithmRowData {

    private int delay;
    private int servoPos;
    private int position;

    public AlgorithmRowData(int position, int delay, int servoPos) {
        this.position = position;
        this.delay = delay;
        this.servoPos = servoPos;

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
