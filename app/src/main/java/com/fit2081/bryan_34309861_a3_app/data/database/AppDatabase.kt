package com.fit2081.bryan_34309861_a3_app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fit2081.bryan_34309861_a3_app.data.dao.FoodIntakeDao
import com.fit2081.bryan_34309861_a3_app.data.dao.NutriCoachTipDao
import com.fit2081.bryan_34309861_a3_app.data.dao.PatientDao
import com.fit2081.bryan_34309861_a3_app.data.util.Converters

@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTip::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
/**
 * Abstract class representing the App's database.
 * It extends RoomDatabase and provides access to the DAO interfaces for the entities.
 */
abstract class AppDatabase: RoomDatabase() {
    /**
     * Provides access to the PatientDao interface for performing
     * database operations on Patient entities.
     * @return PatientDao instance.
     */
    abstract fun patientDao(): PatientDao

    /**
     * Provides access to the FoodIntakeDao interface for performing
     * database operations on FoodIntake entities.
     * @return FoodIntakeDao instance.
     */
    abstract fun foodIntakeDao(): FoodIntakeDao

    /**
     * Provides access to the NutriCoachDao interface for performing
     * database operations on NutriCoachTip entities.
     * @return NutriCoachTipDao instance.
     */
    abstract fun nutriCoachDao(): NutriCoachTipDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        /**
         * Retrieves the singleton instance of the database.
         * If an instance already exist. ot returns the existing
         * instance. Otherwise, it creates a new instance of the database.
         *
         * @param context The context of the application.
         * @return The singleton instance of AppDatabase
         */
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "patient_database")
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}