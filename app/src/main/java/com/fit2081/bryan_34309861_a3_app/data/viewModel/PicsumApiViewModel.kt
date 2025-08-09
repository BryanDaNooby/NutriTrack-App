package com.fit2081.bryan_34309861_a3_app.data.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.repository.PicsumApiRepository
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import kotlinx.coroutines.launch

class PicsumApiViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository = PicsumApiRepository(context)

    /**
     * Private mutable live data that stores the image URL from the API
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _imageUrl = MutableLiveData<String>()

    /**
     * Public immutable LiveData that exposes the current image URL from the API to observers
     *
     * This property enables the UI to react to changes in the image URL's data while preventing
     * direct mutation from outside this class
     */
    val imageUrl: LiveData<String> get() = _imageUrl

    /**
     * Private mutable live data that determines the state of UI for getting
     * the image URL from API
     */
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state for fetching
     * the image URL from API
     */
    val uiState: LiveData<UiState>
        get() = _uiState

    /**
     * Initialize the ViewModel by loading the current image URL from the repository
     * This ensures data is available as soon as the UI starts observing
     */
    init {
        loadImage()
    }

    /**
     * Loads the current image URL by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the most
     * current data
     */
    fun loadImage() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val imageUrlValue = repository.getRandomImage()
                if (imageUrlValue == null) {
                    _uiState.value = UiState.Error("No Image found")
                } else {
                    imageUrlValue.let { _imageUrl.value = it }
                    _uiState.value = UiState.Success("Image found")
                }
            } catch (e: Exception) {
                _uiState.value = e.localizedMessage?.let { UiState.Error(it) }
            }
        }
    }

    // Factory class for creating instances of PicsumApiViewModel
    class PicsumApiViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PicsumApiViewModel(context) as T
    }
}