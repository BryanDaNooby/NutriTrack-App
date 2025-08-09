package com.fit2081.bryan_34309861_a3_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fit2081.bryan_34309861_a3_app.data.database.FoodIntake

@Dao
interface FoodIntakeDao {
    /**
     * Insert a new foodIntake into the database
     *
     * @param foodIntake The [FoodIntake] object to be inserted
     */
    @Insert
    suspend fun insert(foodIntake: FoodIntake)

    /**
     * Update a foodIntake in the database
     *
     * @param foodIntake The [FoodIntake] to be updated
     */
    @Update
    suspend fun updateFoodIntake(foodIntake: FoodIntake)

    /**
     * Get all the food intakes from the database
     *
     * @return A list of all [FoodIntake] object in the
     * database
     */
    @Query("SELECT * FROM food_intake")
    suspend fun getAllFoodIntake(): List<FoodIntake>

    /**
     * Retrieve the food intake of a patient based on the
     * patient id
     *
     * @param patientId The patient's ID
     * @return A [FoodIntake] object that corresponds to the
     * patients ID in the database
     */
    @Query("SELECT * FROM food_intake WHERE patientId = :patientId")
    suspend fun getIntakesByPatientId(patientId: String): FoodIntake
}