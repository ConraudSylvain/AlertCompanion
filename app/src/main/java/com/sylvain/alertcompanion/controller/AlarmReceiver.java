package com.sylvain.alertcompanion.controller;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;

import com.sylvain.alertcompanion.view.StopAlarmActivity;

import java.io.File;


public class AlarmReceiver extends BroadcastReceiver {
    public static Ringtone ringtone;
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentStopAlarm = new Intent(context, StopAlarmActivity.class);
        intentStopAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentStopAlarm.setClass(context, StopAlarmActivity.class);
        context.startActivity(intentStopAlarm);



        /*
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();*/





//        ringtone.stop();


    }


}
