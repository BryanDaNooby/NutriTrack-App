package com.fit2081.bryan_34309861_a3_app.ui.screens.RegisterScreen

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

class RegisterViewModel(context: Context): ViewModel() {
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
     * Public mutable strings that serve as placeholders
     */
    val patientIdPlaceholder = mutableStateOf("")
    val phoneNumberPlaceholder = mutableStateOf("")
    val patientNamePlaceholder = mutableStateOf("")
    val passwordPlaceholder = mutableStateOf("")
    val confirmPasswordPlaceholder = mutableStateOf("")

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
    fun getAllUnregisteredPatient(): List<Patient> {
        return _allPatients.value?.filter { it.name.isEmpty() }?: emptyList()
    }

    /**
     * Updates the patient's name and password
     *
     * @param name The name of the patient
     * @param password The password that the patient set
     */
    private fun updatePatientDetails(name: String, password: String) {
        viewModelScope.launch {
            repository.updatePatientName(_thePatient.value!!, name)
            repository.updatePatientPassword(_thePatient.value!!, password)
            loadPatients()
        }
    }

    /**
     * Verifies if the patient-provided phone number matches the record in the database,
     * and whether the patient is eligible for registration.
     *
     * The verification checks the following:
     * 1. If a patient is selected (`_thePatient` is not null).
     * 2. If the patient is not already registered (password is blank).
     * 3. If the provided phone number matches the patient's phone number.
     *
     * Shows appropriate Toast messages based on each condition.
     *
     * @param phoneNumber The phone number input by the user.
     * @param context The Android [Context] used to display Toast messages.
     * @return `true` if the verification passes, `false` otherwise.
     */
    fun verifyPatient(phoneNumber: String, context: Context): Boolean {
        return when {
            _thePatient.value == null -> {
                Toast.makeText(context, "Please select a valid Patient ID", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            _thePatient.value?.patientPassword != "" -> {
                Toast.makeText(context, "Patient is already registered", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            _thePatient.value?.phoneNumber != phoneNumber -> {
                Toast.makeText(context, "Incorrect Phone Number", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                Toast.makeText(context, "Successfully verified", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    /**
     * Formats a full name by removing extra spaces and ensuring
     * only a single space separates each word.
     *
     * @param name The raw input name string.
     * @return The formatted name.
     */
    private fun formatName(name: String): String {
        return name.trim().split(Regex("\\s+")).joinToString(" ")
    }

    /**
     * Validates patient input during registration.
     *
     * The validation checks the following:
     * - Name is not blank, is at least 2 characters, and contains only valid characters.
     * - Password is not empty, contains no spaces, and matches the confirmation.
     *
     * On successful validation:
     * - Hashes the password using SHA-256.
     * - Formats the name properly.
     * - Updates the patient record.
     * - Navigates to the login screen and shows a success message.
     *
     * @param patientName The name entered by the user.
     * @param password The password entered by the user.
     * @param confirmPassword The confirmation of the password.
     * @param context The Android [Context] used to show Toast messages.
     * @param navController The [NavHostController] to navigate between screens.
     * @return A lambda function that performs the validation when invoked.
     */
    fun validatePatient(
        patientName: String,
        password: String,
        confirmPassword: String,
        context: Context,
        navController: NavHostController
    ): () -> Unit {
        return {
            when {
                patientName.isBlank() -> {
                    Toast.makeText(context, "Name cannot be blank", Toast.LENGTH_SHORT)
                        .show()
                }
                patientName.length < 2 -> {
                    Toast.makeText(context, "Name must be at least length 2", Toast.LENGTH_SHORT)
                        .show()
                }
                !patientName.all {
                    it.isLetter() || it == ' ' || it == '-' || it == '\''
                } -> {
                    Toast.makeText(
                        context,
                        "Name can only consist of letters, space, hyphens and apostrophes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(context, "Password field cannot be empty", Toast.LENGTH_SHORT).show()
                }

                password.contains(" ") -> {
                    Toast.makeText(context, "Password cannot contain any spaces", Toast.LENGTH_SHORT).show()
                }

                password.length < 8 -> {
                    Toast.makeText(context, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                }

                !password.any { it.isUpperCase() } -> {
                    Toast.makeText(context, "Password must include at least one uppercase letter", Toast.LENGTH_SHORT).show()
                }

                !password.any { it.isLowerCase() } -> {
                    Toast.makeText(context, "Password must include at least one lowercase letter", Toast.LENGTH_SHORT).show()
                }

                !password.any { it.isDigit() } -> {
                    Toast.makeText(context, "Password must include at least one number", Toast.LENGTH_SHORT).show()
                }

                !password.any { it in "!@#\$%^&*()-_=+[]{};:'\",.<>?/\\|`~" } -> {
                    Toast.makeText(context, "Password must include at least one special character", Toast.LENGTH_SHORT).show()
                }

                password != confirmPassword -> {
                    Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val hashedPassword = MessageDigest.getInstance("SHA-256")
                        .digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
                    val formattedName = formatName(patientName)
                    updatePatientDetails(formattedName, hashedPassword)
                    Toast.makeText(context, "Patient successfully registered", Toast.LENGTH_SHORT).show()
                    navController.navigate(AppDashboardScreen.PatientLogin.route)
                }
            }
        }
    }

    // Factory class for creating instances of RegisterViewModel
    class RegisterViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RegisterViewModel(context) as T
    }
}