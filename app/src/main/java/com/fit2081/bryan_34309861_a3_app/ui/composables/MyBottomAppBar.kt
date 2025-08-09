package com.fit2081.bryan_34309861_a3_app.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen

@Composable
fun MyBottomAppBar(
    navController: NavHostController
) {
    val items = listOf(
        AppDashboardScreen.Home.route,
        AppDashboardScreen.Insight.route,
        AppDashboardScreen.NutriCoach.route,
        AppDashboardScreen.Settings.route
    )
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.route == item
            NavigationBarItem(
                icon = {
                    when (item) {
                        AppDashboardScreen.Home.route -> Icon(
                            Icons.Filled.Home,
                            contentDescription = "Home"
                        )

                        AppDashboardScreen.Insight.route -> Icon(
                            Icons.Filled.Insights,
                            contentDescription = "Insights"
                        )

                        AppDashboardScreen.NutriCoach.route -> Icon(
                            Icons.Filled.Analytics,
                            contentDescription = "NutriCoach"
                        )

                        AppDashboardScreen.Settings.route -> Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                label = { Text(item) },
                onClick = {
                    navController.navigate(item)
                },
                selected = isSelected
            )
        }
    }
}