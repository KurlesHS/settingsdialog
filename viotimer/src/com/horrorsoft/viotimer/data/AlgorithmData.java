package com.horrorsoft.viotimer.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 19:59
 */


public class AlgorithmData {

    public static class InfoAboutInsertedRow {
        public final int position;
        public final int maxValue;
        public final int minValue;
        public final int recommendedValue;

        public InfoAboutInsertedRow(int position, int minValue, int maxValue, int recommendedValue) {
            this.position = position;
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.recommendedValue = recommendedValue;
        }
    }

    List<Algorithm> listOfAlgorithms;

    public String getAlgorithmDescription(int i) {
        return listOfAlgorithms.get(i).getDescription();
    }

    public class OneRowData {
        int delay;
        int position;

        public OneRowData(int delay, int position) {
            this.delay = delay;
            this.position = position;
        }

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
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

    // возвращает информацию о предпологаемом вставленном значении
    public InfoAboutInsertedRow prepareInfoAboutInsertingNewRowIntoAlgorithm(int algorithmNumber, int servoNumber, int position) {
        List<OneRowData> algorithmDataList = getAlgorithmData(algorithmNumber, servoNumber);
        if (position < 0) {
            position = 0;
        } else if (position > algorithmDataList.size()) {
            position = algorithmDataList.size();
        }

        int valueBefore = 0;
        int valueAfter = 65535;
        if (position > 0) {
            valueBefore = algorithmDataList.get(position - 1).getDelay();
        }
        if (algorithmDataList.size() > position) {
            valueAfter = algorithmDataList.get(position).getDelay();
        }

        int minValue = valueBefore + 1;
        int maxValue = valueAfter - 1;
        if (minValue > maxValue) {
            return null;
        }
        int medium = minValue + ((maxValue - minValue) / 2);
        return new InfoAboutInsertedRow(position, minValue, maxValue, medium);
    }

    boolean insertNewRowIntoAlgorithm(int algorithmNumber, int servoNumber, int position, int delay, int servoPosition) {
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
