package com.horrorsoft.viotimer;

import android.content.Intent;
import android.graphics.Color;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockActivity;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
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

        LineChart mChart = new LineChart(this);

        mChart.setDrawLegend(false);
        mChart.setYRange(-1f, 1f, false);
        mChart.setDescription("Simple sinus graph for test graph");
        mChart.setUnit(" $");
        mChart.setDrawUnitsInChart(true);

        // if enabled, the chart will always start at zero on the y-axis
        mChart.setStartAtZero(false);

        // disable the drawing of values into the chart
        mChart.setDrawYValues(false);

        mChart.setDrawBorder(true);
        mChart.setBorderPositions(new BarLineChartBase.BorderPosition[] {
                BarLineChartBase.BorderPosition.BOTTOM
        });

        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setDrawVerticalGrid(false);
        mChart.setDrawHorizontalGrid(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);
        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutForCharts);
        layout.addView(mChart);
        ArrayList<Entry> valComp1 = new ArrayList<Entry>();
        double pi = (Math.PI * 2) / 360.;
        ArrayList<String> xVal = new ArrayList<String>();
        for (int i = 0; i < 30000; ++i) {
            Entry c1e1 = new Entry((float) Math.sin((double) ((float)i * pi * 0.012)), i); // 0 == quarter 1
            valComp1.add(c1e1);
            xVal.add(Integer.toString(i));
        }
        LineDataSet setComp1 = new LineDataSet(valComp1, "Test");
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVal, dataSets);
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        // l.setTypeface(tf);
        l.setTextColor(Color.WHITE);

        XLabels xl = mChart.getXLabels();
        //xl.setTypeface(tf);
        xl.setTextColor(Color.WHITE);

        YLabels yl = mChart.getYLabels();
        //yl.setTypeface(tf);
        yl.setTextColor(Color.WHITE);
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