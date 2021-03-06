package com.horrorsoft.abctimer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.ColorRes;
import com.horrorsoft.abctimer.data.AlgorithmHandler;
import com.horrorsoft.abctimer.data.AlgorithmRowData;
import com.horrorsoft.abctimer.views.AlgorithmDataRowView;
import com.horrorsoft.abctimer.views.AlgorithmDataRowView_;

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

    @Override
    public void dataChanged() {
        notifyDataSetChangedInUi();
    }

    @UiThread
    protected void notifyDataSetChangedInUi() {
        notifyDataSetChanged();
    }

    public void setAlgorithmHandler(AlgorithmHandler algorithmHandler) {
        if (this.algorithmHandler != null) {
            this.algorithmHandler.removeListener(this);
        }
        if (algorithmHandler != null) {
            algorithmHandler.addListener(this);
            this.algorithmHandler = algorithmHandler;
        }
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
        } else {
            algorithmDataRowView = (AlgorithmDataRowView) convertView;
        }
        AlgorithmRowData algorithmRowData = getItem(position);

        if (position == algorithmHandler.getSelectedRow()) {
        	algorithmDataRowView.setSelected(true, Color.parseColor("#000000"));
        } else {
        	algorithmDataRowView.setSelected(false, Color.parseColor("#ffffff"));
        }
        algorithmDataRowView.bind(algorithmRowData.getPosition(), algorithmRowData.getDelay(), algorithmRowData.getServoPos());
        return algorithmDataRowView;
    }
}
