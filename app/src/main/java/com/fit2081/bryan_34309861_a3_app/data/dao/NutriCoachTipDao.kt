package com.fit2081.bryan_34309861_a3_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit2081.bryan_34309861_a3_app.data.database.NutriCoachTip

@Dao
interface NutriCoachTipDao {
    /**
     * Inserts a new tip into the database
     *
     * @param nutriCoachTip The [NutriCoachTip] object to be
     * inserted in the database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(nutriCoachTip: NutriCoachTip)

    /**
     * Retrieve a list of nutriCoach tips based on the patientId
     * and sort it by descending order on the time added
     *
     * @param patientId the ID of the patient to retrieve
     * @return a list of [NutriCoachTip] objects
     */
    @Query("SELECT * FROM nutricoachtips WHERE patientId = :patientId " +
            "ORDER BY timeAdded DESC")
    suspend fun getTipsByPatientId(patientId: String): List<NutriCoachTip>
}