package com.fit2081.bryan_34309861_a3_app.data.util

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial: UiState

    /**
     * Still loading
     */
    object Loading: UiState

    /**
     * The function have been successfully ran
     */
    data class Success(val outputText: String) : UiState

    /**
     * Error while running the function
     */
    data class Error(val errorMessage: String) : UiState
}