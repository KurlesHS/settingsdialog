package com.horrorsoft.viotimer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.horrorsoft.viotimer.R;
import com.horrorsoft.viotimer.data.ICommonData;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexey
 * Date: 26.10.13
 * Time: 12:37
 */
public class SettingDialogAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    List<ICommonData> objects;

    public SettingDialogAdapter(Context context, List<ICommonData> settingData) {
        ctx = context;
        objects = settingData;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ICommonData data = getData(position);
        if (data == null)
            return null;
        if (view == null) {
            if (data.isSeparator()) {
                view = lInflater.inflate(R.layout.settingseparatorview, parent, false);
                ((TextView) view.findViewById(R.id.textViewSeparator)).setText(data.getDescription());
            } else {
                view = lInflater.inflate(R.layout.settingdataview, parent, false);
                ((TextView) view.findViewById(R.id.textViewDescription)).setText(data.getDescription());
                ((TextView) view.findViewById(R.id.textViewDataDescription)).setText(data.getDataDescription());
            }
        }


        return view;
    }

    public ICommonData getData(int position) {
        return ((ICommonData) getItem(position));
    }

    @Override
    public boolean isEnabled(int position) {
        return !getData(position).isSeparator();
    }
}

