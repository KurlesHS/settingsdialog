package com.horrorsoft.viotimer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.res.ColorRes;
import com.horrorsoft.viotimer.data.AlgorithmHandler;
import com.horrorsoft.viotimer.data.AlgorithmRowData;
import com.horrorsoft.viotimer.views.AlgorithmDataRowView;
import com.horrorsoft.viotimer.views.AlgorithmDataRowView_;

/**
 * Created with IntelliJ IDEA.
 * User: Alexey
 * Date: 07.11.13
 * Time: 0:14
 */
@EBean
public class AlgorithmAdapter extends BaseAdapter implements AlgorithmHandler.IListener {

    @ColorRes
    int backgroundColorForSelectedRowInAlgorithmDataTable;

    AlgorithmHandler algorithmHandler;

    @RootContext
    Context context;

    private int selectedRow;

    @Override
    public void dataChanged() {
        notifyDataSetChangedInUi();
    }

    @UiThread
    private void notifyDataSetChangedInUi() {
        notifyDataSetChanged();
    }

    public AlgorithmHandler getAlgorithmHandler() {
        return algorithmHandler;
    }

    public void setAlgorithmHandler(AlgorithmHandler algorithmHandler) {
        if (this.algorithmHandler != null) {
            algorithmHandler.removeListener(this);
        }
        if (algorithmHandler != null) {
            algorithmHandler.addListener(this);
            this.algorithmHandler = algorithmHandler;
        }
    }

    protected void finalize() {

    }

    @Override
    public int getCount() {
        return algorithmHandler.getSize();
    }

    @Override
    public AlgorithmRowData getItem(int position) {
        return algorithmHandler.getAlgorithmRowData(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlgorithmDataRowView algorithmDataRowView;
        if (convertView == null) {
            algorithmDataRowView = AlgorithmDataRowView_.build(parent.getContext());
            //algorithmDataRowView.copyLayoutParams();
        } else {
            algorithmDataRowView = (AlgorithmDataRowView) convertView;
            //algorithmDataRowView.restoreLayoutParams();
        }
        AlgorithmRowData algorithmRowData = getItem(position);

        if (position == selectedRow) {
            //Log.d(ApplicationData.LOG_TAG, "position: " + position + ", selectedRow = " + selectedRow);
            algorithmDataRowView.setBackgroundColor(backgroundColorForSelectedRowInAlgorithmDataTable);
        } else {
            algorithmDataRowView.setBackgroundColor(Color.TRANSPARENT);
        }
        algorithmDataRowView.bind(algorithmRowData.getPosition(), algorithmRowData.getDelay(), algorithmRowData.getServoPos());
        return algorithmDataRowView;
    }

    public void setCurrentRow(int row) {
        selectedRow = row;
        notifyDataSetChanged();
    }
}
