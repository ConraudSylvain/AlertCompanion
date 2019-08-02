package com.sylvain.alertcompanion.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.controller.Permission;
import com.sylvain.alertcompanion.controller.SendSms;
import com.sylvain.alertcompanion.model.Keys;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.menu_drawer_navigation_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @OnClick(R.id.activity_main_button_sos)
    public void clickAlarmButton(){sendSmsSos();}

    static AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configureAll();

     // getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("save =" + getSharedPreferences(Keys.KEY_MAIN_SAVE,MODE_PRIVATE).getString(Keys.KEY_SAVE_ALARM_LIST, null)+ "end");

    }

    /*CONFIGURATION*/
    private void configureAll(){
        configureToolbar();
        configureDrawerlayout();
        Permission.permissionSms(this);
        Permission.permissionCall(this);
        Permission.permissionContact(this);
    }

    /*UI*/
    //toolbar
    void configureToolbar(){
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toolbar_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Menu drawer
    private void configureDrawerlayout(){
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id){
            case R.id.menu_drawer_button_alarm : startActivityAlarm();
                break;
            case R.id.menu_drawer_button_traitment:

                break;
            case R.id.menu_drawer_button_settings:startSettingsActivity();

                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;}

    /*SOS SMS*/
     //Send sms
    private void sendSmsSos(){
        displayDialogSmsStatus();
            SendSms.getInstance().configureAndSendSms(this, Keys.KEY_MOD_MESSAGE_SOS);
        }

        //Dialog sms status
    private void displayDialogSmsStatus(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("SMS sending...")
                .setPositiveButton("ok", (dialog, which) -> SendSms.getInstance().unregistredBroadcast()).setMessage("status : wait");
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Update dialog sms status
    public static void updateStatusSmsSendForDialog(String status){
        alertDialog.dismiss();
        alertDialog.setMessage("status : " + status);
        alertDialog.show();
    }

    /*Activity*/
    private void startActivityAlarm(){
        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
        startActivity(intent);
    }

    private void startSettingsActivity(){
        Intent intent = new Intent(MainActivity.this , SettingsActivity.class);
        startActivity(intent);
    }



}
