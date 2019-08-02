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
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.model.Keys;
import com.sylvain.alertcompanion.view.MainActivity;
import com.sylvain.alertcompanion.view.StopAlarmActivity;

public class SendSms   {

    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiver2;
    private Context context;

    @SuppressLint("StaticFieldLeak")
    private static SendSms instance = null;

    private SendSms(){}

    public static SendSms getInstance() {
        if (instance == null) {
            instance = new SendSms();
        }
        return instance;
    }

    /*SMS*/
    //Configuration
    public void configureAndSendSms(Context context, String modAlert){
        this.context = context;
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        configureBroadcast(modAlert);

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                new Intent(DELIVERED), 0);


      this.context.registerReceiver(broadcastReceiver,  new IntentFilter(SENT));
      this.context.registerReceiver(broadcastReceiver2, new IntentFilter(DELIVERED));

        String phoneNumber = "";
        String messageContent = "";
        SharedPreferences preferences = context.getSharedPreferences(Keys.KEY_MAIN_SAVE , Context.MODE_PRIVATE);


        if(modAlert.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            phoneNumber = preferences.getString(Keys.KEY_PHONE_NUMBER_CONTACT_ALARM, null);
            messageContent = context.getString(R.string.automatic_message) + preferences.getString(Keys.KEY_MESSAGE_CONTENT_ALARM , null);
       } else if(modAlert.equals(Keys.KEY_MOD_MESSAGE_SOS)){
           phoneNumber = preferences.getString(Keys.KEY_PHONE_NUMBER_CONTACT_SOS, null);
           messageContent = context.getString(R.string.automatic_message) + preferences.getString(Keys.KEY_MESSAGE_CONTENT_SOS , null);
       }

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, messageContent, sentPI, deliveredPI);
    }

    //Broadcast configuration
    private void configureBroadcast(String modAlert){
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
                callBackSmsStatus(modAlert, status);
                }
            }
        };

        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                {
                    String status = "";
                    switch (getResultCode()) {
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
                    callBackSmsStatus(modAlert, status);
                }
            }
        };
    }

    //Update dialog sms status
    private void callBackSmsStatus(String modAlert, String status){
        if(modAlert.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            StopAlarmActivity.updateStatusSmsSendForDialog(status);
        } else if (modAlert.equals(Keys.KEY_MOD_MESSAGE_SOS)) {
            MainActivity.updateStatusSmsSendForDialog(status);
        }
    }

    //Unregistred broadcast
    public void unregistredBroadcast(){
        context.unregisterReceiver(broadcastReceiver);
        context.unregisterReceiver(broadcastReceiver2);
    }
}
