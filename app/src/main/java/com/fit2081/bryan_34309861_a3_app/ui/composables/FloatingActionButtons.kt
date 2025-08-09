package com.fit2081.bryan_34309861_a3_app.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen

@Composable
fun ClinicianDashboardFAB(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(AppDashboardScreen.Settings.route) {
                popUpTo(AppDashboardScreen.ClinicianDashboard.route) { inclusive = true }
            }
        },
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        Text("Done")
    }
}