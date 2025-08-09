package com.fit2081.bryan_34309861_a3_app.ui.screens.HomeScreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import kotlinx.coroutines.launch

class HomeViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository = PatientRepository(context)

    /**
     * Current patient's ID in session
     */
    private val patientId = AuthManager.getPatientId()?: ""

    /**
     * Private mutable live data that stores the current patient in session.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _thePatient = MutableLiveData<Patient>()

    /**
     * Private mutable live data that determines the state of UI for getting the patient
     */
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state for fetching the patient
     */
    val uiState: LiveData<UiState>
        get() = _uiState

    /**
     * Initialize the ViewModel by loading the current patient in session from the repository
     * This ensures data is available as soon as the UI starts observing.
     */
    init {
        loadPatient()
    }

    /**
     * Loads the current patient by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the
     * most current data.
     */
    private fun loadPatient() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val patient = repository.getPatientById(patientId)
                _thePatient.value = patient
                _uiState.value = UiState.Success("Patient loaded")
            } catch (e:Exception) {
                _uiState.value = UiState.Error("Error loading patient: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Retrieves the total HEIFA score of the patient
     *
     * @return A Float value representing the total HEIFA score
     */
    fun getPatientTotalScore(): Float {
        return _thePatient.value?.totalScore?: 0f
    }

    /**
     * Retrieves the name of the patient
     *
     * @return A string representing the name
     */
    fun getPatientName(): String {
        return _thePatient.value?.name?: "User"
    }

    // Factory class for creating instances of HomeViewModel
    class HomeViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(context) as T
    }
}