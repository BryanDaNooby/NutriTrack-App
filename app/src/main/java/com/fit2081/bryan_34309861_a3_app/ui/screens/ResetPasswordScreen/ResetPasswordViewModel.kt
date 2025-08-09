package com.fit2081.bryan_34309861_a3_app.ui.screens.ResetPasswordScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ResetPasswordViewModel(context: Context) : ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository = PatientRepository(context)

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
     * Public mutable strings that serve as the placeholders
     */
    val patientIdPlaceholder = mutableStateOf("")
    val phoneNumberPlaceholder = mutableStateOf("")
    val passwordPlaceholder = mutableStateOf("")
    val confirmPasswordPlaceholder = mutableStateOf("")

    /**
     * Public mutable boolean that serves as the state of the dropdown menu
     */
    val expanded = mutableStateOf(false)

    /**
     * Public mutable booleans that serves as the visibility of the password
     */
    val passwordVisible = mutableStateOf(false)
    val confirmPasswordVisible = mutableStateOf(false)

    /**
     * Public mutable booleans that serve as the state of the patient being verified
     */
    val isVerified = mutableStateOf(false)

    /**
     * Derive state to hold the password requirement only if the password changes
     */
    val isLongEnough = derivedStateOf { passwordPlaceholder.value.length >= 8 }
    val hasUppercase = derivedStateOf { passwordPlaceholder.value.any { it.isUpperCase() } }
    val hasLowercase = derivedStateOf { passwordPlaceholder.value.any { it.isLowerCase() } }
    val hasDigit = derivedStateOf { passwordPlaceholder.value.any { it.isDigit() } }
    val noSpaces = derivedStateOf { !passwordPlaceholder.value.contains(" ") }
    val hasSpecialChar = derivedStateOf { passwordPlaceholder.value.any { it in "!@#\$%^&*()-_=+[]{};:'\",.<>?/\\|`~" } }

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
            _allPatients.value = repository.getAllPatients()
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
            _thePatient.value = repository.getPatientById(patientId)
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
     * Updates the patient's password
     *
     * @param password The password that the patient wants to set
     */
    private fun updatePatientPassword(password: String) {
        viewModelScope.launch {
            repository.updatePatientPassword(_thePatient.value!!, password)
            loadPatients()
        }
    }

    /**
     * Verifies if the patient provided information is same as in the database
     *
     * @param phoneNumber the phoneNumber of the patient
     * @param context The Android context used to show Toast messages.
     */
    fun verifyPatient(phoneNumber: String, context: Context): Boolean {
        return if (_thePatient.value?.phoneNumber != phoneNumber) {
            Toast.makeText(context, "Incorrect Phone Number", Toast.LENGTH_SHORT).show()
            false
        } else {
            Toast.makeText(context, "Successfully verified", Toast.LENGTH_SHORT).show()
            true
        }
    }

    /**
     * Returns a lambda function that resets the patient's password based on provided input validation.
     *
     * This function performs the following checks before updating the password:
     * - Checks if the new password and confirmation password match.
     * - If both checks pass, it hashes the new password using BCrypt and updates it in the database.
     *
     * Appropriate Toast messages are shown for each validation outcome.
     * On successful password reset, it navigates to the Patient Login screen.
     *
     * @param newPassword The new password to set.
     * @param newConfirmPassword Confirmation of the new password.
     * @param context The Android context used to show Toast messages.
     * @param navController The navigation controller used to navigate after password change.
     *
     * @return A lambda function to be triggered (e.g., on a button click) that handles the password reset process.
     */
    fun resetPassword(
        newPassword: String,
        newConfirmPassword: String,
        context: Context,
        navController: NavHostController
    ): () -> Unit {
        return {
            when {
                newPassword.isEmpty() || newConfirmPassword.isEmpty() -> {
                    Toast.makeText(context, "Please fill in your new password", Toast.LENGTH_SHORT).show()
                }
                newPassword != newConfirmPassword -> {
                    Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
                }

                newPassword.length < 8 -> {
                    Toast.makeText(context, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                }

                !newPassword.any { it.isUpperCase() } -> {
                    Toast.makeText(context, "Password must include at least one uppercase letter", Toast.LENGTH_SHORT).show()
                }

                !newPassword.any { it.isLowerCase() } -> {
                    Toast.makeText(context, "Password must include at least one lowercase letter", Toast.LENGTH_SHORT).show()
                }

                !newPassword.any { it.isDigit() } -> {
                    Toast.makeText(context, "Password must include at least one number", Toast.LENGTH_SHORT).show()
                }

                !newPassword.any { it in "!@#\$%^&*()-_=+[]{};:'\",.<>?/\\|`~" } -> {
                    Toast.makeText(context, "Password must include at least one special character", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val hashedPassword = MessageDigest.getInstance("SHA-256")
                        .digest(newPassword.toByteArray()).joinToString("") { "%02x".format(it) }
                    updatePatientPassword(hashedPassword)
                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(AppDashboardScreen.PatientLogin.route)
                }
            }
        }
    }

    // Factory class for creating instances of ResetPasswordViewModel
    class ResetPasswordViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ResetPasswordViewModel(context) as T
    }
}