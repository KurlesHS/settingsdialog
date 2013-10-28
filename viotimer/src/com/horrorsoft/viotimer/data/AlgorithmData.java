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

    public class OneRowData {
        int delay;
        int position;

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
        ArrayList[] algorithmData= new ArrayList[SERVO_COUNT];

        public Algorithm(String description, int pointer){
            for (int idx = 0; idx < algorithmData.length; ++idx) {
                algorithmData[idx] = new ArrayList<OneRowData>();
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

        public ArrayList[] getAlgorithmData() {
            return algorithmData;
        }

        public void setAlgorithmData(ArrayList[] algorithmData) {
            this.algorithmData = algorithmData;
        }
    }

    public AlgorithmData() {
        this.listOfAlgorithms = new ArrayList<Algorithm>();
    }

    public void addAlgorithm (String description, int pointer) {
        listOfAlgorithms.add(new Algorithm(description, pointer));
    }

    public int getAlgorithmCount() {
        return listOfAlgorithms.size();
    }

}