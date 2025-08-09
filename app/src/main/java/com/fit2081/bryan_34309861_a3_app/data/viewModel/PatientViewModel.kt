package com.fit2081.bryan_34309861_a3_app.data.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import kotlinx.coroutines.launch

class PatientViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    val patientRepo = PatientRepository(context)

    /**
     * Inserts a new patient into the database.
     *
     * @param patient The [Patient] object to be inserted
     */
    fun insertPatient(patient: Patient) {
        viewModelScope.launch {
            patientRepo.insertPatient(patient)
        }
    }

    // Factory class for creating instances of PatientViewModel
    class PatientViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PatientViewModel(context) as T
    }
}