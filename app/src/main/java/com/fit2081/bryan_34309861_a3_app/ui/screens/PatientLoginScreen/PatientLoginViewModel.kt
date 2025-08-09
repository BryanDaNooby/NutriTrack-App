package com.fit2081.bryan_34309861_a3_app.ui.screens.PatientLoginScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import kotlinx.coroutines.launch
import java.security.MessageDigest

class PatientLoginViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val patientRepository = PatientRepository(context)

    /**
     * Private mutable live data that stores the list of all patients
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _allPatients = MutableLiveData<List<Patient>>(emptyList())

    /**
     * Private mutable live data that stores the patient
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _thePatient = MutableLiveData<Patient>()

    /**
     * Public mutable strings that serves as placeholder
     */
    val patientIdPlaceholder = mutableStateOf("")
    val patientPasswordPlaceholder = mutableStateOf("")

    /**
     * Public mutable boolean that serves as the state of the visibility of the password
     */
    val passwordVisible = mutableStateOf(false)

    /**
     * Public mutable boolean that serves as the state of expanded dropdown menu
     */
    val expanded = mutableStateOf(false)

    /**
     * Initialize the ViewModel by loading the list of all patients from the repository
     * This ensures data is available as soon as the UI starts observing.
     */
    init {
        loadPatients()
    }

    /**
     * Loads all patients by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the
     * most current data.
     */
    private fun loadPatients() {
        viewModelScope.launch {
            _allPatients.value = patientRepository.getAllPatients()
        }
    }

    /**
     * Retrieve a patient from the database based on their ID.
     *
     * @param patientId The ID of the patient to retrieve.
     * @return The [Patient] object in the database
     */
    fun getPatientById(patientId: String): LiveData<Patient> {
        viewModelScope.launch {
            _thePatient.value = patientRepository.getPatientById(patientId)
        }
        return _thePatient
    }

    /**
     * Retrieve a list of patients that are registered
     *
     * @return A list of patients that are registered
     */
    fun getAllRegisteredPatient(): List<Patient> {
        return _allPatients.value?.filter { it.name.isNotEmpty() }?: emptyList()
    }

    /**
     * Authenticates a patient based on provided ID and password.
     *
     * Shows appropriate Toast messages for various validation outcomes and navigates to the
     * questionnaire screen if login is successful.
     *
     * If the patient have filled in the questionnaire before, it will navigate them to the
     * home screen else they will be sent to the questionnaire screen
     *
     * @param patientId The ID of the patient attempting to log in.
     * @param password The plaintext password entered by the user.
     * @param context The context used to display Toast messages.
     * @param navController The NavController used to navigate upon successful login.
     * @return A lambda function to be executed, typically as an onClick handler.
     */
    fun isAuthorized(
        patientId: String,
        password: String,
        context: Context,
        navController: NavHostController
    ): () -> Unit {
        // No patient selected
        if (patientId == "") {
            return { Toast.makeText(context, "No Patient ID selected", Toast.LENGTH_SHORT).show() }
        }
        // failed to retrieve a patient
        if (_thePatient.value == null) return {
            Toast.makeText(context,"Patient not in database", Toast.LENGTH_SHORT).show()
        }

        val hashedPassword = _thePatient.value?.patientPassword

        // if the patient does not have a password
        if (hashedPassword.isNullOrEmpty()) return {
            Toast.makeText(context, "Patient does not have a password", Toast.LENGTH_SHORT).show()
        }

        val hashedInput = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
        return if (hashedInput != hashedPassword) {
            {
                Toast.makeText(context, "Password is incorrect", Toast.LENGTH_SHORT).show()
            }
        } else {
            {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                AuthManager.login(patientId, context)
                val answered = context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
                    .getBoolean("filled_$patientId", false)
                if (answered) navController.navigate(AppDashboardScreen.Home.route)
                else navController.navigate(AppDashboardScreen.Questionnaire.route)
            }
        }
    }

    // Factory class for creating instances of PatientLoginViewModel
    class PatientLoginViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PatientLoginViewModel(context) as T
    }
}