package com.fit2081.bryan_34309861_a3_app.ui.screens.ClinicianLoginScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen

class ClinicianLoginViewModel(context: Context): ViewModel() {
    /**
     * The predefined key for admin login
     */
    private val key: String = "dollar-entry-apples"

    /**
     * Public mutable string that serves as the placeholder for input key
     */
    val inputKey = mutableStateOf("")

    /**
     * Public mutable boolean that serves as the state the of visibility of the key
     */
    val keyVisible = mutableStateOf(false)

    /**
     * Validates the input key and navigates to the clinician dashboard if correct.
     * Otherwise, displays an error Toast.
     *
     * @param inputKey The key entered by the clinician.
     * @param navController The NavController used to navigate on successful login.
     * @return A lambda that performs the validation and navigation.
     */
    fun validateKey(
        context: Context,
        navController: NavHostController
    ): () -> Unit {
        return {
            if (inputKey.value == key) {
                navController.navigate(AppDashboardScreen.ClinicianDashboard.route)
            } else {
                Toast.makeText(context, "Incorrect key", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Factory class for creating instances of ClinicianLoginViewModel
    class ClinicianLoginViewModelFactory(context: Context) : ViewModelProvider.Factory {
        val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ClinicianLoginViewModel(context) as T
    }
}