package com.horrorsoft.viotimer.data;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.horrorsoft.viotimer.common.ApplicationData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 07.11.13
 * Time: 3:28
 */


@EBean
public class AlgorithmHandler {

    @Bean
    protected ApplicationData applicationData;

    private AlgorithmData algorithmData;

    private int currentServoNum;
    private int currentAlgorithmNum;

    public void setCurrentServoNum(int currentServoNum) {
        this.currentServoNum = currentServoNum;
        notifyAboutDataChange();
    }

    public void setCurrentAlgorithmNum(int currentAlgorithmNum) {
        this.currentAlgorithmNum = currentAlgorithmNum;
        notifyAboutDataChange();
    }

    public void setCurrentAlgorithmAndServoNum(int algorithmNum, int servoNum) {
        this.currentAlgorithmNum = algorithmNum;
        this.currentServoNum = servoNum;
        notifyAboutDataChange();
    }

    public boolean updateAlgorithmData(int row, int position, int delay, int servoPos) {
        boolean success = false;
        if (row < getSize()) {
            success = true;
            getCurrentListOfAlgorithmRows().get(row).setPosition(position);
            getCurrentListOfAlgorithmRows().get(row).setDelay(delay);
            getCurrentListOfAlgorithmRows().get(row).setServoPos(servoPos);
            notifyAboutDataChange();
        }
        return success;
    }

    public interface IListener {
        void dataChanged();
    }

    AlgorithmHandler() {
        currentAlgorithmNum = currentServoNum = 0;
    }

    @AfterInject
    void init() {
        algorithmData = applicationData.getAlgorithmData();
    }

    private List<IListener> iListeners = new ArrayList<IListener>();

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

    public int getSelectedRow() {
        return algorithmData.getAlgorithm(currentAlgorithmNum).getSelectedRow(currentServoNum);
    }

    public void setSelectedRow(int selectedRow) {
        algorithmData.getAlgorithm(currentAlgorithmNum).setSelectedRow(currentServoNum, selectedRow);
        notifyAboutDataChange();
    }

    public void addListener(IListener listener) {
        if (!iListeners.contains(listener)) {
            iListeners.add(listener);
        }
    }

    public void removeListener(IListener listener) {
        if (iListeners.contains(listener)) {
            iListeners.remove(listener);
        }
    }

    public int getSize() {
        /*
        return algorithmRowDataList.size();
         */
        return algorithmData.getAlgorithm(currentAlgorithmNum).getAlgorithmForServoNum(currentServoNum).size();
    }

    public AlgorithmRowData getAlgorithmRowData(int position) {
        AlgorithmRowData algorithmRowData = null;
        if (position >= 0 && position < getSize()) {
            algorithmRowData = getCurrentListOfAlgorithmRows().get(position);
        }
        return algorithmRowData;
    }

    public void insertRow(int row, int delay, int servoPos) {
        insertRow(row, delay, servoPos, true);
    }

    private List<AlgorithmRowData> getCurrentListOfAlgorithmRows() {
         return   algorithmData.getAlgorithm(currentAlgorithmNum).getAlgorithmForServoNum(currentServoNum);
    }

    public void insertRow(int row, int delay, int servoPos, boolean updateChanges) {
        if (row < 0)
            row = 0;
        else if (row >= getSize())
            row = getSize();
        getCurrentListOfAlgorithmRows().add(row, new AlgorithmRowData((byte)(currentServoNum + 1), 0, delay, servoPos));
        recalculatePositions();
        if (updateChanges) {
            notifyAboutDataChange();
        }
    }

    public void removeRow(int row) {
        if (row >= 0 && row < getSize()) {
            getCurrentListOfAlgorithmRows().remove(row);
            recalculatePositions();
            notifyAboutDataChange();
        }
    }

    private void recalculatePositions() {
        int pos = 0;
        for (AlgorithmRowData algorithmRowData : getCurrentListOfAlgorithmRows()) {
            algorithmRowData.setPosition(++pos);
        }
    }

    public InfoAboutRow getInfoAboutRow(int row) {
        int maxDelay = ApplicationData.getGlobalMaxDelay();
        int minDelay = ApplicationData.getGlobalMinDelay();
        InfoAboutRow infoAboutRow = null;
        if (row >= 0 && row < getSize()) {
            AlgorithmRowData algorithmRowData = getAlgorithmRowData(row);
            int servoPos = algorithmRowData.getServoPos();
            int delay = algorithmRowData.getDelay();
            if (getSize() > 0 && row > 0) {
                algorithmRowData = getAlgorithmRowData(row - 1);
                minDelay = algorithmRowData.getDelay() + 1;
            }
            if (row + 1 < getSize()) {
                algorithmRowData = getAlgorithmRowData(row + 1);
                maxDelay = algorithmRowData.getDelay() - 1;
            }
            infoAboutRow = new InfoAboutRow(row, servoPos, minDelay, maxDelay, delay);
        }
        return infoAboutRow;
    }

    public InfoAboutRow getInfoInsertingAboutRow(int row) {
        int globalMaxDelay = ApplicationData.getGlobalMaxDelay();
        int maxDelay = globalMaxDelay;
        int minDelay = ApplicationData.getGlobalMinDelay();
        int servoPos = -1;
        InfoAboutRow infoAboutRow = null;
        if (row >= 0 && row <= getSize()) {
            if (row - 1 < getSize() && row - 1 >= 0) {
                AlgorithmRowData algorithmRowData = getAlgorithmRowData(row - 1);
                minDelay = algorithmRowData.getDelay() + 1;
                servoPos = algorithmRowData.getServoPos();
            }
            if (row < getSize()) {
                AlgorithmRowData algorithmRowData = getAlgorithmRowData(row);
                maxDelay = algorithmRowData.getDelay() - 1;
                if (servoPos < 0) {
                    servoPos = algorithmRowData.getServoPos();
                }
            }
            if (servoPos < 0) {
                servoPos = 0;
            }
            int middle = (maxDelay - minDelay) / 2;
            if (middle > 0) {
                int recommendedDelay = middle + minDelay;
                if (globalMaxDelay < recommendedDelay) {
                    recommendedDelay = globalMaxDelay;
                }
                infoAboutRow = new InfoAboutRow(row, servoPos, minDelay, maxDelay, recommendedDelay);
            }
        }
        return infoAboutRow;
    }

    private void notifyAboutDataChange() {
        for (IListener listener : iListeners) {
            listener.dataChanged();
        }
    }
}
