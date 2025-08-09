package com.fit2081.bryan_34309861_a3_app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.BuildConfig
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GenAIViewModel : ViewModel() {
    /**
     * Generative Model: gemini-1.5-flash
     */
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    /**
     * Private mutable live data that determines the state of UI while getting
     * response from the genAI
     */
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state while getting
     * response from the genAI
     */
    val uiState: LiveData<UiState>
        get() = _uiState

    /**
     * Sends a prompt to the genAI model and gets a response from the model
     *
     * @param prompt A string to be sent to the genAI
     */
    fun sendPrompt(
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.postValue(UiState.Success(outputContent))
                }
            } catch (e: Exception) {
                _uiState.postValue(UiState.Error("Error: ${e.localizedMessage}"))  // <-- And here
            }
        }
    }
}