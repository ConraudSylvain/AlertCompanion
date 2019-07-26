package com.sylvain.alertcompanion.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.controller.AlarmReceiver;
import com.sylvain.alertcompanion.controller.AlarmService;
import com.sylvain.alertcompanion.controller.SendSms;
import com.sylvain.alertcompanion.model.Keys;

import java.sql.Time;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StopAlarmActivity extends AppCompatActivity {

    @OnClick(R.id.activity_stop_button_stop_alarm)
    public void clickStopAlarm(){stopAlarmUser();}

    SharedPreferences preferences;
    Timer timer;
   static AlertDialog alertDialog;
   private static StopAlarmActivity parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        timer = new Timer();

        //Configure next alarm
        if(AlarmService.getAlarmList(this) != null)
            AlarmService.configureAlarms(this, AlarmService.findNextAlarm(AlarmService.getAlarmList(this)));

        setTimer();
        unlockScreen();
        turnOnScreen();
    }

    private void setTimer(){
       timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopAlarmAlert();
            }
        },Integer.valueOf(Objects.requireNonNull(preferences.getString(Keys.KEY_TIMER_ALARM, "10")))*1000);
    }

    //Unlock screen
    private void unlockScreen(){
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
    }

    //Turn on screen
    private void turnOnScreen(){
            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK|
                    PowerManager.ACQUIRE_CAUSES_WAKEUP|
                    PowerManager.ON_AFTER_RELEASE, "AppName:tag");
            wl.acquire(10*60*1000L /*10 minutes*/);
        }




    private void stopAlarmUser(){
       AlarmReceiver.stopAlarm();
       timer.cancel();
       finish();
    }

    private void stopAlarmAlert(){
        Context context = this;
        AlarmReceiver.stopAlarm();
        parent = this;
        parent.runOnUiThread(new Runnable() {
            public void run() {
                displayDialogSmsStatus(context);
            }
        });

        SendSms.getInstance().sendSms(this);
        timer.cancel();
    }

    private static void displayDialogSmsStatus(Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("SMS sending...")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendSms.getInstance().unregistredBroadcast();
                        parent.finish();
                    }
                }).setMessage("status :");
               alertDialog = alertDialogBuilder.create();
               alertDialog.show();
    }

    public static void updateStatusSmsSendForDialog(String status){
        alertDialog.dismiss();
        alertDialog.setMessage("status : " + status);
        alertDialog.show();
    }
}
