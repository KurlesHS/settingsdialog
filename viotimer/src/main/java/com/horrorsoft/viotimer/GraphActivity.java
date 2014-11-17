package com.horrorsoft.viotimer;

import android.content.Intent;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import java.util.ArrayList;

/**
 * Created by Alexey on 17.11.2014.
 */

@Fullscreen
@EActivity(R.layout.activity_graph)
public class GraphActivity extends SherlockActivity {
    @AfterViews
    protected void onInit() {
        LineChart chart = new LineChart(this);
        chart.setDrawLegend(false);
        chart.setYRange(-1f, 1f, false);
        chart.setDescription("Simple sinus graph for test graph");
        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutForCharts);
        layout.addView(chart);
        ArrayList<Entry> valComp1 = new ArrayList<Entry>();
        double pi = (Math.PI * 2) / 360.;
        ArrayList<String> xVal = new ArrayList<String>();
        for (int i = 0; i < 360; ++i) {
            Entry c1e1 = new Entry((float) Math.sin((double) ((float)i * pi)), i); // 0 == quarter 1
            valComp1.add(c1e1);
            xVal.add(Integer.toString(i));
        }
        LineDataSet setComp1 = new LineDataSet(valComp1, "Test");
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVal, dataSets);
        chart.setData(data);
    }

    @Click(R.id.FlightSetButton)
    public void handleFlightSettings() {
        Intent intent = new Intent(this, FlightSettingActivity_.class);
        startActivity(intent);
    }

    @Click(R.id.GenSetButton)
    public void handleCommonSettings() {
        Intent intent = new Intent(this, SettingActivity_.class);
        startActivity(intent);
    }
}