package com.sylvain.alertcompanion.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Treatment.class}, version = 1, exportSchema = false)
public abstract class DatabaseTreatment extends RoomDatabase {

    private static volatile DatabaseTreatment INSTANCE;

    // --- DAO ---
    public abstract TreatmentDao treatmentDao();

    // --- INSTANCE ---
    public static DatabaseTreatment getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseTreatment.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                             DatabaseTreatment.class, "TreatmentDatabase.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}