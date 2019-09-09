package com.sylvain.alertcompanion.utils;

import android.app.Activity;
import android.app.AlertDialog;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.ui.AlarmActivity;
import com.sylvain.alertcompanion.ui.SettingsActivity;

public class UtilsAlertDialog {


    //Alert dialog delete alarm
    public static void displayAlertDialogConfirmDeleteAlarm(Activity activity, int position){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getResources().getString(R.string.delete));
        alertDialog.setPositiveButton("ok", (dialog, which) -> {
            AlarmActivity alarmActivity = (AlarmActivity) activity;
            alarmActivity.deleteAlarm(position); });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), (dialog, which) -> { });
        alertDialog.create().show();
    }

    //Alert dialog delete alarm
    public static void displayAlertDialogConfirmQuitSettings (Activity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getResources().getString(R.string.quit_without_saving ) + " ?");
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.save), (dialog, which) -> {
            SettingsActivity settingsActivity = (SettingsActivity) activity;
           settingsActivity.save(); });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.quit_without_saving), (dialog, which) -> {
            SettingsActivity settingsActivity = (SettingsActivity) activity;
            settingsActivity.back();
        });
        alertDialog.create().show();
    }

    //Alert dialog delete alarm
    public static void displayAlertDialogConfirmRestart (SettingsActivity activity){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getResources().getString(R.string.restart_app ) + " ?");
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.yes), (dialog, which) -> activity.restart());
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), (dialog, which) -> { });
        alertDialog.create().show();
    }
}
