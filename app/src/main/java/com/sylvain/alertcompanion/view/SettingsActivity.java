package com.sylvain.alertcompanion.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.model.Keys;

import java.security.Key;
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

    @OnClick(R.id.activity_settings_alarm_button_save)
    public void clickSave() {
        save();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_alarm);
        ButterKnife.bind(this);
        configureToolbar();
        load();
        test();
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
        //menu.findItem(R.id.menu_toolbar_add_alarm).setVisible(true);
        //menu.findItem(R.id.menu_toolbar_settings_alarm).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toolbar_add_alarm:
                ;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*UTILS*/
    //Check fields correct
    private boolean checkIfAllIsCorrect() {
        if (editTextPhoneNumberAlarm.getText().length() != 10)
            return false;
        if (editTextMessageContentAlarm.getText().length() == 0)
            return false;
        if (editTextDelayForStop.getText().length() == 0)
            return false;
        if (editTextMessageContentSos.getText().length() == 0)
            return false;
        if (editTextPhoneNumberSos.getText().length() != 10)
            return false;
        return true;
    }

    //Loading et set text
    private void load() {
        SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        editTextDelayForStop.setText(preferences.getString(Keys.KEY_TIMER_ALARM, null));
        editTextMessageContentAlarm.setText(preferences.getString(Keys.KEY_MESSAGE_CONTENT_ALARM, null));
        editTextPhoneNumberAlarm.setText(preferences.getString(Keys.KEY_PHONE_NUMBER_CONTACT_ALARM, null));
        editTextPhoneNumberSos.setText(preferences.getString(Keys.KEY_PHONE_NUMBER_CONTACT_SOS, null));
        editTextMessageContentSos.setText(preferences.getString(Keys.KEY_MESSAGE_CONTENT_SOS, null));

    }

    //Saving
    private void save() {
        if (!checkIfAllIsCorrect()) {
            Toast.makeText(this, "erreur de saisi", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
            preferences.edit().putString(Keys.KEY_PHONE_NUMBER_CONTACT_ALARM, editTextPhoneNumberAlarm.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT_ALARM, editTextMessageContentAlarm.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_TIMER_ALARM, editTextDelayForStop.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_PHONE_NUMBER_CONTACT_SOS, editTextPhoneNumberSos.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT_SOS, editTextMessageContentSos.getText().toString()).apply();
            finish();
        }
    }

    private void callPhone() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0685347922"));
        startActivity(callIntent);
    }

    void test() {
        ImageView phoneContactsButttonAlarm = findViewById(R.id.activity_settings_alarm_button_contact_alarm);
        phoneContactsButttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 1);

            }
        });

        ImageView phoneContactsButttonSos = findViewById(R.id.activity_settings_alarm_button_contact_sos);
        phoneContactsButttonSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, 2);

            }
        });

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
                                        case 1 :editTextPhoneNumberAlarm.setText(phoneNumber); break;
                                        case 2 : editTextPhoneNumberSos.setText(phoneNumber); break;
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
}