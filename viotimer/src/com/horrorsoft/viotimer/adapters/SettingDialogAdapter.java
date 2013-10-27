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

    static private final int SEPARATOR_TYPE = 0;
    static private final int DATA_TYPE = 1;
    static private final int DATA_COUNT = 2;

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
    public int getItemViewType(int position) {
        return getData(position).isEditable() ? SEPARATOR_TYPE : DATA_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return DATA_COUNT; // separator and data
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ICommonData data = getData(position);
        int type = getItemViewType(position);
        if (data == null)
            return null;
        if (view == null) {
            ViewHolder viewHolder = new ViewHolder();
            if (type == SEPARATOR_TYPE) {
                view = lInflater.inflate(R.layout.settingseparatorview, parent, false);
                viewHolder.descriptionTextView = (TextView) view.findViewById(R.id.textViewSeparator);
            } else {
                view = lInflater.inflate(R.layout.settingdataview, parent, false);
                viewHolder.descriptionTextView = (TextView) view.findViewById(R.id.textViewDescription);
                viewHolder.dataDescriptionTextView = (TextView) view.findViewById(R.id.textViewDataDescription);
            }
            view.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder != null) {
            viewHolder.descriptionTextView.setText(data.getDescription());
            if (type == DATA_TYPE) {
                viewHolder.dataDescriptionTextView.setText(data.getDataDescription());
            }
        }
        return view;
    }

    private static class ViewHolder {
        TextView descriptionTextView, dataDescriptionTextView;
    }

    public ICommonData getData(int position) {
        return ((ICommonData) getItem(position));
    }

    @Override
    public boolean isEnabled(int position) {
        return !getData(position).isEditable();
    }
}

