package com.horrorsoft.viotimer;

import android.content.Intent;
import com.actionbarsherlock.app.SherlockActivity;
import com.horrorsoft.viotimer.common.ApplicationData;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

/**
 * Created by Alexey on 24.11.2014.
 *
 */
@Fullscreen
@EActivity(R.layout.activity_setup_graph)
public class GraphSettingActivity extends SherlockActivity {
    @Bean
    protected ApplicationData commonData;

    @Click(R.id.showGraphButton)
    public void handleGraphButtonPushed() {
        Intent intent = new Intent(this, GraphActivity_.class);
        startActivity(intent);
    }

}
