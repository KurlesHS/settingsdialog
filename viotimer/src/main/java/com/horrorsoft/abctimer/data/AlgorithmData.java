package com.horrorsoft.abctimer.data;

import com.horrorsoft.abctimer.common.ApplicationData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 28.10.13
 * Time: 19:59
 */

@SuppressWarnings("unchecked")
public class AlgorithmData {

    public interface Serialize {
        void serializeToByteArray(int pointer, byte[] value);
    }

    public interface Deserealize {
        byte[] getAlgorithmByteArrayFromPointer(int pointer);
    }

    private List<Algorithm> listOfAlgorithms;// = new ArrayList<Algorithm>();

    public String getAlgorithmDescription(int i) {
        return listOfAlgorithms.get(i).getDescription();
    }

    public void serializeToByteArray(Serialize serialize) {
        for (Algorithm algorithm : listOfAlgorithms) {
            algorithm.serializeToByteArray(serialize);
        }
    }

    public void recalculatePositions() {
        for (Algorithm algorithm: listOfAlgorithms) {
            algorithm.recalculatePositions();
        }
    }

    public void deserializeFromBinaryDat(Deserealize deserealize) {

        for (Algorithm algorithm : listOfAlgorithms) {
            algorithm.deserializeFromByteArray(deserealize);

        }
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

        public void recalculatePositions() {
            for (List<AlgorithmRowData> algorithmRowDatas : algorithmData) {
                int pos = 0;
                for (AlgorithmRowData algorithmRowData : algorithmRowDatas) {
                    algorithmRowData.setPosition(++pos);
                }
            }

        }

        public void deserializeFromByteArray(Deserealize deserealize) {
            byte[] data = deserealize.getAlgorithmByteArrayFromPointer(getPointer());
            if (data != null) {
                int currentDelay = 0;
                for (int i = 0; i < data.length; i += 4) {
                    int servoNum = ApplicationData.byteToInt(data[i]);
                    int deltaT = ApplicationData.byteToInt(data[i + 1]) + ApplicationData.byteToInt(data[i + 2]) * 0x100;
                    int servoPos = ApplicationData.byteToInt(data[i + 3]);
                    servoNum -= 1;
                    int position = 1;
                    int servoCount = algorithmData.size();
                    if (servoNum >= 0 && servoNum < servoCount) {
                        getArrayOfAlgorithmData().get(servoNum).add(new AlgorithmRowData((byte) (servoNum + 1),
                                position, currentDelay + deltaT, servoPos));
                        ++position;
                    }
                    currentDelay += deltaT;
                }
            }
        }

        public void serializeToByteArray(Serialize serialize) {
            List<AlgorithmRowData> allAlgorithms = new ArrayList<AlgorithmRowData>();
            for (List<AlgorithmRowData> algorithmRowDatas : getArrayOfAlgorithmData()) {
                allAlgorithms.addAll(algorithmRowDatas);
            }
            // сортируем, что бы легче было писать в куда нибудь;
            if (allAlgorithms.size() > 0) {
                Collections.sort(allAlgorithms);
                byte[] bytesToSave = new byte[allAlgorithms.size() * 4 + 1];
                int currentDelay = 0;
                int currentOffset = 0;
                for (AlgorithmRowData algorithmRowData : allAlgorithms) {
                    int delay = algorithmRowData.getDelay();
                    int deltaDelay = delay - currentDelay;
                    currentDelay += deltaDelay;
                    // номер сервы
                    bytesToSave[currentOffset] = algorithmRowData.getServoNumber();
                    // задержка относительно предыдущего шага
                    bytesToSave[currentOffset + 1] = (byte) (deltaDelay % 0x100);
                    bytesToSave[currentOffset + 2] = (byte) (deltaDelay >> 8);
                    // положение сервыэ
                    bytesToSave[currentOffset + 3] = (byte) (algorithmRowData.getServoPos());
                    currentOffset += 4;
                }
                // последний байт - 0х00
                bytesToSave[bytesToSave.length - 1] = 0x00;
                serialize.serializeToByteArray(getPointer(), bytesToSave);
            }
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
            int selectedRow = selectedRows.get(servoNum);
            if (algorithmData.get(servoNum).size() <= selectedRow) {
                selectedRow = -1;
            }
            return selectedRow;
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
        if ( algorithmNumber >= listOfAlgorithms.size()) {
            return null;
        }
        return listOfAlgorithms.get(algorithmNumber);
    }
}
