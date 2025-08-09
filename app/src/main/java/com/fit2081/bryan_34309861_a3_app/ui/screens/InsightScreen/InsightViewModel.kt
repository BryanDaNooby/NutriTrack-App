package com.fit2081.bryan_34309861_a3_app.ui.screens.InsightScreen

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import kotlinx.coroutines.launch

class InsightViewModel(context: Context): ViewModel() {
    /**
     * Repository instance for handling all data operations.
     * This is the single point of contact for the ViewModel to interact with data sources.
     */
    private val repository = PatientRepository(context)

    /**
     * Patient's ID in the current session
     */
    private val patientId = AuthManager.getPatientId()?: ""

    /**
     * Private mutable live data that stores the patient
     * Using LiveData provides a way to observe changes to the data in real time
     */
    private val _thePatient = MutableLiveData<Patient>()

    /**
     * Private mutable live data that determines the state of UI for getting the patient
     */
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the current UI state for fetching the patient
     */
    val uiState: LiveData<UiState>
        get() = _uiState

    /**
     * Public mutable state list of boolean that determines the state of each modal
     */
    val modalState = mutableStateListOf(*Array(13) { false })

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
            _uiState.value = UiState.Loading
            try {
                val patient = repository.getPatientById(patientId)
                _thePatient.value = patient
                _uiState.value = UiState.Success("Patient loaded")
            } catch (e:Exception) {
                _uiState.value = UiState.Error("Error loading patient: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Retrieves the total HEIFA score of the patient
     *
     * @return A Float value representing the total HEIFA score
     */
    fun getPatientTotalScore(): Float {
        return _thePatient.value?.totalScore?: 0f
    }

    /**
     * Retrieves all the scores of the patient
     *
     * @return A list of pairs (String, Float) where the string is the label
     * and the Float is the score
     */
    fun getPatientScore(): List<Pair<String, Float>> {
        return listOf(
            "Vegetables" to (_thePatient.value?.vegetableScore ?: 0f),
            "Fruits" to (_thePatient.value?.fruitsScore?: 0f),
            "Grains & Cereal" to (_thePatient.value?.grainsScore?: 0f),
            "Whole Grains" to (_thePatient.value?.wholeGrainsScore?: 0f),
            "Meat & Alternatives" to (_thePatient.value?.meatAlternativesScore?: 0f),
            "Dairy" to (_thePatient.value?.dairyScore?: 0f),
            "Water" to (_thePatient.value?.waterScore?: 0f),
            "Saturated Fat" to (_thePatient.value?.saturatedFatScore?: 0f),
            "Unsaturated Fat" to (_thePatient.value?.unsaturatedFatScore?: 0f),
            "Sodium" to (_thePatient.value?.sodiumScore?: 0f),
            "Sugar" to (_thePatient.value?.sugarScore?: 0f),
            "Alcohol" to (_thePatient.value?.alcoholScore?: 0f),
            "Discretionary" to (_thePatient.value?.discretionaryScore?: 0f)
        )
    }

    /**
     * Gets the maximum score for each category and also its progress ratio and color based on the ratio
     *
     * @param label The label of the type of scores
     * @param score The score of the the category
     * @return A triple containing the score, maximum score and also the color
     */
    fun getProgressInfo(label: String, score: Float): Triple<Float, Float, Color> {
        val cappedScore = when (label) {
            "Grains & Cereal", "Whole Grains", "Alcohol", "Water", "Saturated Fat", "Unsaturated Fat" -> 5f
            else -> 10f
        }
        val progressRatio = score / cappedScore
        val trackColor = when {
            progressRatio >= 0.8f -> Color(0xFF4CAF50)   // Green
            progressRatio >= 0.5f -> Color(0xFFFFC107)   // Amber
            else -> Color(0xFFF44336)                   // Red
        }

        return Triple(progressRatio, cappedScore, trackColor)
    }

    /**
     * Returns a list of key-value pairs describing nutritional intake and scoring
     * criteria for a specific dietary category based on the given index.
     *
     * Each pair represents a field label and its corresponding value, including:
     * - Intake data (e.g., serve size, intake %, mL)
     * - Scoring rules (e.g., maximum and zero score thresholds)
     * - Terminology to guide user understanding
     *
     * @param index The index representing a specific dietary category (0 to 12).
     * @return A list of label-value pairs representing category info.
     */
    fun getCategoryInfo(index: Int): List<Pair<String, String>> {
        val patient = _thePatient.value

        return when (index) {
            0 -> listOf(
                "Category" to "Vegetables",
                "Serve size" to patient?.vegetableServeSize.toString(),
                "Max Score (5)" to "Males: ≥ 6 serves\nFemales ≥ 5 serves",
                "Zero Score" to "No vegetables",
                "Variation Score" to patient?.vegetableVarietyScore.toString(),
                "Max Score (5)" to "At least one variety of vegetables",
                "Zero score" to "Variety score: 0",
                "Terminology" to "Vegetable variety:\n - Consuming different types of vegetables (leafy, cruciferous, root, etc.)\n" +
                                "Serve size:\n - 75g is approximately 1/2 cup cooked vegetables or 1 cup of leafy salad vegetables."
            )
            1 -> listOf(
                "Category" to "Fruits",
                "Serve size" to patient?.fruitServeSize.toString(),
                "Max Score (5)" to "≥ 2 serves",
                "Zero Score" to "No fruit",
                "Variation Score" to patient?.fruitVarietyScore.toString(),
                "Max Score (5)" to "≥ 2 varieties of fruit consumed",
                "Zero score" to "Variety score: 0",
                "Terminology" to "Fruit serve:\n - Approximately 150g (1 medium piece or 2 small pieces) or 350kJ.\n" +
                        "Fruit variety:\n - Consuming different types of fruits across categories (e.g., berries, citrus, stone fruits)."
            )
            2 -> listOf(
                "Category" to "Grains & Cereal",
                "Serve size" to patient?.grainsServeSize.toString(),
                "Max Score (5)" to "≥ 6 serves",
                "Zero Score" to "No grains and/or cereals",
                "Terminology" to "Refined grains:\n - Grains that have had the bran and germ removed."
            )
            3 -> listOf(
                "Category" to "Whole Grains",
                "Serve size" to patient?.wholeGrainsServeSize.toString(),
                "Max Score (5)" to "≥ 50% wholegrains or ≥ 3 serves",
                "Zero Score" to "No wholegrains",
                "Terminology" to "Wholegrains:\n - Grains that retain all parts of the grain (bran, germ, endosperm)."
            )
            4 -> listOf(
                "Category" to "Meat & Alternatives",
                "Serve size" to patient?.meatAlternativesServeSize.toString(),
                "Max Score (10)" to "Males: ≥ 3 serves\nFemales: ≥ 2.5 serves",
                "Zero Score" to "Males: ≤ 0.5 serves\nFemales: 0 serves",
                "Terminology" to "Meat alternatives:\n - Include eggs, nuts, seeds, legumes, tofu.\n" +
                        "Serve size:\n - ~65-100g cooked meat, 2 eggs, 170g tofu, 30g nuts/seeds."
            )
            5 -> listOf(
                "Category" to "Dairy",
                "Serve size" to patient?.dairyServeSize.toString(),
                "Max Score (10)" to "≥ 2.5 serves",
                "Zero Score" to "No dairy and/or alternatives",
                "Terminology" to "Dairy alternatives:\n - Plant-based milk and products fortified with calcium.\n" +
                                "Serve size:\n - 250ml milk, 200g yogurt, 40g cheese"
            )
            6 -> listOf(
                "Category" to "Water",
                "Water Consumption (ml)" to patient?.waterTotalML.toString(),
                "Total Beverage Consumption (ml)" to patient?.beverageTotalML.toString(),
                "Percentage of Water Consumption (%)" to patient?.water.toString(),
                "Max Score (5)" to "≥ 50% water consumed relative to total beverages",
                "Zero Score" to "Did not meet 1.5L of non-alcoholic beverages",
                "Terminology" to "Total beverages:\n - Includes all fluids consumed.\n" +
                        "Recommended intake:\n - 8-10 cups (2-2.5L) of fluid daily, primarily from water."
            )
            7 -> listOf(
                "Category" to "Saturated fat",
                "Intake (%)" to patient?.saturatedFat.toString(),
                "Max Score (5)" to "Saturated fat ≤ 10% of total energy intake",
                "Zero Score" to "Saturated fat  ≥ 12% of total energy intake",
                "Terminology" to "Saturated fat:\n - Type of fat found in animal products and some plant oils."
            )
            8 -> listOf(
                "Category" to "Unsaturated fat",
                "Serve Size" to patient?.unsaturatedFatServeSize.toString(),
                "Max Score (5)" to "MUFA & PUFA Males: 4 serves\nMUFA & PUFA Females: 2 serves",
                "Zero Score" to " MUFA & PUFA Males: < 1 serve\nMUFA & PUFA Females: < 0.5 serves",
                "Terminology" to "MUFA:\n - Monounsaturated Fatty Acids (olive oil, avocados).\n" +
                                "PUFA:\n - Polyunsaturated Fatty Acids (fish, nuts, seeds).\n" +
                                "Serve size:\n - 10g or approximately 2 teaspoons."
            )
            9 -> listOf(
                "Category" to "Sodium",
                "Intake (mg)" to patient?.sodiumMG.toString(),
                "Max Score" to "≤ 70 mmol (920 mg)",
                "Zero Score" to "> 100 mmol (3200 mg)",
                "Terminology" to "Sodium:\n - Main component of salt (NaCl).\n" +
                        "mmol:\n - Millimole, a unit of measurement (1 mmol sodium = 23mg).\n" +
                        "Recommended intake:\n - Less than 2000mg per day"
            )
            10 -> listOf(
                "Category" to "Sugar",
                "Intake (%)" to patient?.sugar.toString(),
                "Max Score" to "< 15% of total energy intake",
                "Zero Score" to "> 20% of total energy intake",
                "Terminology" to "Added sugars:\n - Sugars added during food processing or preparation, not naturally occurring in foods.\n" +
                        "WHO recommendation:\n - Less than 10% of total energy from added sugars."
            )
            11 -> listOf(
                "Category" to "Alcohol",
                "Intake (%)" to patient?.alcoholServeSize.toString(),
                "Max Score" to "≤ 1.4 standard drinks per day",
                "Zero Score" to "> 1.4 standard drinks per day",
                "Terminology" to "Standard drink:\n - Contains 10g of pure alcohol.\n" +
                        "Examples:\n - 100ml wine (13% alcohol), 285ml beer (4.9% alcohol).\n" +
                        "Guidelines:\n - For health reasons, consuming no alcohol is safest.\n"
            )
            12 -> listOf(
                "Category" to "Discretionary",
                "Serve size" to patient?.discretionaryServeSize.toString(),
                "Max Score" to "Males: < 3 serves\nFemales < 2.5 serves",
                "Zero Score" to "Males: ≥ 6 serves\nFemales ≥ 5.5 serves",
                "Terminology" to "Foods high in saturated fat, added sugar, salt and/or alcohol that are not necessary for a healthy diet (e.g., chips, sweets, soft drinks)."
            )
            else -> emptyList()
        }
    }


    // Factory class for creating instances of InsightViewModel
    class InsightViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            InsightViewModel(context) as T
    }
}