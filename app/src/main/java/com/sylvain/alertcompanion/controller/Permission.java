package com.sylvain.alertcompanion.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sylvain.alertcompanion.view.SettingsActivity;


public class Permission {

    public static void permissionSms(Activity activity){
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS},1);
        }
    }
    public static void permissionCall(Activity activity){
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},1);
        }
    }
    public static void permissionContact(Activity activity){
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},1);
        }
    }

    public static void permissionCamera(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat. checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat. requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, 1);
            }
        }
    }

}
