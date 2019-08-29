package com.sylvain.alertcompanion.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;

import com.sylvain.alertcompanion.ui.StopAlarmActivity;


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
