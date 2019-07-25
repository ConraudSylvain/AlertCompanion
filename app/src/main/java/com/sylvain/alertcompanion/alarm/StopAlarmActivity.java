package com.sylvain.alertcompanion.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.alarm.AlarmReceiver;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StopAlarmActivity extends AppCompatActivity {

    @OnClick(R.id.activity_stop_button_stop_alarm)
    public void clickStopAlarm(){stopAlarm();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);
        ButterKnife.bind(this);
    }

    private void stopAlarm(){
       AlarmReceiver.stopAlarm();
       if(AlarmService.getAlarmList(this) != null)
       AlarmService.configureAlarms(this, AlarmService.findNextAlarm(AlarmService.getAlarmList(this)));
       finish();
    }
}
