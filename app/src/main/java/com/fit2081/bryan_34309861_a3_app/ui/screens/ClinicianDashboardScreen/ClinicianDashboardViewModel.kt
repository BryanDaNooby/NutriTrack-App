package com.fit2081.bryan_34309861_a3_app.ui.screens.ClinicianDashboardScreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.R
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import com.fit2081.bryan_34309861_a3_app.data.model.PatientsWithFoodIntake
import kotlinx.coroutines.launch

class ClinicianDashboardViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository = PatientRepository(context)

    /**
     * Private mutable live data that stores the all the data of the patient's score and food intake.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _allData = MutableLiveData<List<PatientsWithFoodIntake>>(emptyList())

    /**
     * Private mutable live data that stores the average male HEIFA score.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _maleScore = MutableLiveData<Float>()

    /**
     * Public immutable LiveData that exposes the average male HEIFA score to the observers
     *
     * This property enables the UI to react to changes in the maleScore data while
     * preventing direct mutation from outside this class.
     */
    val maleScore: LiveData<Float>
        get() = _maleScore

    /**
     * Private mutable live data that stores the average female HEIFA score.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _femaleScore = MutableLiveData<Float>()

    /**
     * Public immutable LiveData that exposes the average female HEIFA score to the observers
     *
     * This property enables the UI to react to changes in the femaleScore data while
     * preventing direct mutation from outside this class.
     */
    val femaleScore: LiveData<Float>
        get() = _femaleScore

    /**
     * Initialize the ViewModel by loading the data as well as calculating the average score
     * for both sex
     * This ensures data is available as soon as the UI starts observing
     */
    init {
        loadData()
        getAvgScore()
    }

    /**
     * Loads the data of the patient by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the
     * most current data.
     */
    private fun loadData() {
        viewModelScope.launch {
            _allData.value = repository.getAllData()
        }
    }

    /**
     * Loads the average scores for both sex by fetching the latest data
     * from the repository
     *
     * This method is responsible for loading the observed LiveData with the
     * most current data.
     */
    private fun getAvgScore() {
        viewModelScope.launch {
            val maleAvgScore = repository.getAvgScoreBySex("male")
            val femaleAvgScore = repository.getAvgScoreBySex("female")

            if (maleAvgScore != 0f && femaleAvgScore != 0f) {
                _maleScore.value = maleAvgScore
                _femaleScore.value = femaleAvgScore
            }
        }
    }

    /**
     * Function to initialize a table for prompting
     *
     * @return A string representation of the table
     */
    private fun getDataTable(): String {
        val header = """
        | Patient ID | Total Score | Discretionary Score | Vegetable Score | Fruits Score | Grains Score | Whole Grains Score | Meat Alternatives Score | Dairy Score | Alcohol Score | Water Score | Sugar Score | Saturated Fat Score | Unsaturated Fat Score | Checkboxes | Persona | Sleep Time | Eat Time | Wake Up Time |
        |------------|-------------|---------------------|-----------------|--------------|--------------|--------------------|-------------------------|-------------|---------------|-------------|-------------|---------------------|------------------------|------------|---------|------------|----------|---------------|
    """.trimMargin()

        val rows = _allData.value?.joinToString("\n") { it.toTableRow() } ?: ""

        return header + "\n" + rows
    }

    /**
     * Retrieve a list of persona with its description
     *
     * @param context the context of the application
     * @return A list of pairs(String, String) where the strings are the label
     * and the description
     */
    private fun getPersonaDescription(context: Context): List<Pair<String, String>> {
        return listOf(
            "Health Devotee" to context.getString(R.string.healthDevoteeDesc),
            "Mindful Eater" to context.getString(R.string.mindfulEaterDesc),
            "Wellness Striver" to context.getString(R.string.wellnessStriverDesc),
            "Balance Seeker" to context.getString(R.string.balanceSeekerDesc),
            "Health Procrastinator" to context.getString(R.string.healthProcrastinatorDesc),
            "Food Carefree" to context.getString(R.string.foodCarefreeDesc)
        )
    }

    /**
     * Function to structure the prompt to be given to the genAI
     *
     * @param context the context of the application
     * @return A string representation of the prompt
     */
    fun getPrompt(context: Context): String {
        val personaInfo = getPersonaDescription(context).toMap()
        return """
            Based on the data provided:
            ${getDataTable()}
            
            Info:
            - The Food Intake checkbox is in this order [Fruits, Vegetables, Grains, Red Meat, Seafood,
            Poultry, Fish, Eggs, Nuts/Seeds] 
            - The persona description is as below:
                - Health Devotee: ${personaInfo["Health Devotee"]}
                - Mindful Eater: ${personaInfo["Mindful Eater"]}
                - Wellness Striver: ${personaInfo["Wellness Striver"]}
                - Balance Seeker: ${personaInfo["Balance Seeker"]}
                - Health Procrastinator: ${personaInfo["Health Procrastinator"]}
                - Food Carefree: ${personaInfo["Food Carefree"]}
            - The times are in 24 hours format.
            
            Identify and describe 3 interesting patterns in the data in 1 to 2 sentence(s) and return the analysis in this format:
            1. <PatternPlaceholder>: <DescPlaceholder>
            
            2. <PatternPlaceholder>: <DescPlaceholder>
            
            3. <PatternPlaceholder>: <DescPlaceholder>
            
        """.trimIndent()
    }

    /**
     * Function to extract the content of the returned response from the genAI
     *
     * @param text The response from the genAI
     * @return A list of pairs(String, String) where the strings are the title and
     * the description
     */
    fun extractInsights(text: String): List<Pair<String, String>> {
        val regex = Regex("""\d+\.\s\*\*(.*?)\*\*[:：]?\s*(.*?)((?=\n\d+\.\s\*\*)|$)""", RegexOption.DOT_MATCHES_ALL)
        return regex.findAll(text).map {
            val title = it.groupValues[1].trim() // cleaned headline (no number, no asterisks)
            val description = it.groupValues[2].trim()
            title to description
        }.toList()
    }

    // Factory class for creating instances of ClinicianDashboardViewModel
    class ClinicianDashboardViewModelFactory(context: Context) : ViewModelProvider.Factory {
        val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ClinicianDashboardViewModel(context) as T
    }
}