package com.horrorsoft.viotimer.data;

import com.horrorsoft.viotimer.common.ApplicationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 19:59
 */


public class AlgorithmData {

    public static class InfoAboutRow {
        public final int servoPos;
        public final int maxDelay;
        public final int minDelay;
        public final int delay;
        public final int position;

        public InfoAboutRow(int position, int servoPos, int minDelay, int maxDelay, int delay) {
            this.position = position;
            this.servoPos = servoPos;
            this.maxDelay = maxDelay;
            this.minDelay = minDelay;
            this.delay = delay;
        }
    }

    List<Algorithm> listOfAlgorithms;

    public String getAlgorithmDescription(int i) {
        return listOfAlgorithms.get(i).getDescription();
    }

    public static class OneRowData {
        int delay;
        int servoPos;

        public OneRowData(int delay, int servoPos) {
            this.delay = delay;
            this.servoPos = servoPos;
        }

        public int getDelay() {
            return delay;
        }

        public int getServoPos() {
            return servoPos;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public void setServoPos(int servoPos) {
            this.servoPos = servoPos;
        }
    }

    public class Algorithm {
        static final int SERVO_COUNT = 5;
        String description;
        int pointer;
        List<List<OneRowData>> algorithmData = new ArrayList<List<OneRowData>>();

        public Algorithm(String description, int pointer) {
            for (int idx = 0; idx < SERVO_COUNT; ++idx) {
                algorithmData.add(new ArrayList<OneRowData>());
            }
            setDescription(description);
            setPointer(pointer);
        }

        public int getPointer() {
            return pointer;
        }

        public void setPointer(int pointer) {
            this.pointer = pointer;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<List<OneRowData>> getArrayOfAlgorithmData() {
            return algorithmData;
        }

        public void setAlgorithmData(List<List<OneRowData>> algorithmData) {
            this.algorithmData = algorithmData;
        }
    }

    public AlgorithmData() {
        this.listOfAlgorithms = new ArrayList<Algorithm>();
    }

    public void addAlgorithm(String description, int pointer) {
        listOfAlgorithms.add(new Algorithm(description, pointer));
    }

    public int getAlgorithmCount() {
        return listOfAlgorithms.size();
    }

    public Algorithm getAlgorithm(int algorithmNumber) {
        return listOfAlgorithms.get(algorithmNumber);
    }


    public InfoAboutRow getInfoAboutAlgorithm(int algorithmNumber, int servoNumber, int position) {
        InfoAboutRow retVal = null;
        List<OneRowData> algorithmDataList = getAlgorithmData(algorithmNumber, servoNumber);
        if (position < algorithmDataList.size()) {
            OneRowData oneRowData = algorithmDataList.get(position);
            int minValue = position - 1 < 0 ? 0 : algorithmDataList.get(position - 1).getDelay() + 1;
            int maxValue = position + 1 < algorithmDataList.size() ? algorithmDataList.get(position + 1).getDelay() - 1 : ApplicationData.getGlobalMaxDelay();
            retVal = new InfoAboutRow(position, oneRowData.getServoPos(), minValue, maxValue, oneRowData.getDelay());
        }
        return retVal;
    }

    public boolean updateAlgorithmData(int algorithmNumber, int servoNumber, int position, int delay, int servoPos) {
        boolean retVal = false;
        List<OneRowData> dataList = getAlgorithmData(algorithmNumber, servoNumber);
        if (position < dataList.size()) {
            retVal = true;
            OneRowData oneRowData = dataList.get(position);
            oneRowData.setDelay(delay);
            oneRowData.setServoPos(servoPos);
        }
        return retVal;
    }

    // возвращает информацию о предпологаемом вставленном значении
    public InfoAboutRow prepareInfoAboutInsertingNewRowIntoAlgorithm(int algorithmNumber, int servoNumber, int position) {
        List<OneRowData> algorithmDataList = getAlgorithmData(algorithmNumber, servoNumber);
        if (position < 0) {
            position = 0;
        } else if (position > algorithmDataList.size()) {
            position = algorithmDataList.size();
        }

        int valueBefore = 0;
        int valueAfter = ApplicationData.getGlobalMaxDelay();
        int servoPos = -1;
        boolean hasBefore = false;
        boolean hasNext = false;
        if (position > 0) {
            hasBefore = true;
            valueBefore = algorithmDataList.get(position - 1).getDelay();
            servoPos = algorithmDataList.get(position - 1).getServoPos();
        }
        if (algorithmDataList.size() > position) {
            valueAfter = algorithmDataList.get(position).getDelay();
            hasNext = true;
            if (servoPos >= 0) {
                servoPos = algorithmDataList.get(position).getServoPos();
            }
        }
        if (servoPos < 0) {
            servoPos = 0;
        }
        int minValue = valueBefore;
        if (hasBefore)
            ++valueBefore;
        int maxValue = valueAfter;
        if (hasNext)
            --valueAfter;
        if (minValue > maxValue) {
            return null;
        }
        int medium = minValue + ((maxValue - minValue) / 2);
        return new InfoAboutRow(position, servoPos, minValue, maxValue, medium);
    }

    public boolean insertNewRowIntoAlgorithm(int algorithmNumber, int servoNumber, int position, int delay, int servoPosition) {
        List<OneRowData> algorithmData = getAlgorithmData(algorithmNumber, servoNumber);
        if (algorithmData == null) {
            return false;
        }
        OneRowData oneRowData = new OneRowData(delay, servoPosition);
        try {
            algorithmData.add(position, oneRowData);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<OneRowData> getAlgorithmData(int algorithmNumber, int servoNumber) {
        if (servoNumber < 0 || servoNumber >= 5) {
            return null;
        }
        if (algorithmNumber >= getAlgorithmCount() || algorithmNumber < 0) {
            return null;
        }
        Algorithm algorithm = getAlgorithm(algorithmNumber);
        if (algorithm == null) {
            return null;
        }
        return algorithm.getArrayOfAlgorithmData().get(algorithmNumber);
    }
}
