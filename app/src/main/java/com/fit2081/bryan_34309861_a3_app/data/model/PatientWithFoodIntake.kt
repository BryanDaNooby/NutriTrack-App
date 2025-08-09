package com.fit2081.bryan_34309861_a3_app.data.model

/**
 * Data class for patient's score and food intake result
 */
data class PatientsWithFoodIntake(
    val patientId: String,
    val totalScore: Float,
    val discretionaryScore: Float,
    val vegetableScore: Float,
    val fruitsScore: Float,
    val grainsScore: Float,
    val wholeGrainsScore: Float,
    val meatAlternativesScore: Float,
    val dairyScore: Float,
    val sodiumScore: Float,
    val alcoholScore: Float,
    val waterScore: Float,
    val sugarScore: Float,
    val saturatedFatScore: Float,
    val unsaturatedFatScore: Float,
    var checkboxes: List<Boolean>,
    var persona: String,
    var sleepTime: String,
    var eatTime: String,
    var wakeUpTime: String
) {
    fun toTableRow(): String {
        return """
            | $patientId | $totalScore | $discretionaryScore | $vegetableScore | $fruitsScore | $grainsScore | $wholeGrainsScore | $meatAlternativesScore | $dairyScore | $alcoholScore | $waterScore | $sugarScore | $saturatedFatScore | $unsaturatedFatScore | ${checkboxes.joinToString(", ")} | $persona | $sleepTime | $eatTime | $wakeUpTime |
        """.trimMargin()
    }
}
