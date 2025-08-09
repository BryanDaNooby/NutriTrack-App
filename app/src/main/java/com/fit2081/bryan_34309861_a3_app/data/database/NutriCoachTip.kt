package com.fit2081.bryan_34309861_a3_app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a nutriCoachTip entity within the Room database
 */
@Entity(tableName = "NutriCoachTips")
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val tip: String,
    val timeAdded: String
)