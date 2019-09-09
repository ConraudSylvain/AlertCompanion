package com.sylvain.alertcompanion.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Treatment {
    @PrimaryKey (autoGenerate = true)
    private long id;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private boolean morning;
    @ColumnInfo
    private boolean midday;
    @ColumnInfo
    private boolean evening;
    @ColumnInfo
    private int dosageQuantity;
    @ColumnInfo
    private String dosageUnit;

    public Treatment(String name, boolean morning, boolean midday, boolean evening, int dosageQuantity, String dosageUnit) {
        this.name = name;
        this.morning = morning;
        this.midday = midday;
        this.evening = evening;
        this.dosageQuantity = dosageQuantity;
        this.dosageUnit = dosageUnit;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public void setMidday(boolean midday) {
        this.midday = midday;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }

    public String getName() {
        return name;
    }

    public boolean isMorning() {
        return morning;
    }

    public boolean isMidday() {
        return midday;
    }

    public boolean isEvening() {
        return evening;
    }

    public int getDosageQuantity() {
        return dosageQuantity;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }
}
