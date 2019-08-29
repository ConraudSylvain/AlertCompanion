package com.sylvain.alertcompanion;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class StethoUtils extends Application {


        @Override
        public void onCreate() {
            super.onCreate();
            Stetho.initializeWithDefaults(this  );
        }

}
