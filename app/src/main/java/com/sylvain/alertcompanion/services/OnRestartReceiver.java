package com.sylvain.alertcompanion.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sylvain.alertcompanion.utils.Keys;

import java.util.Objects;

public class OnRestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_BOOT_COMPLETED) ){
            setNextAlarm(context);
            configureNextNotif(context);
        }
    }

    /*ALARM*/
   public static void setNextAlarm(Context context){
        //Configure next alarm
        if(AlarmService.getAlarmList(context) != null && AlarmService.getAlarmList(context).size() != 0)
            AlarmService.configureAlarms(context, AlarmService.findNextAlarm(AlarmService.getAlarmList(context)), Keys.KEY_ALRMMANAGER_REQUEST_CODE_ALARM);
    }

    public static void configureNextNotif(Context context){
        if(context.getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE).getBoolean(Keys.KEY_NOTIFICATION_TREATMENT, true))
            AlarmService.configureAlarms(context, AlarmService.findNextAlarm(AlarmService.getListHourNotification(context)), Keys.KEY_ALRMMANAGER_REQUEST_CODE_NOTIFICATION);
    }
}
