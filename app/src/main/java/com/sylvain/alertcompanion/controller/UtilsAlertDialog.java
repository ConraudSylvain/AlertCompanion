package com.sylvain.alertcompanion.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.sylvain.alertcompanion.view.AlarmActivity;

public class UtilsAlertDialog {


    //Alert dialog delete alarm
    public static void displayAlertDialogConfirmDeleteAlarm(Activity activity, int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("delete?");
        alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlarmActivity alarmActivity = (AlarmActivity) activity;
                alarmActivity.deleteAlarm(position);
            }
        });
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.create().show();
    }
}
