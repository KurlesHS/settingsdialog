package com.horrorsoft.viotimer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.*;

import java.util.ArrayList;

/**
 * Created by Alexey on 17.11.2014.
 * Yep, I create this shit
 */

@Fullscreen
@EActivity(R.layout.activity_graph)
public class GraphActivity extends SherlockActivity {
    class FlightValue {
        FlightValue(byte[] data) {
            int intData = ApplicationData.getIntFromBytes(data);

            mRdtFlag = (intData & 0x01) != 0;
            mDtFlag = (intData & 0x02) != 0;
            int t = (intData >>> 2) & 0x03ff;
            int v = (intData >>> 12) & 0x03ff;
            int h = (intData >>> 22) & 0x03ff;
            mTemperature = (double)t * 0.1 - 20.;
            mHeight = h - 0x40;
            mSpeed = (double)v * 0.01 ;
        }

        int mHeight;
        double mSpeed;
        double mTemperature;
        boolean mDtFlag;
        boolean mRdtFlag;

        public boolean getDtFlag() {return mDtFlag;}
        public boolean getRdtFlag() {return mRdtFlag;}
        public int getHeight() {return  mHeight;}
        public double getSpeed() {return mSpeed;}
        public double getTemperature() {return  mTemperature;}
    }

    class FlightChunk {
        FlightValue[] mFlightData = null;
        int flightNumber = 0;

        FlightChunk(byte[] data) {
            if (data.length == 0x40) {
                byte[] tmp = new byte[4];
                System.arraycopy(data, 0x3c, tmp, 0, 0x04);
                flightNumber = ApplicationData.getIntFromBytes(tmp);
                mFlightData = new FlightValue[0x0f];
                for (int i = 0; i < 0x0f; ++i) {
                    System.arraycopy(data, i * 0x04, tmp, 0, 0x04);
                    mFlightData[i] = new FlightValue(tmp);
                }
            }
        }

        boolean isValid() {
            return mFlightData != null;
        }

        public FlightValue[] getFlightData() {
            return mFlightData;
        }

        public int getFlightNumber() {
            return flightNumber;
        }
    }

    @Bean
    protected ApplicationData commonData;

    private byte[] mBuffForChunk = new byte[0x40];


    @AfterViews
    protected void onInit() {
        byte[] flightHistoryData = commonData.getFlightHistoryData();
        if (flightHistoryData == null) {
            return;
        }

        int flightNum = 0;
        boolean firstTime = true;
        int currentStep = 0x02;
        ArrayList<Entry> heights = new ArrayList<>();
        ArrayList<Entry> speeds = new ArrayList<>();
        ArrayList<Entry> temperatures = new ArrayList<>();
        ArrayList<String> xVal = new ArrayList<>();
        int currentXAxis = 0x00;
        for(int i = 0; i < 100; ++i) {
            System.arraycopy(flightHistoryData, i * 0x40, mBuffForChunk, 0, 0x40);
            FlightChunk fc = new FlightChunk(mBuffForChunk);
            if (!fc.isValid()) {
                break;
            }
            if (firstTime) {
                firstTime = false;
                flightNum = fc.getFlightNumber();
            } else if (flightNum != fc.getFlightNumber()) {
                break;
            }
            for (FlightValue fv : fc.mFlightData) {
                speeds.add(new Entry((float) fv.getSpeed(), currentXAxis));
                heights.add(new Entry(fv.getHeight(), currentXAxis));
                temperatures.add(new Entry((float) fv.getTemperature(), currentXAxis));
                double time = currentXAxis * 0.05;
                xVal.add(String.format("%.2f", time));
                currentXAxis += currentStep;
            }
            if (i == 12) {
                currentStep = 5;
            } else if (i == 60) {
                currentStep = 20;
            }
        }

        LineDataSet setHeight = new LineDataSet(heights, "Altitude");
        LineDataSet setSpeed = new LineDataSet(speeds, "Speed");


        setHeight.setAxisDependency(YAxis.AxisDependency.LEFT);
        setHeight.setColor(ColorTemplate.getHoloBlue());
        setHeight.setCircleColor(ColorTemplate.getHoloBlue());
        setHeight.setValueTextColor(Color.WHITE);
        setHeight.setLineWidth(2f);
        setHeight.setCircleSize(3f);
        setHeight.setFillAlpha(65);
        setHeight.setFillColor(ColorTemplate.getHoloBlue());
        setHeight.setHighLightColor(Color.rgb(244, 117, 117));
        setHeight.setDrawCircleHole(false);

        setSpeed.setAxisDependency(YAxis.AxisDependency.RIGHT);
        setSpeed.setValueTextColor(Color.WHITE);
        setSpeed.setColor(Color.RED);
        setSpeed.setCircleColor(Color.RED);
        setSpeed.setLineWidth(2f);
        setSpeed.setCircleSize(3f);
        setSpeed.setFillAlpha(65);
        setSpeed.setFillColor(ColorTemplate.getHoloBlue());
        setSpeed.setHighLightColor(Color.rgb(244, 117, 117));
        setSpeed.setDrawCircleHole(false);


        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setHeight);
        dataSets.add(setSpeed);
        LineData data = new LineData(xVal, dataSets);


        LineChart mChart = new LineChart(this);
        mChart.setDescription("Flight graph");
        mChart.setGridBackgroundColor(Color.WHITE);
        Paint descriptionPaint = mChart.getPaint(LineChart.PAINT_DESCRIPTION);
        descriptionPaint.setColor(Color.WHITE);
        //mChart.setBackgroundColor(Color.BLACK);


        LinearLayout layout = (LinearLayout) findViewById(R.id.LayoutForCharts);
        layout.addView(mChart);
        mChart.setData(data);

        mChart.animateX(2500);
        mChart.setDrawGridBackground(false);


        //Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(tf);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(0x0400 - 0x40);
        leftAxis.setAxisMinValue(-0x40);
        leftAxis.setDrawGridLines(true);
        leftAxis.setStartAtZero(false);

        YAxis rightAxis = mChart.getAxisRight();
        //rightAxis.setTypeface(tf);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaxValue(10.24f);
        rightAxis.setStartAtZero(false);
        rightAxis.setAxisMinValue(0f);
        rightAxis.setDrawGridLines(false);
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