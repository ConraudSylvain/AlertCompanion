package com.sylvain.alertcompanion.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsManager;
import com.sylvain.alertcompanion.R;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SendSms {

    @SuppressLint("StaticFieldLeak")
    private static SendSms instance = null;
    private MediaPlayer player;

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

        List<String> listContact;
        String contacts ="";
        if(modAlert.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            contacts =context.getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE).getString(Keys.KEY_LIST_CONTACT_ALARM, null);
        }else if (modAlert.equals(Keys.KEY_MOD_MESSAGE_SOS)){
            contacts =context.getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE).getString(Keys.KEY_LIST_CONTACT_SOS, null);
        }
        listContact = Converter.convertStringContactToList(Objects.requireNonNull(contacts));

        for (int i = 0 ; i < listContact.size() ; i++){

            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";


            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                    new Intent(DELIVERED), 0);

            String phoneNumber;
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

            if(player == null){
                Uri path;
                if(Locale.getDefault().getLanguage().equals("fr")){
                     path = Uri.parse("android.resource://com.sylvain.alertcompanion/raw/message_envoye_fr");
                }else{
                    path = Uri.parse("android.resource://com.sylvain.alertcompanion/raw/alertcompanion_message_sent_eng");
                }
                player = MediaPlayer.create(context, path);
            }
            if(!player.isPlaying())
            player.start();
        }
    }
}
