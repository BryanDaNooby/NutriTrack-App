package com.fit2081.bryan_34309861_a3_app.data.model

/**
 * Data class to store the API response from FruityVice API
 */
data class Fruit(
    val id: Int,
    val name: String,
    val family: String,
    val order: String,
    val genus: String,
    val nutritions: Nutrition
)

/**
 * Data class to store the nutrition in the fruit object
 */
data class Nutrition(
    val calories: Float,
    val fat: Float,
    val sugar: Float,
    val carbohydrates: Float,
    val protein: Float
)
