package com.sylvain.alertcompanion.utils;

import android.app.Activity;
import android.app.AlertDialog;

import com.sylvain.alertcompanion.ui.AlarmActivity;

public class UtilsAlertDialog {


    //Alert dialog delete alarm
    public static void displayAlertDialogConfirmDeleteAlarm(Activity activity, int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("delete?");
        alertDialog.setPositiveButton("ok", (dialog, which) -> {
            AlarmActivity alarmActivity = (AlarmActivity) activity;
            alarmActivity.deleteAlarm(position); });
        alertDialog.setNegativeButton("cancel", (dialog, which) -> { });
        alertDialog.create().show();
    }
}
