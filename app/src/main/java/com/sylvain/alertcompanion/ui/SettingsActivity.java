package com.sylvain.alertcompanion.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.utils.AlarmService;
import com.sylvain.alertcompanion.utils.Utils;
import com.sylvain.alertcompanion.data.Keys;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.activity_settings_alarm_edittext_delay_for_stop)
    EditText editTextDelayForStop;
    @BindView(R.id.activity_settings_alarm_edittext_message_content_alarm)
    EditText editTextMessageContentAlarm;
    @BindView(R.id.activity_settings_alarm_edittext_phone_number_alarm)
    EditText editTextPhoneNumberAlarm;
    @BindView(R.id.activity_settings_alarm_edittext_phone_number_sos)
    EditText editTextPhoneNumberSos;
    @BindView(R.id.activity_settings_alarm_edittext_message_content_sos)
    EditText editTextMessageContentSos;
    @BindView(R.id.activity_settings_alarm_checkbox_confirm_send_sms)
    CheckBox checkBoxPopUpConfirmSendSms;
    @BindView(R.id.activity_settings_activity_linearlayout_contains_contact_alarm)
    LinearLayout linearLayoutContainsContactAlarm;
    @BindView(R.id.activity_settings_activity_linearlayout_contains_contact_sos)
    LinearLayout linearLayoutContainsContactSos;
    @BindView(R.id.activity_settings_seekbar_volume)
    SeekBar seekBarVolume;
    @BindView(R.id.activity_settings_radiobutton_alarm)
    RadioButton radioButtonAlarm;
    @BindView(R.id.activity_settings_radiobutton_voice)
    RadioButton radioButtonVoice;
    @BindView(R.id.activity_settings_togglebutton_flash)
    ToggleButton toggleButtonFlash;
    @BindView(R.id.activity_settings_togglebutton_vibrate)
    ToggleButton toggleButtonVibrate;
    @BindView(R.id.activity_settings_checkbox_notification_treatment)
    CheckBox checkBoxNotificationTreatment;
    @BindView(R.id.activity_settings_edittext_hour_morning_treatment)
    EditText editTextHourMorning;
    @BindView(R.id.activity_settings_edittext_hour_midday_treatment)
    EditText editTextHourMidday;
    @BindView(R.id.activity_settings_edittext_hour_evening_treatment)
    EditText editTextHourEvening;

    @OnClick(R.id.activity_settings_alarm_button_contact_alarm)
    public void clickOpenContactAlarm(){ openContact(Keys.KEY_MOD_MESSAGE_ALARM);
    }
    @OnClick(R.id.activity_settings_alarm_button_add_contact_alarm)
    public void clickAddAlarm(){ addCustomContactAlarm();
    }
    @OnClick(R.id.activity_settings_alarm_button_contact_sos)
    public void clickOpenContactSos(){ openContact( Keys.KEY_MOD_MESSAGE_SOS);
    }
    @OnClick(R.id.activity_settings_alarm_button_add_contact_sos)
    public void clickAddSos(){addCustomContactSos();
    }

    @OnClick(R.id.activity_settings_alarm_button_save)
    public void clickSave() { save();
    }

    private List<String> listContactAlarm = new ArrayList<>();
    private List<String> listContactSos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_alarm);
        ButterKnife.bind(this);
        configureAll();
    }


    private void configureAll(){
        configureToolbar();
        configureSeekBarVolume();
        configureListenerEditTextHour();
        load();
    }

    /*UI*/
    //Toolbar
    void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_toolbar_add_alarm) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void displayListContactAlarm(){
        linearLayoutContainsContactAlarm.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        if(listContactAlarm.size() != 0){
            for (String contact : listContactAlarm){
                @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.item_contact_number_and_delete, null);
                TextView textView = view.findViewById(R.id.item_contact_number_and_delete_textview_name_and_number);
                String[] nameAndNumber = contact.split("/");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nameAndNumber[0]).append(" ").append(nameAndNumber[1]);
                textView.setText(stringBuilder);
                ImageView imageViewDelete = view.findViewById(R.id.item_contact_number_and_delete_imageview_delete);
                imageViewDelete.setOnClickListener(v -> {
                    View parent = (View) v.getParent();
                    deleteContactList( linearLayoutContainsContactAlarm.indexOfChild(parent), Keys.KEY_MOD_MESSAGE_ALARM);
                });
                linearLayoutContainsContactAlarm.addView(view);
            }
        }
    }


    private void displayListContactSos(){
        linearLayoutContainsContactSos.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        if(listContactSos.size() != 0){
            for (String contact : listContactSos){
                @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.item_contact_number_and_delete, null);
                TextView textView = view.findViewById(R.id.item_contact_number_and_delete_textview_name_and_number);
                String[] nameAndNumber = contact.split("/");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nameAndNumber[0]).append(" ").append(nameAndNumber[1]);
                textView.setText(stringBuilder);
                ImageView imageViewDelete = view.findViewById(R.id.item_contact_number_and_delete_imageview_delete);
                imageViewDelete.setOnClickListener(v -> {
                    View parent = (View) v.getParent();
                    deleteContactList( linearLayoutContainsContactSos.indexOfChild(parent), Keys.KEY_MOD_MESSAGE_SOS);
                });
                linearLayoutContainsContactSos.addView(view);
            }
        }
    }


    /*SAVE AND LOAD*/
    //Loading et set text
    private void load() {
        SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        editTextDelayForStop.setText(preferences.getString(Keys.KEY_TIMER_ALARM, null));
        editTextMessageContentAlarm.setText(preferences.getString(Keys.KEY_MESSAGE_CONTENT_ALARM, null));
        editTextMessageContentSos.setText(preferences.getString(Keys.KEY_MESSAGE_CONTENT_SOS, null));
        checkBoxPopUpConfirmSendSms.setChecked(preferences.getBoolean(Keys.KEY_POPUP_CONFIRM_SEND_SMS, false));
        if(preferences.getString(Keys.KEY_LIST_CONTACT_ALARM , null) != null)
            listContactAlarm = Utils.convertStringContactToList(Objects.requireNonNull(preferences.getString(Keys.KEY_LIST_CONTACT_ALARM, null))) ;
        if(preferences.getString(Keys.KEY_LIST_CONTACT_SOS , null) != null)
            listContactSos = Utils.convertStringContactToList(Objects.requireNonNull(preferences.getString(Keys.KEY_LIST_CONTACT_SOS, null)));
        seekBarVolume.setProgress(preferences.getInt(Keys.KEY_ALRM_VOLUME, 10) * 10);
        if(!preferences.getBoolean(Keys.KEY_TYPE_ALARM_VOICE, true))
            radioButtonAlarm.setChecked(true);
        toggleButtonFlash.setChecked(preferences.getBoolean(Keys.KEY_FLASH, true));
        toggleButtonVibrate.setChecked(preferences.getBoolean(Keys.KEY_VIBRATE, true));
        editTextHourMorning.setText(preferences.getString(Keys.KEY_HOUR_NOTIFICATION_MORNING, "08:00"));
        editTextHourMidday.setText(preferences.getString(Keys.KEY_HOUR_NOTIFICATION_MIDDAY, "12:00"));
        editTextHourEvening.setText(preferences.getString(Keys.KEY_HOUR_NOTIFICATION_EVENING, "19:00"));
        checkBoxNotificationTreatment.setChecked(preferences.getBoolean(Keys.KEY_NOTIFICATION_TREATMENT, true));

        displayListContactAlarm();
        displayListContactSos();
    }

    //Saving
    private void save() {
        if (!checkIfAllIsCorrect()) {
            Toast.makeText(this, "erreur de saisi", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
            preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT_ALARM, editTextMessageContentAlarm.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_TIMER_ALARM, editTextDelayForStop.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_LIST_CONTACT_SOS, Utils.convertListContactToString(listContactSos)).apply();
            preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT_SOS, editTextMessageContentSos.getText().toString()).apply();
            preferences.edit().putBoolean(Keys.KEY_POPUP_CONFIRM_SEND_SMS, checkBoxPopUpConfirmSendSms.isChecked()).apply();
            preferences.edit().putString(Keys.KEY_LIST_CONTACT_ALARM, Utils.convertListContactToString(listContactAlarm)).apply();
            preferences.edit().putInt(Keys.KEY_ALRM_VOLUME, seekBarVolume.getProgress()/10).apply();
            preferences.edit().putBoolean(Keys.KEY_TYPE_ALARM_VOICE, radioButtonVoice.isChecked()).apply();
            preferences.edit().putBoolean(Keys.KEY_FLASH, toggleButtonFlash.isChecked()).apply();
            preferences.edit().putBoolean(Keys.KEY_VIBRATE, toggleButtonVibrate.isChecked()).apply();
            preferences.edit().putString(Keys.KEY_HOUR_NOTIFICATION_MORNING, editTextHourMorning.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_HOUR_NOTIFICATION_MIDDAY, editTextHourMidday.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_HOUR_NOTIFICATION_EVENING, editTextHourEvening.getText().toString()).apply();
            preferences.edit().putBoolean(Keys.KEY_NOTIFICATION_TREATMENT, checkBoxNotificationTreatment.isChecked()).apply();
            configureNotificationState();
            finish();
        }
    }

    /*CONTACT*/

    void openContact(String type) {

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (type.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            startActivityForResult(contactPickerIntent, 1);
        }else{
            startActivityForResult(contactPickerIntent, 2);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name = "";

        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cur = getContentResolver().query(Objects.requireNonNull(contactData), null, null, null, null);
            if (Objects.requireNonNull(cur).getCount() > 0) {
                if (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                        while (Objects.requireNonNull(phones).moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            switch (requestCode){
                                case 1 : addContactList(phoneNumber, name, Keys.KEY_MOD_MESSAGE_ALARM);
                                    break;
                                case 2 : addContactList(phoneNumber, name,  Keys.KEY_MOD_MESSAGE_SOS);
                                    break;
                            }
                        }
                        phones.close();
                    }
                }
            }
            cur.close();
        }
        Toast.makeText(this,"phone number of " + name, Toast.LENGTH_SHORT).show();
    }

    private void addContactList(String number, String name, String type){
        if(type.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            listContactAlarm.add(name + "/" + number);
            displayListContactAlarm();
        }else{
            listContactSos.add(name + "/" + number);
            displayListContactSos();
        }
    }

    private void deleteContactList(int position, String type){
        if(type.equals(Keys.KEY_MOD_MESSAGE_ALARM)){
            listContactAlarm.remove(position);
            displayListContactAlarm();
        } else{
            listContactSos.remove(position);
            displayListContactSos();
        }
    }

    private void addCustomContactAlarm(){
        if(editTextPhoneNumberAlarm.getText().length() != 10){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        } else{
            addContactList(editTextPhoneNumberAlarm.getText().toString(), getResources().getString(R.string.no_name), Keys.KEY_MOD_MESSAGE_ALARM);
        }
    }

    private void addCustomContactSos(){
        if(editTextPhoneNumberSos.getText().length() != 10){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        } else{
            addContactList(editTextPhoneNumberSos.getText().toString(), getResources().getString(R.string.no_name), Keys.KEY_MOD_MESSAGE_SOS);
        }
    }

    /*UTILS*/
    //Check fields correct
    private boolean checkIfAllIsCorrect() {
        if (editTextMessageContentAlarm.getText().length() == 0)
            return false;
        if (editTextDelayForStop.getText().length() == 0)
            return false;
        if (editTextMessageContentSos.getText().length() == 0)
            return false;

        return listContactAlarm.size() != 0 && listContactSos.size() != 0;
    }

    private void configureSeekBarVolume(){
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarVolume.setProgress(moveCursorSeekBar(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private int moveCursorSeekBar(int progress){
        int cursor = progress/10;
        return cursor * 10 ;
    }

    private void configureListenerEditTextHour(){
        editTextHourMorning.setOnClickListener(v ->  openTimePickerDialog("morning"));
        editTextHourMidday.setOnClickListener(v -> openTimePickerDialog("midday"));
        editTextHourEvening.setOnClickListener(v -> openTimePickerDialog("evening"));
    }

    //Time picker dialog
    private void openTimePickerDialog(String id){

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            switch (id){
                case ("morning"): editTextHourMorning.setText(Utils.convertTimeIntToString(hourOfDay, minute)); break;
                case ("midday") : editTextHourMidday.setText(Utils.convertTimeIntToString(hourOfDay, minute)); break;
                case ("evening") : editTextHourEvening.setText(Utils.convertTimeIntToString(hourOfDay, minute)); break;
            }
        },0,0,true);
        timePickerDialog.show();
    }

    //Configur notification
    private void configureNotificationState(){
        if(!checkBoxNotificationTreatment.isChecked()){
            AlarmService.cancelAlarm(this, Keys.KEY_ALRMMANAGER_REQUEST_CODE_NOTIFICATION);
        } else{
            List<Date> lstHour = new ArrayList<>();
            lstHour.add(Utils.convertTimeStringToDate(editTextHourMorning.getText().toString()));
            lstHour.add(Utils.convertTimeStringToDate(editTextHourMidday.getText().toString()));
            lstHour.add(Utils.convertTimeStringToDate(editTextHourEvening.getText().toString()));

            AlarmService.configureAlarms(this, AlarmService.findNextAlarm(lstHour), Keys.KEY_ALRMMANAGER_REQUEST_CODE_NOTIFICATION );
        }
    }
}