package com.horrorsoft.viotimer.data;

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

    public boolean updateAlgorithmData(int row, int position, int delay, int servoPos) {
        boolean success = false;
        if (row < getSize()) {
            success = true;
            algorithmRowDataList.get(row).setPosition(position);
            algorithmRowDataList.get(row).setDelay(delay);
            algorithmRowDataList.get(row).setServoPos(servoPos);
            notifyAboutDataChange();
        }
        return success;
    }

    public interface IListener {
        void dataChanged();
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

    public void addListener(IListener listener) {
        if (!iListeners.contains(listener)) {
            iListeners.add(listener);
        }
    }

    public void removeListener(IListener listener) {
        if(iListeners.contains(listener)) {
            iListeners.remove(listener);
        }
    }

    List<AlgorithmRowData> algorithmRowDataList = new ArrayList<AlgorithmRowData>();

    public int getSize() {
        return algorithmRowDataList.size();
    }

    public AlgorithmRowData getAlgorithmRowData(int position) {
        AlgorithmRowData algorithmRowData = null;
        if (position >= 0 && position < getSize()) {
            algorithmRowData = algorithmRowDataList.get(position);
        }
        return algorithmRowData;
    }

    public void insertRow(int row, int delay, int servoPos) {
        if (row < 0)
            row = 0;
        else if (row >= getSize())
            row = getSize();
        algorithmRowDataList.add(row, new AlgorithmRowData(0, delay, servoPos));
        recalculatePositions();
        notifyAboutDataChange();
    }

    public void removeRow(int row) {
        if (row >= 0 && row < getSize()) {
            algorithmRowDataList.remove(row);
            recalculatePositions();
            notifyAboutDataChange();
        }
    }

    private void recalculatePositions() {
        int pos = 0;
        for (AlgorithmRowData algorithmRowData : algorithmRowDataList) {
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
