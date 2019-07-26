package com.sylvain.alertcompanion.controller;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String convertTimeIntToString(int hour, int minute){
        String hourStr = String.valueOf(hour);
        if(hourStr.length() == 1)
            hourStr = "0" + hourStr;

        String minuteStr = String.valueOf(minute);
        if(minuteStr.length() == 1)
            minuteStr = "0" + minuteStr;

        return hourStr + ":" + minuteStr;
    }


    public static Date convertTimeStringToDate(String time){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            return dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("error class Utils");
    }
}
