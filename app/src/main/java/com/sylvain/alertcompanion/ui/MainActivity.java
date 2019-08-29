package com.sylvain.alertcompanion.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
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
import com.sylvain.alertcompanion.utils.Permission;
import com.sylvain.alertcompanion.utils.SendSmsService;
import com.sylvain.alertcompanion.data.Keys;

import java.io.IOException;

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
    public void clickAlarmButton(){clickSosButton();}

    static AlertDialog alertDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configureAll();
        //startActivity(new Intent(this,StopAlarmActivity.class));
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
        Permission.permissionCamera(this);


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
            case 0:
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
            case R.id.menu_drawer_button_traitment: startTreatmentActivity();

                break;
            case R.id.menu_drawer_button_settings:startSettingsActivity();

                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;}


    private void clickSosButton(){
        SharedPreferences preferences = getSharedPreferences(Keys.KEY_MAIN_SAVE, MODE_PRIVATE);
        if(preferences.getBoolean(Keys.KEY_POPUP_CONFIRM_SEND_SMS, false)){
            displayAlertDialogConfirm();
        }else{
            sendSmsSos();
        }
    }


    /*SOS SMS*/
     //Send sms
    private void sendSmsSos(){
        displayDialogSmsStatus();
            SendSmsService.getInstance().configureAndSendSms(this, Keys.KEY_MOD_MESSAGE_SOS);
        }

        //Dialog sms status
    private void displayDialogSmsStatus(){

        String contacts = getSharedPreferences(Keys.KEY_MAIN_SAVE,MODE_PRIVATE).getString(Keys.KEY_LIST_CONTACT_SOS, null);
        String[] tabContact = contacts.split(",");
        int totalContact = tabContact.length;
        StringBuilder name = new StringBuilder();
        name.append("(");
        for (int i = 0 ; i < tabContact.length ; i++){
            String[] contact = tabContact[i].split("/");
            name.append(contact[0]);
            if (i != totalContact-1)
            name.append(", ");
        }
        name.append(")");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("SMS sending...")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setMessage("send " + totalContact + " sms" + "\n" + name.toString());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    /*UTILS*/
    private void displayAlertDialogConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Sms")
                .setMessage("are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSmsSos();
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    /*Activity*/
    private void startActivityAlarm(){
        startActivity(new Intent(MainActivity.this, AlarmActivity.class));
    }

    private void startSettingsActivity(){
        startActivity(new Intent(MainActivity.this , SettingsActivity.class));
    }

    private void startTreatmentActivity(){
        startActivity(new Intent(MainActivity.this, TreatmentActivity.class));
    }


    private void flashOn(){
        Camera cam = Camera.open();
        Camera.Parameters p = cam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        cam.setParameters(p);
        SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
        try {
            cam.setPreviewTexture(mPreviewTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cam.startPreview();
    }
}
