package com.sylvain.alertcompanion.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.controller.AlarmReceiver;
import com.sylvain.alertcompanion.controller.SendSmsService;
import com.sylvain.alertcompanion.model.Keys;

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
   private boolean blinkOk = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        timer = new Timer();

        setTimer();
        unlockScreen();
        turnOnScreen();
        blink();
    }

    /*ALARM*/
    //Set timer for stop alarm before send sms
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

    //Stop alarm user action
    private void stopAlarmUser(){
       AlarmReceiver.stopAlarm();
       timer.cancel();
       blinkOk = false;
       finish();
    }

    //Stop alarm no response
    private void stopAlarmAlert(){
        AlarmReceiver.stopAlarm();
        blinkOk = false;
        parent = this;
        parent.runOnUiThread(new Runnable() {
            public void run() {
                displayDialogSmsStatus();
            }
        });
        SendSmsService.getInstance().configureAndSendSms(this, Keys.KEY_MOD_MESSAGE_ALARM);
        timer.cancel();
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ImageView imageviewWarning = findViewById(R.id.activity_stop_alarm_imageview_warning);
                        if(imageviewWarning.getVisibility() == View.VISIBLE){
                            imageviewWarning.setVisibility(View.INVISIBLE);
                        }else{
                            imageviewWarning.setVisibility(View.VISIBLE);
                        }
                        if (blinkOk)
                        blink();
                    }
                });
            }
        }).start();
    }

    /*Alert dialog*/
    //Display alertdialog send sms
    private  void displayDialogSmsStatus(){
        String contacts = getSharedPreferences(Keys.KEY_MAIN_SAVE,MODE_PRIVATE).getString(Keys.KEY_LIST_CONTACT_ALARM, null);
        String[] tabContact = contacts.split(",");
        int totalContact = tabContact.length;
        StringBuilder name = new StringBuilder();
        name.append("(");
        for (int i = 0 ; i < tabContact.length ; i++){
            String[] contact = tabContact[i].split("/");
            name.append(contact[0]);
            if (i != totalContact-1)
                name.append(", ");
        }
        name.append(")");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("SMS sending...")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setMessage("send " + totalContact + " sms" + "\n" + name.toString());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
