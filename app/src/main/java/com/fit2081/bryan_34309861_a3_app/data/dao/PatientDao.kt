package com.fit2081.bryan_34309861_a3_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.model.PatientsWithFoodIntake

@Dao
interface PatientDao {
    /**
     * Inserts a new patient into the database.
     *
     * @param patient The [Patient] object to be inserted
     */
    @Insert
    suspend fun insert(patient: Patient)

    /**
     * Updates the patient in the database
     *
     * @param patient The [Patient] to be updated
     */
    @Update
    suspend fun updatePatient(patient: Patient)

    /**
     * Retrieves a list of patients that is sorted base on the patient ID
     *
     * @return A list of all [Patient] objects that is sorted by patient ID
     * in ascending order
     */
    @Query("SELECT * FROM patients ORDER BY CAST(patientId AS INTEGER) ASC")
    suspend fun getAllPatients(): List<Patient>

    /**
     * Retrieve a patient from the database based on their ID.
     *
     * @param patientId The ID of the patient to retrieve.
     * @return The [Patient] object in the database
     */
    @Query("SELECT * FROM patients WHERE patientId = :patientId")
    suspend fun getPatientById(patientId: String): Patient

    /**
     * Retrieve the average HEIFA scores of the sex
     *
     * @param sex The sex that is going to pass to the query
     * @return A Float value representing the average score of the sex
     */
    @Query("SELECT AVG(totalScore) AS avgScore FROM patients WHERE upper(sex) = upper(:sex)")
    suspend fun getAvgScoreBySex(sex: String): Float

    /**
     * Retrieves a list of patients along with their food intake
     *
     * This query performs on inner join between the 'patients'
     * and 'food_intake' tables and gets all the necessary information
     * @return A list of [PatientsWithFoodIntake] objects.
     * Each objet contains the
     * patient's ID, all the scores, the food intakes, persona and
     * the eat, sleep and wake up time.
     */
    @Query("""
        SELECT
            patients.patientId, totalScore, discretionaryScore, vegetableScore, fruitsScore, 
            grainsScore, wholeGrainsScore, meatAlternativesScore, dairyScore, sodiumScore, 
            alcoholScore, waterScore, sugarScore, saturatedFatScore, unsaturatedFatScore, 
            checkboxes, persona, sleepTime, eatTime, wakeUpTime 
            FROM patients INNER JOIN food_intake ON patients.patientId = food_intake.patientId
    """)
    suspend fun getAllData(): List<PatientsWithFoodIntake>
}