package com.fit2081.bryan_34309861_a3_app.data.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.database.FoodIntake
import com.fit2081.bryan_34309861_a3_app.data.repository.FoodIntakeRepository
import kotlinx.coroutines.launch

class FoodIntakeViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val foodIntakeRepo = FoodIntakeRepository(context)

    /**
     * Insert a new foodIntake into the database
     * the function uses viewModelScope to launch a coroutine scope
     * to run the suspend function
     *
     * @param foodIntake The [FoodIntake] object to be inserted
     */
    fun insertFoodIntake(foodIntake: FoodIntake) {
        viewModelScope.launch {
            foodIntakeRepo.insert(foodIntake)
        }
    }

    // factory class for creating instances of FoodIntakeViewModel
    class FoodIntakeViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FoodIntakeViewModel(context) as T
    }
}