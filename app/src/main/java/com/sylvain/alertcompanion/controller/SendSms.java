package com.sylvain.alertcompanion.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.sylvain.alertcompanion.model.Keys;
import com.sylvain.alertcompanion.view.StopAlarmActivity;

public class SendSms   {

    @SuppressLint("StaticFieldLeak")
    private static SendSms instance = null;

    private SendSms(){}

    public static SendSms getInstance() {
        if (instance == null) {
            instance = new SendSms();
        }
        return instance;
    }

    public BroadcastReceiver broadcastReceiver;
    public BroadcastReceiver broadcastReceiver2;
    private Context context;

    public void sendSms(Context context){
        this.context = context;
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";


        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---

       broadcastReceiver = new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {{
               String status = "";
               switch (getResultCode())
               {
                   case Activity.RESULT_OK:
                       Toast.makeText(context, "SMS sent",
                               Toast.LENGTH_SHORT).show();
                       status = "SMS sent";
                       break;
                   case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                       Toast.makeText(context, "Generic failure",
                               Toast.LENGTH_SHORT).show();
                       status = "Generic failure";
                       break;
                   case SmsManager.RESULT_ERROR_NO_SERVICE:
                       Toast.makeText(context, "No service",
                               Toast.LENGTH_SHORT).show();
                       status = "No service";
                       break;
                   case SmsManager.RESULT_ERROR_NULL_PDU:
                       Toast.makeText(context, "Null PDU",
                               Toast.LENGTH_SHORT).show();
                       status = "Null PDU";
                       break;
                   case SmsManager.RESULT_ERROR_RADIO_OFF:
                       Toast.makeText(context, "Radio off",
                               Toast.LENGTH_SHORT).show();
                       status = "Radio off";
                       break;
               }
               StopAlarmActivity.updateStatusSmsSendForDialog(status);
               //context.unregisterReceiver(this);
               }
           }
       };
      this.context.registerReceiver(broadcastReceiver,  new IntentFilter(SENT));



        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                {
                    String status = "";
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(context, "SMS delivered",
                                    Toast.LENGTH_SHORT).show();
                            status = "SMS delivered";
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(context, "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                            status = "SMS not delivered";
                            break;
                    }
                    StopAlarmActivity.updateStatusSmsSendForDialog(status);
                }
            }
        };
       this.context.registerReceiver(broadcastReceiver2, new IntentFilter(DELIVERED));

        SharedPreferences preferences = context.getSharedPreferences(Keys.KEY_MAIN_SAVE , Context.MODE_PRIVATE);
        String phoneNumber = preferences.getString(Keys.KEY_PHONE_NUMBER_CONTACT, null);
        String messageContent = preferences.getString(Keys.KEY_MESSAGE_CONTENT , null);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, messageContent, sentPI, deliveredPI);

    }

    public void unregistredBroadcast(){
        context.unregisterReceiver(broadcastReceiver);
        context.unregisterReceiver(broadcastReceiver2);
    }
}
