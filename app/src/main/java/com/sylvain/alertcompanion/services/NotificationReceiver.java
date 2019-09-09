package com.sylvain.alertcompanion.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.data.room.DatabaseTreatment;
import com.sylvain.alertcompanion.utils.Keys;
import com.sylvain.alertcompanion.data.entities.Treatment;
import com.sylvain.alertcompanion.ui.TreatmentActivity;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        configureNextNotif(context);
        createNotificationChannel(context);
        showNotification(context);
    }

    /*ON RECEIVE*/
    private void configureNextNotif(Context context){
        if(context.getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE).getBoolean(Keys.KEY_NOTIFICATION_TREATMENT, true))
            AlarmService.configureAlarms(context, AlarmService.findNextAlarm(AlarmService.getListHourNotification(context)), Keys.KEY_ALRMMANAGER_REQUEST_CODE_NOTIFICATION);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.its_time_to_take_treatment);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }

    public void showNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Intent resultIntent = new Intent(context, TreatmentActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getResources().getString(R.string.its_time_to_take_treatment))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(createContentNotif(context)))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

       notificationManager.notify(1, notifBuilder.build());
    }


    /*UTILS*/

    //Create message notif
    private String createContentNotif(Context context){
        List<Date> lstHour;
        lstHour = AlarmService.getListHourNotification(context);
        Date nearestDate = getNearestDate(lstHour, AlarmService.getCurrentTime());
        int time = 0;
        for (int i = 0 ; i < lstHour.size() ; i ++){
            if(lstHour.get(i) == nearestDate)
                time = i + 1;
        }

        switch (time){
            case 1 : return context.getResources().getString(R.string.morning) + ": \n" + getTreatmentToString(DatabaseTreatment.getInstance(context).treatmentDao().getListTreatmentMorning());
            case 2 : return context.getResources().getString(R.string.midday) + ": \n" + getTreatmentToString(DatabaseTreatment.getInstance(context).treatmentDao().getListTreatmentMidday());
            case 3 : return context.getResources().getString(R.string.evening) + ": \n" + getTreatmentToString(DatabaseTreatment.getInstance(context).treatmentDao().getListTreatmentEvening());
            default: return null;
        }
    }

    //Convert list treatment to string
    private String getTreatmentToString(List<Treatment> lstTreatment){
        StringBuilder builder = new StringBuilder();
        for (Treatment treatment : lstTreatment){
            builder.append(treatment.getName()).append(" , ").append(treatment.getDosageQuantity())
                    .append(" ").append(treatment.getDosageUnit()).append("\n");
        }
        return builder.toString();
    }

    //Find nearest hour for display
    public static Date getNearestDate(List<Date> dates, Date currentDate) {
        long minDiff = -1, currentTime = currentDate.getTime();
        Date minDate = null;
        for (Date date : dates) {
            long diff = Math.abs(currentTime - date.getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                minDate = date;
            }
        }
        return minDate;
    }
}
