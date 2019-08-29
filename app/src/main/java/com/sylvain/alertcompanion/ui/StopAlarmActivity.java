package com.sylvain.alertcompanion.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.utils.AlarmService;
import com.sylvain.alertcompanion.utils.SendSmsService;
import com.sylvain.alertcompanion.data.Keys;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StopAlarmActivity extends AppCompatActivity {

    @OnClick(R.id.activity_stop_button_stop_alarm)
    public void clickStopAlarm(){stopAlarmUser();}
    @BindView(R.id.activity_stop_textview_message_click)
    TextView textViewMessageClick;

    SharedPreferences preferences;
    Timer timer;
   static AlertDialog alertDialog;
   private boolean blinkOk = true;
   private MediaPlayer player;
   private Camera cam ;
   boolean stopFlash = false;
   boolean allowFlash = false ;
   int tentativCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);
        ButterKnife.bind(this);
        preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        configureAll();

    }

    private void configureAll(){
        timer = new Timer();
        setNextAlarm();
        unlockScreen();
        turnOnScreen();
        playAudio();
        if(preferences.getBoolean(Keys.KEY_FLASH, true))
        openCamera();
        setTimer();
        blink();
    }

    /*ALARM*/
    void setNextAlarm(){
        //Configure next alarm
        if(AlarmService.getAlarmList(this) != null)
            AlarmService.configureAlarms(this, AlarmService.findNextAlarm(AlarmService.getAlarmList(this)));


    }

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
        stopAudio();
       timer.cancel();
       blinkOk = false;
       stopFlash = true;
       finish();
    }

    //Stop alarm no response
    private void stopAlarmAlert(){
        stopAudio();
        blinkOk = false;
        stopFlash = true;
        StopAlarmActivity parent = this;
        parent.runOnUiThread(new Runnable() {
            public void run() {
                displayDialogSmsStatus();
            }
        });
        SendSmsService.getInstance().configureAndSendSms(this, Keys.KEY_MOD_MESSAGE_ALARM);
        timer.cancel();
        flashOff();
    }

    /*UI*/
    //Screen blink
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
                            blinkFlash();
                            vibrate();
                        }else{
                            imageviewWarning.setVisibility(View.VISIBLE);
                            blinkFlash();
                        }
                        if (blinkOk)
                        blink();
                    }
                });
            }
        }).start();
    }

    /*FLASH*/

    //Open camera for light
    private void openCamera(){
        tentativCount = 10;
        Timer time = new Timer();
        TimerTask task = new TimerTask(){

            @Override
            public void run() {
                try{
                    cam = Camera.open();
                    time.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            configureFlash();
                        }
                    });

                } catch (RuntimeException r){
                    System.out.println("error camera.open");
                    tentativCount -= 1;
                    if(tentativCount <= 0)
                        time.cancel();
                }
            }
        };
        time.scheduleAtFixedRate(task,500, 500);
    }

    //configure Flash
    private void configureFlash(){
        Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
        try{
            cam.setParameters(p);
            try {
                cam.setPreviewTexture(mPreviewTexture);
                allowFlash = true;

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (RuntimeException r){
            r.printStackTrace();
        }
    }




    //Flash blink
    private void blinkFlash(){
        if(allowFlash){
            flashOn();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    flashOff();
                }
            };
            new Timer().schedule(task,100);
        }
    }

    //Turn on flask
    private void flashOn(){
        cam.startPreview();
    }

    //Turn off flash
    private void flashOff(){
        cam.stopPreview();
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


    /*AUDIO*/

     void playAudio(){
        setVolume();
        Uri path;
         if(preferences.getBoolean(Keys.KEY_TYPE_ALARM_VOICE, true)){
             path = Uri.parse("android.resource://com.sylvain.alertcompanion/raw/alert_companion_click_button_fr");
         } else{
             path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
             if (path == null) {
                 path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
             }
         }

         player = MediaPlayer.create(this, path);
         player.setLooping(true);
         player.start();
     }

    private void setVolume(){
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int settingsVolume = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE).getInt(Keys.KEY_ALRM_VOLUME, 10);
        float targetVolume =(float)  volumeMax / 10 * settingsVolume;

        am.setStreamVolume(AudioManager.STREAM_MUSIC,(int) targetVolume,0);
    }

     void stopAudio(){

             player.stop();
         }

     /*VIBRATE*/

    private void vibrate(){
        if(preferences.getBoolean(Keys.KEY_VIBRATE, true)){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }

    }
}
