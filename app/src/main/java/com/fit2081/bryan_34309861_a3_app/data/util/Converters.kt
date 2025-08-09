package com.fit2081.bryan_34309861_a3_app.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters to allow Room to handle non-primitive types.
 *
 * This converter handles conversion between a List of Booleans and a JSON String.
 * It is required when storing complex data types in Room database entities.
 */
class Converters {

    /**
     * Converts a List of Booleans to a JSON String so it can be stored in the database.
     *
     * @param value The list of Boolean values.
     * @return A JSON-formatted string representing the list.
     */
    @TypeConverter
    fun fromList(value: List<Boolean>): String {
        return Gson().toJson(value)
    }

    /**
     * Converts a JSON String back into a List of Booleans.
     *
     * @param value A JSON-formatted string representing the list.
     * @return The original list of Boolean values.
     */
    @TypeConverter
    fun toList(value: String): List<Boolean> {
        val listType = object : TypeToken<List<Boolean>>() {}.type
        return Gson().fromJson(value, listType)
    }
}