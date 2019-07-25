package com.sylvain.alertcompanion.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

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

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
           KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
           keyguardManager.requestDismissKeyguard(this, null);
        } else {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK|
                PowerManager.ACQUIRE_CAUSES_WAKEUP|
                PowerManager.ON_AFTER_RELEASE, "AppName:tag");
        wl.acquire(10*60*1000L /*10 minutes*/);
    }

    private void stopAlarm(){
       AlarmReceiver.stopAlarm();
       if(AlarmService.getAlarmList(this) != null)
       AlarmService.configureAlarms(this, AlarmService.findNextAlarm(AlarmService.getAlarmList(this)));
       finish();
    }
}
