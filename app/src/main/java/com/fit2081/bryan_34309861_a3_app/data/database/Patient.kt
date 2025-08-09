package com.fit2081.bryan_34309861_a3_app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a patient entity within the Room database
 */
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String = "",
    var patientPassword: String = "",
    var name: String = "",
    val phoneNumber: String,
    val sex: String,
    val totalScore: Float,
    val discretionaryScore: Float,
    val discretionaryServeSize: Float,
    val vegetableScore: Float,
    val vegetableServeSize: Float,
    val vegetableVarietyScore: Float,
    val fruitsScore: Float,
    val fruitServeSize: Float,
    val fruitVarietyScore: Float,
    val grainsScore: Float,
    val grainsServeSize: Float,
    val wholeGrainsScore: Float,
    val wholeGrainsServeSize: Float,
    val meatAlternativesScore: Float,
    val meatAlternativesServeSize: Float,
    val dairyScore: Float,
    val dairyServeSize: Float,
    val sodiumScore: Float,
    val sodiumMG: Float,
    val alcoholScore: Float,
    val alcoholServeSize: Float,
    val waterScore: Float,
    val water: Float,
    val waterTotalML: Float,
    val beverageTotalML: Float,
    val sugarScore: Float,
    val sugar: Float,
    val saturatedFatScore: Float,
    val saturatedFat: Float,
    val unsaturatedFatScore: Float,
    val unsaturatedFatServeSize: Float
)