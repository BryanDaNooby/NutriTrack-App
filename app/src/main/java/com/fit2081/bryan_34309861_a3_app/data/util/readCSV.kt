package com.fit2081.bryan_34309861_a3_app.data.util

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.fit2081.bryan_34309861_a3_app.data.database.FoodIntake
import com.fit2081.bryan_34309861_a3_app.data.database.Patient
import com.fit2081.bryan_34309861_a3_app.data.viewModel.FoodIntakeViewModel
import com.fit2081.bryan_34309861_a3_app.data.viewModel.PatientViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

fun readCSV(
    context: Context,
    fileName: String,
    patientViewModel: PatientViewModel,
    foodIntakeViewModel: FoodIntakeViewModel,
) {
    val sharedPref = context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
    val isRead = sharedPref.getBoolean("isRead", false)
    if (!isRead) {
        val assets = context.assets
        try {
            val inputStream = assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val values = line.split(",")
                    val isMale = values[2] == "Male"
                    val offset = if (isMale) 0 else 1
                    val newPatient = Patient(
                        patientId = values[1],
                        phoneNumber = values[0],
                        sex = values[2],
                        totalScore = values[3 + offset].toFloat(),
                        discretionaryScore = values[5 + offset].toFloat(),
                        discretionaryServeSize = values[7].toFloat(),
                        vegetableScore = values[8 + offset].toFloat(),
                        vegetableServeSize = values[10].toFloat(),
                        vegetableVarietyScore = values[13].toFloat(),
                        fruitsScore = values[19 + offset].toFloat(),
                        fruitServeSize = values[21].toFloat(),
                        fruitVarietyScore = values[22].toFloat(),
                        grainsScore = values[29 + offset].toFloat(),
                        grainsServeSize = values[31].toFloat(),
                        wholeGrainsScore = values[33 + offset].toFloat(),
                        wholeGrainsServeSize = values[35].toFloat(),
                        meatAlternativesScore = values[36 + offset].toFloat(),
                        meatAlternativesServeSize = values[38].toFloat(),
                        dairyScore = values[40 + offset].toFloat(),
                        dairyServeSize = values[42].toFloat(),
                        sodiumScore = values[43 + offset].toFloat(),
                        sodiumMG = values[45].toFloat(),
                        alcoholScore = values[46 + offset].toFloat(),
                        alcoholServeSize = values[48].toFloat(),
                        waterScore = values[49 + offset].toFloat(),
                        water = values[51].toFloat(),
                        waterTotalML = values[52].toFloat(),
                        beverageTotalML = values[53].toFloat(),
                        sugarScore = values[54 + offset].toFloat(),
                        sugar = values[56].toFloat(),
                        saturatedFatScore = values[57 + offset].toFloat(),
                        saturatedFat = values[59].toFloat(),
                        unsaturatedFatScore = values[60 + offset].toFloat(),
                        unsaturatedFatServeSize = values[62].toFloat()
                    )

                    val newFoodIntake = FoodIntake(
                        patientId = values[1]
                    )
                    patientViewModel.insertPatient(patient = newPatient)
                    foodIntakeViewModel.insertFoodIntake(foodIntake = newFoodIntake)
                    Log.d("NEW PATIENT", "Added new patient: $newPatient")
                }
            }
            sharedPref.edit().apply {
                putBoolean("isRead", true)
                apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}

