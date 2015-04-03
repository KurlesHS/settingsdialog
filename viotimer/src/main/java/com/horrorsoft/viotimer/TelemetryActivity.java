package com.horrorsoft.viotimer;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.horrorsoft.viotimer.bluetooth.ITelemetryListener;
import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.*;
import org.androidannotations.api.BackgroundExecutor;

import java.util.ArrayList;
import java.util.ListIterator;


@Fullscreen
@EActivity(R.layout.activity_telemetry)
public class TelemetryActivity extends ActivityWithBluetoothStatuses implements ITelemetryListener {

    @ViewById(R.id.imageViewBluetoothStatus)
    protected ImageView imageViewBluetoothStatus;

    @ViewById(R.id.tm_alt_digit_1_view)
    protected ImageView tmAltDigit1;
    @ViewById(R.id.tm_alt_digit_2_view)
    protected ImageView tmAltDigit2;
    @ViewById(R.id.tm_alt_digit_3_view)
    protected ImageView tmAltDigit3;
    @ViewById(R.id.tm_alt_digit_4_view)
    protected ImageView tmAltDigit4;
    @ViewById(R.id.tm_alt_digit_5_view)
    protected ImageView tmAltDigit5;

    private ArrayList<ImageView> altitudeDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_timvol_digit_1_view)
    protected ImageView tmTimVolDigit1;
    @ViewById(R.id.tm_timvol_digit_2_view)
    protected ImageView tmTimVolDigit2;
    @ViewById(R.id.tm_timvol_digit_3_view)
    protected ImageView tmTimVolDigit3;
    @ViewById(R.id.tm_timvol_digit_4_view)
    protected ImageView tmTimVolDigit4;
    @ViewById(R.id.tm_timvol_digit_5_view)
    protected ImageView tmTimVolDigit5;

    private ArrayList<ImageView> timerVoltageDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_time_digit_1_view)
    protected ImageView timeDigit1;
    @ViewById(R.id.tm_time_digit_2_view)
    protected ImageView timeDigit2;
    @ViewById(R.id.tm_time_digit_3_view)
    protected ImageView timeDigit3;
    @ViewById(R.id.tm_time_digit_4_view)
    protected ImageView timeDigit4;
    @ViewById(R.id.tm_time_digit_5_view)
    protected ImageView timeDigit5;

    private ArrayList<ImageView> timeDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_nodat_digit_1_view)
    protected ImageView noDataDigit1;
    @ViewById(R.id.tm_nodat_digit_2_view)
    protected ImageView noDataDigit2;
    @ViewById(R.id.tm_nodat_digit_3_view)
    protected ImageView noDataDigit3;
    @ViewById(R.id.tm_nodat_digit_4_view)
    protected ImageView noDataDigit4;
    @ViewById(R.id.tm_nodat_digit_5_view)
    protected ImageView noDataDigit5;

    private ArrayList<ImageView> noDataDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_dtvol_digit_1_view)
    protected ImageView tmDtVoltageDigit1;
    @ViewById(R.id.tm_dtvol_digit_2_view)
    protected ImageView tmDtVoltageDigit2;
    @ViewById(R.id.tm_dtvol_digit_3_view)
    protected ImageView tmDtVoltageDigit3;
    @ViewById(R.id.tm_dtvol_digit_4_view)
    protected ImageView tmDtVoltageDigit4;
    @ViewById(R.id.tm_dtvol_digit_5_view)
    protected ImageView tmDtVoltageDigit5;

    private ArrayList<ImageView> dtVoltageDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_speed_digit_1_view)
    protected ImageView tmSpeedDigit1;
    @ViewById(R.id.tm_speed_digit_2_view)
    protected ImageView tmSpeedDigit2;
    @ViewById(R.id.tm_speed_digit_3_view)
    protected ImageView tmSpeedDigit3;
    @ViewById(R.id.tm_speed_digit_4_view)
    protected ImageView tmSpeedDigit4;
    @ViewById(R.id.tm_speed_digit_5_view)
    protected ImageView tmSpeedDigit5;

    private ArrayList<ImageView> speedDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_temp_digit_1_view)
    protected ImageView tmTempDigit1;
    @ViewById(R.id.tm_temp_digit_2_view)
    protected ImageView tmTempDigit2;
    @ViewById(R.id.tm_temp_digit_3_view)
    protected ImageView tmTempDigit3;
    @ViewById(R.id.tm_temp_digit_4_view)
    protected ImageView tmTempDigit4;
    @ViewById(R.id.tm_temp_digit_5_view)
    protected ImageView tmTempDigit5;

    private ArrayList<ImageView> temperatureDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_prgn_digit_1_view)
    protected ImageView tmPredictDigit1;
    @ViewById(R.id.tm_prgn_digit_2_view)
    protected ImageView tmPredictDigit2;
    @ViewById(R.id.tm_prgn_digit_3_view)
    protected ImageView tmPredictDigit3;
    @ViewById(R.id.tm_prgn_digit_4_view)
    protected ImageView tmPredictDigit4;
    @ViewById(R.id.tm_prgn_digit_5_view)
    protected ImageView tmPredictDigit5;

    private ArrayList<ImageView> predictDigitImages = new ArrayList<>();

    @ViewById(R.id.tm_sign_digit_1_view)
    protected ImageView tmSignalDigit1;
    @ViewById(R.id.tm_sign_digit_2_view)
    protected ImageView tmSignalDigit2;
    @ViewById(R.id.tm_sign_digit_3_view)
    protected ImageView tmSignalDigit3;
    @ViewById(R.id.tm_sign_digit_4_view)
    protected ImageView tmSignalDigit4;
    @ViewById(R.id.tm_sign_digit_5_view)
    protected ImageView tmSignalDigit5;

    @ViewById(R.id.tm_flag_servo_view)
    protected ImageView servoFlagImage;

    @ViewById(R.id.tm_flag_blink_view)
    protected ImageView blinkFlagImage;

    @ViewById(R.id.tm_flag_dt_view)
    protected ImageView dtFlagImage;

    @ViewById(R.id.tm_flag_rdt_view)
    protected ImageView rdtFlagImage;

    private ArrayList<ImageView> signalDigitImages = new ArrayList<>();

    private ArrayList<Drawable> digitsImages = new ArrayList<>();
    Drawable plusImg;
    Drawable minusImg;
    Drawable dotImg;
    Drawable emptyImg;

    Drawable tmServoFlagOnImg;
    Drawable tmServoFlagOffImg;

    Drawable tmBlinkFlagOnImg;
    Drawable tmBlinkFlagOffImg;

    Drawable tmDtFlagOnImg;
    Drawable tmDtFlagOffImg;

    Drawable tmRdtFlagOnImg;
    Drawable tmRdtFlagOffImg;


    @Bean
    protected ApplicationData commonData;

    private final static String FETCH_DATA_TASK_ID = "fetchTaskId";

    @AfterViews
    protected void init() {
        altitudeDigitImages.add(tmAltDigit1);
        altitudeDigitImages.add(tmAltDigit2);
        altitudeDigitImages.add(tmAltDigit3);
        altitudeDigitImages.add(tmAltDigit4);
        altitudeDigitImages.add(tmAltDigit5);

        timerVoltageDigitImages.add(tmTimVolDigit1);
        timerVoltageDigitImages.add(tmTimVolDigit2);
        timerVoltageDigitImages.add(tmTimVolDigit3);
        timerVoltageDigitImages.add(tmTimVolDigit4);
        timerVoltageDigitImages.add(tmTimVolDigit5);

        timeDigitImages.add(timeDigit1);
        timeDigitImages.add(timeDigit2);
        timeDigitImages.add(timeDigit3);
        timeDigitImages.add(timeDigit4);
        timeDigitImages.add(timeDigit5);

        noDataDigitImages.add(noDataDigit1);
        noDataDigitImages.add(noDataDigit2);
        noDataDigitImages.add(noDataDigit3);
        noDataDigitImages.add(noDataDigit4);
        noDataDigitImages.add(noDataDigit5);

        dtVoltageDigitImages.add(tmDtVoltageDigit1);
        dtVoltageDigitImages.add(tmDtVoltageDigit2);
        dtVoltageDigitImages.add(tmDtVoltageDigit3);
        dtVoltageDigitImages.add(tmDtVoltageDigit4);
        dtVoltageDigitImages.add(tmDtVoltageDigit5);

        speedDigitImages.add(tmSpeedDigit1);
        speedDigitImages.add(tmSpeedDigit2);
        speedDigitImages.add(tmSpeedDigit3);
        speedDigitImages.add(tmSpeedDigit4);
        speedDigitImages.add(tmSpeedDigit5);

        temperatureDigitImages.add(tmTempDigit1);
        temperatureDigitImages.add(tmTempDigit2);
        temperatureDigitImages.add(tmTempDigit3);
        temperatureDigitImages.add(tmTempDigit4);
        temperatureDigitImages.add(tmTempDigit5);

        predictDigitImages.add(tmPredictDigit1);
        predictDigitImages.add(tmPredictDigit2);
        predictDigitImages.add(tmPredictDigit3);
        predictDigitImages.add(tmPredictDigit4);
        predictDigitImages.add(tmPredictDigit5);

        signalDigitImages.add(tmSignalDigit1);
        signalDigitImages.add(tmSignalDigit2);
        signalDigitImages.add(tmSignalDigit3);
        signalDigitImages.add(tmSignalDigit4);
        signalDigitImages.add(tmSignalDigit5);

        plusImg = getResources().getDrawable(R.drawable.digit_plus);
        minusImg = getResources().getDrawable(R.drawable.digit_minus);
        emptyImg = getResources().getDrawable(R.drawable.digit_empty);
        dotImg = getResources().getDrawable(R.drawable.digit_point);

        tmBlinkFlagOffImg = getResources().getDrawable(R.drawable.tm_blink_flag_0);
        tmBlinkFlagOnImg = getResources().getDrawable(R.drawable.tm_blink_flag_1);

        tmServoFlagOffImg = getResources().getDrawable(R.drawable.tm_servo_flag_0);
        tmServoFlagOnImg = getResources().getDrawable(R.drawable.tm_servo_flag_1);

        tmDtFlagOffImg = getResources().getDrawable(R.drawable.tm_dt_flag_0);
        tmDtFlagOnImg = getResources().getDrawable(R.drawable.tm_dt_flag_1);

        tmRdtFlagOffImg = getResources().getDrawable(R.drawable.tm_rdt_flag_0);
        tmRdtFlagOnImg = getResources().getDrawable(R.drawable.tm_rdt_flag_1);

        digitsImages.add(getResources().getDrawable(R.drawable.digit_0));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_1));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_2));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_3));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_4));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_5));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_6));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_7));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_8));
        digitsImages.add(getResources().getDrawable(R.drawable.digit_9));
    }

    private void drawText(ArrayList<ImageView> imageViews, String text) {
        ListIterator<ImageView> li = imageViews.listIterator(imageViews.size());
        String reversedText = new StringBuilder(text).reverse().toString();
        int stringLen = reversedText.length();
        int i = 0;
        while (li.hasPrevious()) {
            ImageView nextDigitImage = li.previous();
            Drawable srcForThisPos = emptyImg;
            if (i < stringLen) {
                char ch = reversedText.charAt(i);
                if (ch == '.' || ch == ',') {
                    srcForThisPos = dotImg;
                } else if (ch == '-') {
                    srcForThisPos = minusImg;
                } else if (ch == '+') {
                    srcForThisPos = plusImg;
                } else if (ch >= '0' && ch <= '9') {
                    int idx = ch - '0';
                    srcForThisPos = digitsImages.get(idx);
                }
            }
            ++i;
            nextDigitImage.setImageDrawable(srcForThisPos);
        }
    }

    @Background(delay = 1000, id = FETCH_DATA_TASK_ID)
    protected void fetchActualDataHelper() {
        if (!commonData.getBlueToothConnectionStatus()) {
            fetchActualDataHelper();
            return;
        }
        commonData.getTelemetry();
    }

    @Override
    protected ImageView getImageViewBluetoothStatus() {
        return imageViewBluetoothStatus;
    }

    @Override
    protected ApplicationData getCommonData() {
        return commonData;
    }

    @Override
    protected void onResume() {
        super.onResume();
        commonData.setTelemetryListener(this);
        updateUi();
        fetchActualDataHelper();
    }

    @Override
    protected void onPause() {
        super.onPause();
        commonData.setTelemetryListener(null);
        BackgroundExecutor.cancelAll(FETCH_DATA_TASK_ID, false);
    }

    private String appendSign(String str, float value) {
        if (value < 0) {
            return "-" + str;
        } else if (value > 0) {
            return "+" + str;
        }
        return str;
    }

    private void setNewTelemetryData(ITelemetryListener.TelemetryData data) {
        String altitudeStr = String.format("%04d", Math.abs(data.height));
        altitudeStr = appendSign(altitudeStr, data.height);
        String voltageStr = String.format("%.2f", data.voltage);
        String timeDtStr = String.format("%04d", data.timeToDt);
        String noDataStr = String.format("%5d", data.reservedA & 0xff);
        String voltage2Str = String.format("%.2f", data.pwr);
        String speedStr = String.format("%.2f", data.speed);
        String temperatureStr = String.format("%.1f", Math.abs(data.temperature));
        temperatureStr = appendSign(temperatureStr, data.temperature);
        String predictStr = String.format("%04d", data.prediction);
        String signalStr = String.format("%5d", data.rssi & 0xff);

        drawText(altitudeDigitImages, altitudeStr);
        drawText(timerVoltageDigitImages, voltageStr);
        drawText(timeDigitImages, timeDtStr);
        drawText(noDataDigitImages, noDataStr);
        drawText(dtVoltageDigitImages, voltage2Str);
        drawText(speedDigitImages, speedStr);
        drawText(temperatureDigitImages, temperatureStr);
        drawText(predictDigitImages, predictStr);
        drawText(signalDigitImages, signalStr);

        servoFlagImage.setImageDrawable(data.servoOnFlag ? tmServoFlagOnImg : tmServoFlagOffImg);
        blinkFlagImage.setImageDrawable(data.blinkerOnFlag ? tmBlinkFlagOnImg : tmBlinkFlagOffImg);
        dtFlagImage.setImageDrawable(data.dtFlag ? tmDtFlagOnImg : tmDtFlagOffImg);
        rdtFlagImage.setImageDrawable(data.rdtFlag ? tmRdtFlagOnImg : tmRdtFlagOffImg);
    }

    @UiThread(delay = 1000)
    protected void updateUi() {
        ITelemetryListener.TelemetryData telemetryData = new ITelemetryListener.TelemetryData();
        telemetryData.act = 123;
        telemetryData.height = -23;
        telemetryData.voltage = 12.65f;
        telemetryData.hasError = false;
        telemetryData.blinkerOnFlag = true;
        telemetryData.dtFlag = true;
        telemetryData.prediction = 5432;
        telemetryData.pwr = 54.63f;
        telemetryData.rdtFlag = true;
        telemetryData.reservedA = (byte) 210;
        telemetryData.rssi = 23;
        telemetryData.servoOnFlag = true;
        telemetryData.speed = 44.32f;
        telemetryData.temperature = 44.2f;
        telemetryData.timeToDt = 1987;

        setNewTelemetryData(telemetryData);
    }

    @Override
    public void result(TelemetryData telemetryData) {
        if (!telemetryData.hasError) {
            setNewTelemetryData(telemetryData);
        }
        fetchActualDataHelper();
    }
}
