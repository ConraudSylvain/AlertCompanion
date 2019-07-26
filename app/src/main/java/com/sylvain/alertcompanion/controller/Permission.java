package com.sylvain.alertcompanion.controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class Permission {

    public static void permissionSms(Activity activity){
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS},1);
        }
    }
}
