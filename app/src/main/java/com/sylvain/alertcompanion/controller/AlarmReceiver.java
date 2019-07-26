package com.sylvain.alertcompanion.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.sylvain.alertcompanion.view.StopAlarmActivity;


public class AlarmReceiver extends BroadcastReceiver {
    public static Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        Intent intentStopAlarm = new Intent(context, StopAlarmActivity.class);
        intentStopAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentStopAlarm);

    }

    public static void stopAlarm(){
        ringtone.stop();
    }
}
