package com.sylvain.alertcompanion.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sylvain.alertcompanion.data.entities.Treatment;

import java.util.List;

@Dao
public interface TreatmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createTreatment (Treatment treatment);

    @Query("DELETE FROM Treatment WHERE id =:id_treatment")
    void deleteTreatment(long id_treatment);

    @Query("SELECT * FROM Treatment WHERE morning = 1")
    List<Treatment> getListTreatmentMorning();

    @Query("SELECT * FROM Treatment WHERE midday = 1")
    List<Treatment> getListTreatmentMidday();

    @Query("SELECT * FROM Treatment WHERE evening = 1")
    List<Treatment> getListTreatmentEvening();

    @Query("DELETE FROM Treatment")
    void deleteAllTreatment();

    @Query("SELECT * FROM Treatment WHERE id =:id_treatment")
    Treatment getTreatment(long id_treatment);

    @Update
    void updateTreatment(Treatment treatment);

}
