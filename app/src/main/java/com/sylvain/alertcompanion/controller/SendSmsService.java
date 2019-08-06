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

import java.util.ArrayList;
import java.util.List;

public class SendSmsService {


    @SuppressLint("StaticFieldLeak")
    private static SendSmsService instance = null;


    private SendSmsService(){}

    public static SendSmsService getInstance() {
        if (instance == null) {
            instance = new SendSmsService();
        }
        return instance;
    }

    /*SMS*/
    //Configuration
    public void configureAndSendSms(Context context, String modAlert){

        List<String> listContact = new ArrayList<>();
        String contacts ="";
        if(modAlert.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            contacts =context.getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE).getString(Keys.KEY_LIST_CONTACT_ALARM, null);
        }else if (modAlert.equals(Keys.KEY_MOD_MESSAGE_SOS)){
            contacts =context.getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE).getString(Keys.KEY_LIST_CONTACT_SOS, null);
        }
        listContact = Utils.convertStringContactToList(contacts);

        for (int i = 0 ; i < listContact.size() ; i++){

            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";


            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(DELIVERED), 0);


            String phoneNumber = "";
            String messageContent = "";
            SharedPreferences preferences = context.getSharedPreferences(Keys.KEY_MAIN_SAVE , Context.MODE_PRIVATE);

            String[] tabContact = listContact.get(i).split("/");
            phoneNumber = tabContact[1];

            if(modAlert.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
                messageContent = context.getString(R.string.automatic_message) + preferences.getString(Keys.KEY_MESSAGE_CONTENT_ALARM , null);
            } else if(modAlert.equals(Keys.KEY_MOD_MESSAGE_SOS)){
                messageContent = context.getString(R.string.automatic_message) + preferences.getString(Keys.KEY_MESSAGE_CONTENT_SOS , null);
            }

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, messageContent, sentPI, deliveredPI);
        }
    }
}
