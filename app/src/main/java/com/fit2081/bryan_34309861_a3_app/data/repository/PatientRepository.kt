package com.fit2081.bryan_34309861_a3_app.data.repository

import android.content.Context
import com.fit2081.bryan_34309861_a3_app.data.database.AppDatabase
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.model.PatientsWithFoodIntake

class PatientRepository(context: Context) {

    // Get the PatientDao instance from the database
    private val patientDao = AppDatabase.getDatabase(context).patientDao()

    /**
     * Inserts a new patient into the database.
     *
     * @param patient The [Patient] object to be inserted
     */
    suspend fun insertPatient(patient: Patient) {
        patientDao.insert(patient)
    }

    /**
     * Updates the patient's password
     *
     * @param patient The patient to be updated
     * @param password The password that the patient set
     */
    suspend fun updatePatientPassword(patient: Patient, password: String) {
        patient.patientPassword = password
        patientDao.updatePatient(patient)
    }

    /**
     * Updates the patient's name
     *
     * @param patient The patient to be updated
     * @param name The name of the patient
     */
    suspend fun updatePatientName(patient: Patient, name: String) {
        patient.name = name
        patientDao.updatePatient(patient)
    }

    /**
     * Retrieve a patient from the database based on their ID.
     *
     * @param patientId The ID of the patient to retrieve.
     * @return The [Patient] object in the database
     */
    suspend fun getPatientById(patientId: String): Patient =
        patientDao.getPatientById(patientId)

    /**
     * Retrieves a list of all patients in the database
     *
     * @return A list of all [Patient] objects
     */
    suspend fun getAllPatients(): List<Patient> {
        return patientDao.getAllPatients()
    }

    /**
     * Retrieve the average HEIFA scores of the sex
     *
     * @param sex The sex that is going to pass to the query
     * @return A Float value representing the average score of the sex
     */
    suspend fun getAvgScoreBySex(sex: String): Float {
        return patientDao.getAvgScoreBySex(sex)
    }

    /**
     * Retrieves a list of patients along with their food intake
     *
     * @return A list of [PatientsWithFoodIntake] objects.
     */
    suspend fun getAllData(): List<PatientsWithFoodIntake> {
        return patientDao.getAllData()
    }
}