package com.sylvain.alertcompanion.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sylvain.alertcompanion.utils.Keys;
import com.sylvain.alertcompanion.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class AlarmService {

    //Configure
    public static void configureAlarms(Context context, Date alarm){
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
         Intent myIntent = new Intent(context, AlarmReceiver.class);
         SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm");
         String alarmStr = dateFormatTime.format(alarm);

        final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(alarmStr.substring(0,2)) );
            calendar.set(Calendar.MINUTE, Integer.valueOf(alarmStr.substring(3,5)));
            calendar.set(Calendar.SECOND, 0);
            if(!isToday(alarm))
                calendar.add(Calendar.DAY_OF_YEAR, 1);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context ,0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }

    public static boolean isToday(Date alarm){
        return getCurrentTime().before(alarm);
    }

    public static Date getCurrentTime(){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTimeString  = dateFormat.format(Calendar.getInstance().getTime());
        try {
            return  dateFormat.parse(currentTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("error");
    }

    public static Date findNextAlarm(List<Date> alarmList){

            Date currentTimeDate = getCurrentTime();
            for (int i = 0 ; i < alarmList.size() ; i++){
                if (currentTimeDate.before(alarmList.get(i))){
                    return alarmList.get(i);
                }
        }
        return alarmList.get(0);

    }

    public static void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context ,0, myIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public static List<Date> getAlarmList(Context context){
        List<Date> alarmList = new ArrayList<>();
        String [] alarm;
        String alarms = context.getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE).getString(Keys.KEY_SAVE_ALARM_LIST, null);
        if(alarms == null || alarms.length() < 1 ){
            return null;
        }else{
            alarm = alarms.split(",");
            for( String date : alarm){
                alarmList.add(Utils.convertTimeStringToDate(date));
            }
        }
        return alarmList;
    }
}
