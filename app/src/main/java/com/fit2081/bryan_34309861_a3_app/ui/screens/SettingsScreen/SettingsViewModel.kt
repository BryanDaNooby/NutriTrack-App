package com.fit2081.bryan_34309861_a3_app.ui.screens.SettingsScreen

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

class SettingsViewModel(context: Context): ViewModel() {
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
     * Public immutable LiveData that exposes the current patient to the observers
     *
     * This property enables the UI to react to changes in the current patient's data while
     * preventing direct mutation from outside this class.
     */
    val thePatient: LiveData<Patient>
        get() = _thePatient

    /**
     * Public mutable state of boolean that serves as the state of the modal
     */
    val showModal = mutableStateOf(false)

    /**
     * Public mutable string that serves as the placeholder of the input name
     */
    val newName = mutableStateOf("")

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
            val patient = repository.getPatientById(patientId)
            _thePatient.value = patient
        }
    }

    /**
     * Returns a lambda that logs the current user out and navigates back to the Patient Login screen.
     *
     * This function:
     * - Clears the current session by calling `AuthManager.logout`.
     * - Navigates to the Patient Login screen via the provided `NavHostController`.
     *
     * Typically used as the onClick handler for a "Logout" button.
     *
     * @param navController The navigation controller used to navigate to the login screen.
     * @param context The context required by `AuthManager` for accessing shared preferences.
     * @return A lambda function that performs logout and navigation when invoked.
     */
    fun logout(navController: NavHostController, context: Context): () -> Unit {
        return {
            AuthManager.logout(context)
            navController.navigate(AppDashboardScreen.PatientLogin.route)
        }
    }

    /**
     * Returns a lambda that navigates the user to the Clinician Login screen.
     *
     * This function is typically used to redirect users who want to log in as a clinician.
     * It performs a single action: navigating to `AppDashboardScreen.ClinicianLogin.route`.
     *
     * @param navController The navigation controller used to perform the screen transition.
     * @return A lambda function that navigates to the Clinician Login screen when invoked.
     */
    fun clinicianLogin(navController: NavHostController): () -> Unit {
        return {
            navController.navigate(AppDashboardScreen.ClinicianLogin.route)
        }
    }

    /**
     * This function updates the shared preferences to retain the mode the user used
     * before re-running the app
     *
     * @param context The current context of the application
     * @param isDark The current mode of the screen theme
     */
    fun toggleDarkMode(context: Context, isDark: Boolean) {
        context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("darkMode", isDark)
            .apply()
    }

    /**
     * Updates the patient's name
     */
    private fun updatePatientName(name: String) {
        viewModelScope.launch {
            val formattedName = formatName(name)
            repository.updatePatientName(_thePatient.value!!, formattedName)
            loadPatient()
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
     * Validates the patient's new name during profile update.
     *
     * This function performs the following validations:
     * - Ensures the name is not blank.
     * - Checks that the name is at least 2 characters long.
     * - Ensures the name only contains letters, spaces, hyphens, or apostrophes.
     *
     * On successful validation:
     * - Updates the patient's name in the database.
     * - Closes the modal.
     * - Displays a success message.
     * - Navigates to the Settings screen.
     *
     * @param context The Android [Context] used to show Toast messages.
     * @param navController The [NavHostController] used for navigation.
     */
    fun validateName(
        context: Context,
        navController: NavHostController
    ) {
        val name = newName.value
        return when {
            name.isBlank() -> {
                Toast.makeText(context, "Name cannot be blank", Toast.LENGTH_SHORT)
                    .show()
            }
            name.length < 2 -> {
                Toast.makeText(context, "Name must be at least length 2", Toast.LENGTH_SHORT)
                    .show()
            }
            !name.all {
                it.isLetter() || it == ' ' || it == '-' || it == '\''
            } -> {
                Toast.makeText(
                    context,
                    "Name can only consist of letters, space, hyphens and apostrophes",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                updatePatientName(name)
                showModal.value = false
                Toast.makeText(context, "Name successfully changed", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate(AppDashboardScreen.Settings.route)
            }
        }
    }


    // Factory class for creating instances of HomeViewModel
    class SettingViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(context) as T
    }
}