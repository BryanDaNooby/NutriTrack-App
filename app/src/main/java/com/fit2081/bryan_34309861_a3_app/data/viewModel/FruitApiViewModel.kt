package com.fit2081.bryan_34309861_a3_app.data.viewModel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.model.Fruit
import com.fit2081.bryan_34309861_a3_app.data.repository.FruitApiRepository
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import kotlinx.coroutines.launch

class FruitApiViewModel(context: Context): ViewModel() {
    /**
     * Repositories instance for handling all data operations.
     * This is the single point of contact for the ViewModels to interact with data sources.
     */
    private val fruitApiRepository = FruitApiRepository(context)
    private val patientRepository = PatientRepository(context)

    /**
     * Patient's ID in the current session
     */
    private val patientId = AuthManager.getPatientId()?: ""

    /**
     * Private mutable live data that stores the patient
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _thePatient = MutableLiveData<Patient>()

    /**
     * Private mutable live data that determines the state of UI for getting the patient
     */
    private val _patientUiState =  MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state for fetching the patient
     */
    val patientUiState: LiveData<UiState>
        get() = _patientUiState

    /**
     * Private mutable live data that stores the fruit from the API
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _apiFruit = MutableLiveData<Fruit>()

    /**
     * Private mutable live data that determines the state of UI for getting the fruit from API
     */
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state for fetching the fruit from API
     */
    val uiState: LiveData<UiState>
        get() = _uiState

    /**
     * Public mutable string that serves as the placeholder for fruitName
     */
    val fruitName = mutableStateOf("")

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
            // Loading when run
            _patientUiState.value = UiState.Loading
            val patient = patientRepository.getPatientById(patientId)
            _thePatient.value = patient
            // Patient is found
            _patientUiState.value = UiState.Success("Fetch successfully")
        }
    }

    /**
     * Checks whether the current patient's fruit score is optimal
     *
     * @return true is the fruit score is greater than 5
     */
    fun isFruitScoreOptimal() : Boolean {
        val fruitServeSize = _thePatient.value?.fruitServeSize?: 0f
        val fruitVarietyScore = _thePatient.value?.fruitVarietyScore?: 0f

        if (fruitServeSize >= 1f && fruitVarietyScore >= 2.5f) {
            return true
        }
        return false
    }

    /**
     * Retrieves the api fruit's detail by the name given
     *
     * @param fruitName The name of fruit wanted to be retrieved
     */
    fun getFruitDetailByName() {
        viewModelScope.launch {
            // Loading while running
            _uiState.value = UiState.Loading
            try {
                val fruit = fruitApiRepository.getFruitDetailsByName(fruitName.value)
                // If the fruit exists
                if (fruit.name != "") {
                    _apiFruit.value = fruit
                    _uiState.value = UiState.Success("Fruit found")
                } else {
                    _uiState.value = UiState.Error("Fruit not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error finding fruit: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Retrieve a the fruit details in a pair form
     *
     * @return A list of pairs containing the label and the content
     */
    fun getFruitDetailsMap(): List<Pair<String, String>> {
        return listOf(
            "Name" to (_apiFruit.value?.name?: ""),
            "Family" to (_apiFruit.value?.family?: ""),
            "Genus" to (_apiFruit.value?.genus?: ""),
            "Order" to (_apiFruit.value?.order?: ""),
            "Calories" to "${_apiFruit.value?.nutritions?.calories}",
            "Sugar" to "${_apiFruit.value?.nutritions?.sugar}",
            "Carbohydrates" to "${_apiFruit.value?.nutritions?.carbohydrates}",
            "Protein" to "${_apiFruit.value?.nutritions?.protein}",
            "Fat" to "${_apiFruit.value?.nutritions?.fat}"
        )
    }

    /**
     * Decides whether the button is enabled or disabled
     *
     * @return A boolean value to determine it is enable or disabled
     */
    fun buttonEnable(): Boolean {
        return fruitName.value.isNotEmpty() && _uiState.value !is UiState.Loading
    }

    // Factory class for creating instances of FruitApiViewModel
    class FruitApiViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FruitApiViewModel(context) as T
    }
}