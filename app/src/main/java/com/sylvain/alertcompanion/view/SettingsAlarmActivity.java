package com.sylvain.alertcompanion.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.model.Keys;

import java.security.Key;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAlarmActivity extends AppCompatActivity {

    @BindView(R.id.activity_settings_alarm_edittext_delay_for_stop)
    EditText editTextDelayForStop;
    @BindView(R.id.activity_settings_alarm_edittext_message_content)
    EditText editTextMessageContent;
    @BindView(R.id.activity_settings_alarm_edittext_phone_number)
    EditText editTextPhoneNumber;

    @OnClick(R.id.activity_settings_alarm_button_save)
    public void clickSave(){save();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_alarm);
        ButterKnife.bind(this);
        configureToolbar();
        load();
    }


    /*UI*/
    //Toolbar
    void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);}

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
            case R.id.menu_toolbar_add_alarm: ;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkIfAllIsCorrect(){
        if(editTextPhoneNumber.getText().length() != 10)
            return false;
        if (editTextMessageContent.getText().length() == 0)
            return false;
        if(editTextDelayForStop.getText().length() == 0)
            return false;
        return true;
    }

    private void load(){
        SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        editTextDelayForStop.setText(preferences.getString(Keys.KEY_TIMER_ALARM, null));
        editTextMessageContent.setText(preferences.getString(Keys.KEY_MESSAGE_CONTENT, null));
        editTextPhoneNumber.setText(preferences.getString(Keys.KEY_PHONE_NUMBER_CONTACT, null));

    }

    private void save(){
        if(!checkIfAllIsCorrect()){
            Toast.makeText(this, "erreur de saisi", Toast.LENGTH_SHORT).show();
        }else{
            SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
            preferences.edit().putString(Keys.KEY_PHONE_NUMBER_CONTACT, editTextPhoneNumber.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT, editTextMessageContent.getText().toString()).apply();
            preferences.edit().putString(Keys.KEY_TIMER_ALARM, editTextDelayForStop.getText().toString()).apply();
        }
    }
}
