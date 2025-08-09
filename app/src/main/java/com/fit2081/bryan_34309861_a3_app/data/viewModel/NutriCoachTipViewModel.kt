package com.fit2081.bryan_34309861_a3_app.data.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.database.FoodIntake
import com.fit2081.bryan_34309861_a3_app.data.repository.FoodIntakeRepository
import com.fit2081.bryan_34309861_a3_app.data.database.NutriCoachTip
import com.fit2081.bryan_34309861_a3_app.data.repository.NutriCoachTipRepository
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.repository.PatientRepository
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import kotlinx.coroutines.launch

class NutriCoachTipViewModel(context: Context): ViewModel() {
    /**
     * Repositories instance for handling all data operations.
     * This is the single point of contact for the ViewModels to interact with data sources.
     */
    private val nutriRepository = NutriCoachTipRepository(context)
    private val patientRepository = PatientRepository(context)
    private val foodRepository = FoodIntakeRepository(context)

    /**
     * Current patient's ID in session
     */
    val patientId = AuthManager.getPatientId()?: ""

    /**
     * Private mutable live data that stores the current patient in session.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _thePatient = MutableLiveData<Patient>()

    /**
     * Private mutable live data that stores the foodIntake of the current patient.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _foodIntake = MutableLiveData<FoodIntake>()

    /**
     * Private mutable live data that stores the list of tips of patient in session.
     * Using LiveData provides a way to observe changes to the data over time.
     */
    private val _allTips = MutableLiveData<List<NutriCoachTip>>(emptyList())

    /**
     * Public immutable LiveData that exposes the current list of tips to the observers
     *
     * This property enables the UI to react to changes in the tips data while
     * preventing direct mutation from outside this class.
     */
    val allTips: LiveData<List<NutriCoachTip>>
        get() = _allTips

    /**
     * Private mutable live data that determines the state of UI while getting
     * all the tips of the patient in the database
     */
    private val _allTipsUiState = MutableLiveData<UiState>(UiState.Initial)

    /**
     * Public immutable LiveData that exposes the state of UI while getting
     * all the tips of the patient in the database
     */
    val allTipsUiState: LiveData<UiState>
        get() = _allTipsUiState

    /**
     * Initialize the ViewModel by loading the current patient with his/her
     * foodIntake and all the tips
     * This ensures data is available as soon as the UI starts observing
     */
    init {
        loadPatient(patientId)
        loadFoodIntake(patientId)
        loadAllTips()
    }

    /**
     * Loads the current patient by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the
     * most current data.
     */
    private fun loadPatient(patientId: String) {
        viewModelScope.launch {
            val patient = patientRepository.getPatientById(patientId)
            _thePatient.value = patient
        }
    }

    /**
     * Loads the current patient's foodIntake by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the most current data.
     */
    private fun loadFoodIntake(patientId: String) {
        viewModelScope.launch {
            val foodIntake = foodRepository.getAllIntakesByPatientId(patientId)
            _foodIntake.value = foodIntake
        }
    }

    /**
     * Loads the current patient's nutriCoachTips by fetching the latest data from the repository
     *
     * This method is responsible for loading the observed LiveData with the most current data.
     */
    private fun loadAllTips() {
        viewModelScope.launch {
            _allTipsUiState.value = UiState.Loading
            try {
                val tips = nutriRepository.getTipsByPatientId(patientId)
                _allTips.value = tips
                _allTipsUiState.value = UiState.Success("Tips successfully fetched")
            } catch (e: Exception) {
                _allTipsUiState.value = UiState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Retrieve a list of pairs of (label, score) of patient's score
     *
     * @return A list of pairs(String, Float) where the string is the label
     * and the Float is the score
     */
    private fun getPatientScore(): List<Pair<String, Float>> {
        return listOf(
            "Total Score" to (_thePatient.value?.totalScore ?: 0f),
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
     * Retrieve a list of pairs of (label, boolean) of patient's foodIntake
     *
     * @return A list of pairs(String, Boolean) where the string is the label
     * and the Boolean is either the patients eats or don't eat
     */
    private fun getPatientFoodIntake(): List<Pair<String, Boolean>> {
        val checkboxes = _foodIntake.value?.checkboxes
        return listOf(
            "Fruits" to (checkboxes?.get(0)?: false),
            "Vegetables" to (checkboxes?.get(1)?: false),
            "Grains" to (checkboxes?.get(2)?: false),
            "Red Meat" to (checkboxes?.get(3)?: false),
            "Seafood" to (checkboxes?.get(4)?: false),
            "Poultry" to (checkboxes?.get(5)?: false),
            "Fish" to (checkboxes?.get(6)?: false),
            "Eggs" to (checkboxes?.get(7)?: false),
            "Nuts/Seeds" to (checkboxes?.get(8)?: false),
        )
    }

    /**
     * Generate a prompt based on the patient's score and food intake
     *
     * @return A string value to pass it to the genAI
     */
    fun generatePrompt(): String {
        val scores = getPatientScore().toMap()
        val foodIntake = getPatientFoodIntake().toMap()

        val ateFruits = foodIntake["Fruits"] ?: false

        return """
                Generate a short, friendly, and encouraging message (1–2 sentences) to help someone improve their diet. Here are their HEIFA category scores:
                - Total Score: ${scores["Total Score"]}
                - Vegetables: ${scores["Vegetables"]}
                - Fruits: ${scores["Fruits"]}
                - Grains & Cereal: ${scores["Grains & Cereal"]}
                - Whole Grains: ${scores["Whole Grains"]}
                - Meat & Alternatives: ${scores["Meat & Alternatives"]}
                - Dairy: ${scores["Dairy"]}
                - Water: ${scores["Water"]}
                - Saturated Fat: ${scores["Saturated Fat"]}
                - Unsaturated Fat: ${scores["Unsaturated Fat"]}
                - Sodium: ${scores["Sodium"]}
                - Sugar: ${scores["Sugar"]}
                - Alcohol: ${scores["Alcohol"]}
                - Discretionary: ${scores["Discretionary"]}
                
                They ${if (ateFruits) "reported eating fruits recently" else "did not report eating fruits recently"}. Encourage them to keep up the good habits or improve in a positive, supportive, and motivational tone.
                """.trimIndent()
    }

    /**
     * Inserts a new tip into the database
     * Load the tips everytime a new tip is inserted
     *
     * @param nutriCoachTip The [NutriCoachTip] object to be
     * inserted in the database
     */
    fun insertTip(nutriCoachTip: NutriCoachTip) {
        viewModelScope.launch {
            nutriRepository.insertTip(nutriCoachTip)
            loadAllTips()
        }
    }

    // Factory class for creating instances of NutriCoachTipViewModel
    class NutriCoachTipViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NutriCoachTipViewModel(context) as T
    }
}