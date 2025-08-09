package com.fit2081.bryan_34309861_a3_app.data.repository

import android.content.Context
import com.fit2081.bryan_34309861_a3_app.data.database.AppDatabase
import com.fit2081.bryan_34309861_a3_app.data.database.NutriCoachTip

class NutriCoachTipRepository(context: Context) {

    // Get the NutriCoachTipDao instance from the database
    val nutriCoachTipDao = AppDatabase.getDatabase(context).nutriCoachDao()

    /**
     * Inserts a new tip into the database
     *
     * @param nutriCoachTip The [NutriCoachTip] object to be
     * inserted in the database
     */
    suspend fun insertTip(tip: NutriCoachTip) {
        nutriCoachTipDao.insertTip(tip)
    }

    /**
     * Retrieve a list of nutriCoach tips based on the patientId
     * and sort it by descending order on the time added
     *
     * @param patientId the ID of the patient to retrieve
     * @return a list of [NutriCoachTip] objects
     */
    suspend fun getTipsByPatientId(patientId: String): List<NutriCoachTip> {
        return nutriCoachTipDao.getTipsByPatientId(patientId)
    }
}

