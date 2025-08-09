package com.fit2081.bryan_34309861_a3_app.ui.screens.QuestionnaireScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.R
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.database.FoodIntake
import com.fit2081.bryan_34309861_a3_app.data.repository.FoodIntakeRepository
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import kotlinx.coroutines.launch

class QuestionnaireViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository = FoodIntakeRepository(context)

    /**
     * Patient's ID in the current session
     */
    private val patientId = AuthManager.getPatientId()?: ""

    /**
     * Private mutable live data that stores the foodIntake of the patient
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _foodIntake = MutableLiveData<FoodIntake>()

    /**
     * Public immutable LiveData that exposes the patient's foodIntake to the observers
     *
     * This property enables the UI to react to changes in the patient's foodIntake data while
     * preventing direct mutation from outside this class.
     */
    val foodIntake: LiveData<FoodIntake>
        get() = _foodIntake

    /**
     * Private mutable live data that determines the state of UI for getting the patient's
     * foodIntake
     */
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state for fetching the patient's
     * foodIntake
     */
    val uiState: LiveData<UiState>
        get() = _uiState

    /**
     * Public mutable placeholders that serves as placeholder for user inputs
     */
    val checkboxPlaceholder = mutableStateOf(mutableStateListOf(*Array(9) { false }))
    val personaPlaceholder = mutableStateOf("")
    val eatTimePlaceholder = mutableStateOf("")
    val sleepTimePlaceholder = mutableStateOf("")
    val wakeUpTimePlaceholder = mutableStateOf("")

    /**
     * Public mutable boolean that serves as the state of the dropdown menu
     */
    val personaExpanded = mutableStateOf(false)

    /**
     * Public mutable state list of array of 6 boolean values to keep track of
     * the modal that is been shown
     */
    val personaModalStates = mutableStateListOf(*Array(6) { false })

    val eatTimePickerState = mutableStateOf(false)
    val sleepTimePickerState = mutableStateOf(false)
    val wakeUpTimePickerState = mutableStateOf(false)

    /**
     * Initialize the ViewModel by loading the current patient's foodIntake from the repository
     * This ensures data is available as soon as the UI starts observing.
     */
    init {
        loadFoodIntake()
    }

    /**
     * Loads the current patient's foodIntake by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the most current data.
     */
    private fun loadFoodIntake() {
        viewModelScope.launch {
            try {
                val foodIntake = repository.getAllIntakesByPatientId(patientId)
                _foodIntake.value = foodIntake
                checkboxPlaceholder.value = foodIntake.checkboxes.toMutableStateList()
                personaPlaceholder.value = foodIntake.persona
                eatTimePlaceholder.value = foodIntake.eatTime
                sleepTimePlaceholder.value = foodIntake.sleepTime
                wakeUpTimePlaceholder.value = foodIntake.wakeUpTime
            } catch (e: Exception) {
                println(e.stackTrace)
            }
        }
    }

    /**
     * Gets the list of food categories
     *
     * @return A list of String (food categories)
     */
    fun getFoodCategories(): List<String> {
        return listOf(
            "Fruits", "Vegetables", "Grains", "Red Meat", "Seafood",
            "Poultry", "Fish", "Eggs", "Nuts/Seeds"
        )
    }

    /**
     * Gets the list of persona
     *
     * @return a list of String (persona)
     */
    fun getPersonaList(): List<String> {
        return listOf(
            "Health Devotee", "Mindful Eater", "Wellness Striver",
            "Balance Seeker", "Health Procrastinator", "Food Carefree"
        )
    }

    /**
     * Updates the checkbox in the foodIntake object
     *
     * @param foodIntake The foodIntake to be updated
     * @param checkboxes the checkboxes from the UI
     * @param index the index of the checkbox being changed
     */
    private fun updateCheckbox() {
        viewModelScope.launch {
            _foodIntake.value?. let { repository.updateFoodIntakeCheckbox(it, checkboxPlaceholder.value) }
            loadFoodIntake()
        }
    }

    /**
     * Updates the persona in the foodIntake object
     *
     * @param foodIntake The foodIntake to be updated
     * @param persona the persona from the UI
     */
    private fun updatePersona() {
        viewModelScope.launch {
            _foodIntake.value?.let { repository.updateFoodIntakePersona(it, personaPlaceholder.value) }
            loadFoodIntake()
        }
    }

    /**
     * Updates the times in the foodIntake object
     *
     * @param foodIntake The foodIntake to be updated
     * @param timeType the type of time being changed
     * @param time the value of the time
     */
    private fun updateTime() {
        viewModelScope.launch {
            _foodIntake.value?.let {
                repository.updateFoodIntakeTime(
                    it,
                    eatTimePlaceholder.value,
                    sleepTimePlaceholder.value,
                    wakeUpTimePlaceholder.value
                )
            }
            loadFoodIntake()
        }
    }

    /**
     * Returns persona information (image, description, and title) for a given index.
     *
     * Each persona represents a user archetype in the nutrition app. The function maps
     * the index to a specific `PersonaInfo` object containing:
     * - A drawable resource for the persona image
     * - A string resource ID for the persona description
     * - A title for the persona
     *
     * @param index The index of the persona (0 to 5).
     * @return A [PersonaInfo] object containing details of the selected persona.
     * @throws IllegalArgumentException if the index is out of the expected range.
     */
    fun getPersonaInfo(index: Int): PersonaInfo = when (index) {
        0 -> PersonaInfo(R.drawable.persona_1, R.string.healthDevoteeDesc, "Health Devotee")
        1 -> PersonaInfo(R.drawable.persona_2, R.string.mindfulEaterDesc, "Mindful Eater")
        2 -> PersonaInfo(R.drawable.persona_3, R.string.wellnessStriverDesc, "Wellness Striver")
        3 -> PersonaInfo(R.drawable.persona_4, R.string.balanceSeekerDesc, "Balance Seeker")
        4 -> PersonaInfo(R.drawable.persona_5, R.string.healthProcrastinatorDesc, "Health Procrastinator")
        5 -> PersonaInfo(R.drawable.persona_6, R.string.foodCarefreeDesc, "Food Carefree")
        else -> error("No image found")
    }

    /**
     * Validates the user's responses in the food intake questionnaire and provides feedback.
     *
     * This function performs the following checks:
     * 1. At least one food item (checkbox) is selected.
     * 2. A persona is selected.
     * 3. All time fields (sleep, eat, wake up) are filled in.
     * 4. Sleep, eat, and wake-up times are not all the same.
     * 5. Eating time is optimal, defined as:
     *    - At least 2 hours before sleep time, AND
     *    - Not during the user's sleep period (between sleep and wake-up).
     *
     * If all validations pass, a success message is shown and the user is navigated to the home screen.
     * If any validation fails, an appropriate error Toast is displayed.
     *
     * This function is typically used as an `onClick` listener for a submit button.
     *
     * @param context The context used for displaying Toast messages.
     * @param navController The NavHostController used for screen navigation upon successful submission.
     * @return A lambda function that should be invoked to perform the validation and possibly navigation.
     */
    fun validateQuestionnaire(context: Context, navController: NavHostController): () -> Unit {
        return {
            val checkboxesValid = checkboxPlaceholder.value.any { it }

            val personaValid = personaPlaceholder.value.isNotEmpty()

            val sleepTime = sleepTimePlaceholder.value
            val eatTime = eatTimePlaceholder.value
            val wakeUpTime = wakeUpTimePlaceholder.value


            val timeValid = sleepTime.isNotEmpty() &&
                    eatTime.isNotEmpty() &&
                    wakeUpTime.isNotEmpty()

            val timesDifferent = sleepTime != eatTime && sleepTime != wakeUpTime && eatTime != wakeUpTime
            val eatTimeOptimal = if (timeValid) {
                try {
                    val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                    val eat = java.time.LocalTime.parse(eatTime, timeFormatter)
                    val sleep = java.time.LocalTime.parse(sleepTime, timeFormatter)
                    val wake = java.time.LocalTime.parse(wakeUpTime, timeFormatter)

                    val twoHoursBeforeSleep = sleep.minusHours(2)

                    // Handle time crossing midnight
                    val isDuringSleep = if (sleep.isAfter(wake)) {
                        eat.isAfter(sleep) || eat.isBefore(wake)
                    } else {
                        eat.isAfter(sleep) && eat.isBefore(wake)
                    }

                    eat.isBefore(twoHoursBeforeSleep) && !isDuringSleep
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false
            }

            if (checkboxesValid && personaValid && timeValid && timesDifferent && eatTimeOptimal) {
                updateCheckbox()
                updatePersona()
                updateTime()
                Toast.makeText(context, "Questionnaire submitted", Toast.LENGTH_SHORT).show()
                navController.navigate(AppDashboardScreen.Home.route)
                context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("filled_$patientId", true)
                    .apply()
            } else {
                when {
                    !checkboxesValid -> Toast.makeText(context, "Choose at least one food", Toast.LENGTH_SHORT).show()
                    !personaValid -> Toast.makeText(context, "Choose one persona", Toast.LENGTH_SHORT).show()
                    !timeValid -> Toast.makeText(context, "Please fill in the time", Toast.LENGTH_SHORT).show()
                    !timesDifferent -> Toast.makeText(context, "Eat, Sleep and Wake Up time cannot be the same", Toast.LENGTH_SHORT).show()
                    !eatTimeOptimal -> Toast.makeText(context, "Eat at least 2 hours before sleep and not during sleep", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Factory class for creating instances of QuestionnaireViewModel
    class QuestionnaireViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            QuestionnaireViewModel(context) as T
    }
}