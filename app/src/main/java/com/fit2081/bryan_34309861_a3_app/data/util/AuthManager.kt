package com.fit2081.bryan_34309861_a3_app.data.util

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * A singleton object to manage user authentication state within the app.
 * Handles login, logout, and session persistence using SharedPreferences.
 */
object AuthManager {

    // Holds the current logged-in patient's ID as state
    private val _userId: MutableState<String?> = mutableStateOf(null)

    /**
     * Initializes the user session by reading the stored user ID from SharedPreferences.
     * This should be called on app start or when the session needs to be restored.
     *
     * @param context The application context required to access SharedPreferences.
     */
    fun initializeUserId(context: Context) {
        val sharedPref = context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
        val savedUserId = sharedPref.getString("currentSession", null)
        _userId.value = savedUserId
    }

    /**
     * Logs in a user by saving their user ID to SharedPreferences and updating the internal state.
     *
     * @param userId The ID of the user to log in.
     * @param context The application context required to access SharedPreferences.
     */
    fun login(userId: String, context: Context) {
        _userId.value = userId
        context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
            .edit()
            .apply {
                putString("currentSession", _userId.value)
                    .apply()
            }
    }

    /**
     * Logs out the current user by clearing their session from SharedPreferences and
     * resetting the internal state.
     *
     * @param context The application context required to access SharedPreferences.
     */
    fun logout(context: Context) {
        _userId.value = null
        context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
            .edit()
            .putString("currentSession", null)
            .apply()
    }

    /**
     * Returns the ID of the currently logged-in user.
     *
     * @return The user ID as a [String], or null if no user is logged in.
     */
    fun getPatientId(): String? { // wheres the fun
        return  _userId.value
    }
}