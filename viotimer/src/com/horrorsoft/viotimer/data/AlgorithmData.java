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

    List<Algorithm> listOfAlgorithms;

    public String getAlgorithmDescription(int i) {
        return listOfAlgorithms.get(i).getDescription();
    }


    public class Algorithm {
        static final int SERVO_COUNT = 5;
        String description;
        int pointer;
        List<List<AlgorithmRowData>> algorithmData = new ArrayList<List<AlgorithmRowData>>();
        List<Integer> selectedRows = new ArrayList<Integer>();

        public Algorithm(String description, int pointer) {
            for (int idx = 0; idx < SERVO_COUNT; ++idx) {
                algorithmData.add(new ArrayList<AlgorithmRowData>());
                selectedRows.add(0);
            }
            setDescription(description);
            setPointer(pointer);
        }

        public List<AlgorithmRowData> getAlgorithmForServoNum(int servoNum) {
            return algorithmData.get(servoNum);
        }

        public void setSelectedRow(int servoNum, int selectedRow) {
            int maxValue = algorithmData.get(servoNum).size();
            if (selectedRow > maxValue) {
                selectedRow = maxValue;
            }
            selectedRows.set(servoNum, selectedRow);
        }

        public int getSelectedRow(int servoNum) {
            return selectedRows.get(servoNum);
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

        public List<List<AlgorithmRowData>> getArrayOfAlgorithmData() {
            return algorithmData;
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

    public boolean insertNewRowIntoAlgorithm(int algorithmNumber, int servoNumber, int position, int delay, int servoPosition) {
        List<AlgorithmRowData> algorithmData = getAlgorithmData(algorithmNumber, servoNumber);
        if (algorithmData == null) {
            return false;
        }
        AlgorithmRowData oneRowData = new AlgorithmRowData(position, delay, servoPosition);
        try {
            algorithmData.add(position, oneRowData);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<AlgorithmRowData> getAlgorithmData(int algorithmNumber, int servoNumber) {
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
