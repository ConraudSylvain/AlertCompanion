package com.sylvain.alertcompanion.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sylvain.alertcompanion.services.AlarmService;
import com.sylvain.alertcompanion.utils.Keys;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.utils.Converter;
import com.sylvain.alertcompanion.utils.UtilsAlertDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends AppCompatActivity {

    @BindView(R.id.activity_alarm_scrollview_linearlayout)
    LinearLayout scrollViewAlarmLinearLayout;

    List<Date> alarmList = new ArrayList<>();
    SimpleDateFormat dateFormatTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        configureAll();
    }

    /*CONFIGURATION*/
    @SuppressLint("SimpleDateFormat")
    private void configureAll(){
        dateFormatTime = new SimpleDateFormat("HH:mm");
        configureToolbar();
        loadAlarmList();
        displayAlarms();
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
        menu.findItem(R.id.menu_toolbar_add_alarm).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_toolbar_add_alarm) {
            openTimePickerDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*ALARM*/
    //Display
    @SuppressLint("InflateParams")
    private void displayAlarms(){
        LayoutInflater layoutInflater = getLayoutInflater();
        scrollViewAlarmLinearLayout.removeAllViews();
        for (Date alarm : alarmList){
            View view = layoutInflater.inflate(R.layout.item_clock_display, null);
            TextView textViewAlarm = view.findViewById(R.id.item_clock_display_textview_clock);
            textViewAlarm.setText(dateFormatTime.format(alarm));
            view.setOnClickListener(v -> {
                int position = scrollViewAlarmLinearLayout.indexOfChild(v);
                UtilsAlertDialog.displayAlertDialogConfirmDeleteAlarm(AlarmActivity.this, position);
            });
            scrollViewAlarmLinearLayout.addView(view);
        }
    }

    //Delete
    public void deleteAlarm (int position){
        alarmList.remove(position);
        displayAlarms();
        configureAlarm();
        saveAlarmList();
    }

    //Add alarm to the list
    private void addAlarmToListAlarm(Date alarm) {
        alarmList.add(alarm);
        Collections.sort(alarmList);
        configureAlarm();
        saveAlarmList();
    }

    //Configure next alarm
    private void configureAlarm(){
        if(alarmList == null || alarmList.size() == 0){
            AlarmService.cancelAlarm(this, Keys.KEY_ALRMMANAGER_REQUEST_CODE_ALARM);
        }else{
            AlarmService.configureAlarms(this, AlarmService.findNextAlarm(alarmList), Keys.KEY_ALRMMANAGER_REQUEST_CODE_ALARM);
        }

    }

    /*UTILS*/
    //Time picker dialog
    private void openTimePickerDialog(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            addAlarmToListAlarm(Converter.convertTimeStringToDate(Converter.convertTimeIntToString(hourOfDay, minute)));
            displayAlarms();

        },0,0,true);
        timePickerDialog.show();
    }


    //Save
    private void saveAlarmList(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Date alarm : alarmList){
            stringBuilder.append(dateFormatTime.format(alarm));
            stringBuilder.append(",");
        }
        getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE).edit().putString(Keys.KEY_SAVE_ALARM_LIST, stringBuilder.toString()).apply();
    }

    //Load
    private void loadAlarmList(){
        alarmList.clear();
        alarmList = AlarmService.getAlarmList(this);
    }


}
