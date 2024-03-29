package com.sylvain.alertcompanion.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sylvain.alertcompanion.utils.Keys;
import com.sylvain.alertcompanion.utils.Converter;

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

    /*CONFIGURATION*/
    //Create alarm
    public static void configureAlarms(Context context, Date alarm, int requestCode){
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent myIntent = null;
         if(requestCode == Keys.KEY_ALRMMANAGER_REQUEST_CODE_ALARM){
              myIntent = new Intent(context, AlarmReceiver.class);
         }else if(requestCode == Keys.KEY_ALRMMANAGER_REQUEST_CODE_NOTIFICATION){
             myIntent = new Intent(context, NotificationReceiver.class);
         }
         @SuppressLint("SimpleDateFormat")
         SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm");
         String alarmStr = dateFormatTime.format(alarm);

        final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(alarmStr.substring(0,2)) );
            calendar.set(Calendar.MINUTE, Integer.valueOf(alarmStr.substring(3,5)));
            calendar.set(Calendar.SECOND, 0);
            if(!isToday(alarm))
                calendar.add(Calendar.DAY_OF_YEAR, 1);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context ,requestCode, myIntent, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    //Cancel alarm
    public static void cancelAlarm(Context context, int requestCode){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context ,requestCode, myIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

    /*UTILS*/
    //Check if alarm is today
    private static boolean isToday(Date alarm){
        return getCurrentTime().before(alarm);
    }

    //Get current time
    static Date getCurrentTime(){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTimeString  = dateFormat.format(Calendar.getInstance().getTime());
        try {
            return  dateFormat.parse(currentTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("error");
    }

    //Find next alarm
    public static Date findNextAlarm(List<Date> alarmList){

            Date currentTimeDate = getCurrentTime();
            for (int i = 0 ; i < alarmList.size() ; i++){
                if (currentTimeDate.before(alarmList.get(i))){
                    return alarmList.get(i);
                }
        }
        return alarmList.get(0);
    }

    //Get list all alarm
    public static List<Date> getAlarmList(Context context){
        List<Date> alarmList = new ArrayList<>();
        String [] alarm;
        String alarms = context.getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE).getString(Keys.KEY_SAVE_ALARM_LIST, null);
        if(alarms == null || alarms.length() < 1 ){
            return new ArrayList<>();
        }else{
            alarm = alarms.split(",");
            for( String date : alarm){
                alarmList.add(Converter.convertTimeStringToDate(date));
            }
        }
        return alarmList;
    }

     static List<Date> getListHourNotification(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        List<Date> lstHour = new ArrayList<>();
        lstHour.add(Converter.convertTimeStringToDate(preferences.getString(Keys.KEY_HOUR_NOTIFICATION_MORNING, "08:00")));
        lstHour.add(Converter.convertTimeStringToDate(preferences.getString(Keys.KEY_HOUR_NOTIFICATION_MIDDAY, "12:00")));
        lstHour.add(Converter.convertTimeStringToDate(preferences.getString(Keys.KEY_HOUR_NOTIFICATION_EVENING, "19:00")));
        return lstHour;
    }
}
