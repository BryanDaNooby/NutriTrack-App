package com.fit2081.bryan_34309861_a3_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit2081.bryan_34309861_a3_app.data.util.AuthManager
import com.fit2081.bryan_34309861_a3_app.data.viewModel.FoodIntakeViewModel
import com.fit2081.bryan_34309861_a3_app.data.util.readCSV
import com.fit2081.bryan_34309861_a3_app.data.viewModel.PatientViewModel
import com.fit2081.bryan_34309861_a3_app.ui.composables.ClinicianDashboardFAB
import com.fit2081.bryan_34309861_a3_app.ui.composables.MyBottomAppBar
import com.fit2081.bryan_34309861_a3_app.ui.composables.MyTopAppBar
import com.fit2081.bryan_34309861_a3_app.ui.screens.ClinicianDashboardScreen.ClinicianDashboardScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.ClinicianLoginScreen.ClinicianLoginScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.HomeScreen.HomeScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.InsightScreen.InsightScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.NutriCoachScreen.NutriCoachScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.PatientLoginScreen.PatientLoginScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.QuestionnaireScreen.QuestionnaireScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.RegisterScreen.RegisterScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.ResetPasswordScreen.ResetPasswordScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.SettingsScreen.SettingsScreen
import com.fit2081.bryan_34309861_a3_app.ui.screens.WelcomeScreen.WelcomeScreen
import com.fit2081.bryan_34309861_a3_app.ui.theme.Bryan_34309861_A3_appTheme

sealed class AppDashboardScreen(val route: String) {
    object Launch : AppDashboardScreen("Launch")
    object Welcome : AppDashboardScreen("Welcome")
    object PatientLogin : AppDashboardScreen("Patient Login")
    object Register : AppDashboardScreen("Register")
    object ResetPassword : AppDashboardScreen("Reset Password")
    object Questionnaire : AppDashboardScreen("Questionnaire")
    object Home : AppDashboardScreen("Home")
    object Insight : AppDashboardScreen("Insight")
    object NutriCoach: AppDashboardScreen("NutriCoach")
    object Settings: AppDashboardScreen("Settings")
    object ClinicianLogin: AppDashboardScreen("Clinician Login")
    object ClinicianDashboard: AppDashboardScreen("Clinician Dashboard")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val patientViewModel: PatientViewModel = ViewModelProvider(
                this, PatientViewModel.PatientViewModelFactory(this@MainActivity)
            )[PatientViewModel::class.java]

            val foodIntakeViewModel: FoodIntakeViewModel = ViewModelProvider(
                this, FoodIntakeViewModel.FoodIntakeViewModelFactory(this@MainActivity)
            )[FoodIntakeViewModel::class.java]

            readCSV(context, "data.csv", patientViewModel, foodIntakeViewModel)
            AuthManager.initializeUserId(context)

            val navController = rememberNavController()
            val currentRoute by navController.currentBackStackEntryAsState()
            val darkMode = context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
                .getBoolean("darkMode", false)
            var darkModeEnabled by rememberSaveable { mutableStateOf(darkMode) }

            Bryan_34309861_A3_appTheme(darkTheme = darkModeEnabled) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute?.destination?.route in listOf(
                                AppDashboardScreen.Home.route,
                                AppDashboardScreen.Insight.route,
                                AppDashboardScreen.NutriCoach.route,
                                AppDashboardScreen.Settings.route,
                                AppDashboardScreen.ClinicianLogin.route,
                                AppDashboardScreen.ClinicianDashboard.route
                            )) {
                            MyBottomAppBar(navController)
                        }
                    },
                    topBar = {
                        if (currentRoute?.destination?.route
                            == AppDashboardScreen.Questionnaire.route) {
                            MyTopAppBar(navController, context)
                        }
                    },
                    floatingActionButton = {
                        if (currentRoute?.destination?.route == AppDashboardScreen.ClinicianDashboard.route) {
                            ClinicianDashboardFAB(navController)
                        }
                    }
                ) { innerPadding ->
                    AppDashboardContent(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        context = context,
                        darkModeEnabled = darkModeEnabled,
                        onToggleDarkMode =  { darkModeEnabled = it },
                    )
                }
            }
        }
    }
}

@Composable
fun AppDashboardContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NavHostApp(navController, context,darkModeEnabled, onToggleDarkMode)
    }
}

@Composable
fun NavHostApp(
    navController: NavHostController,
    context: Context,
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDashboardScreen.Launch.route,
        modifier = modifier
    ) {
        composable(AppDashboardScreen.Launch.route) {
            InitialLaunchScreen(navController, context)
        }
        composable(AppDashboardScreen.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(AppDashboardScreen.PatientLogin.route) {
            PatientLoginScreen(navController, context)
        }
        composable(AppDashboardScreen.Register.route) {
            RegisterScreen(navController, context)
        }
        composable(AppDashboardScreen.ResetPassword.route) {
            ResetPasswordScreen(navController, context)
        }
        composable(AppDashboardScreen.Questionnaire.route) {
            QuestionnaireScreen(navController, context)
        }
        composable(AppDashboardScreen.Home.route) {
            HomeScreen(navController, context)
        }
        composable(AppDashboardScreen.Insight.route) {
            InsightScreen(navController, context)
        }
        composable(AppDashboardScreen.NutriCoach.route) {
            NutriCoachScreen(navController, context)
        }
        composable(AppDashboardScreen.Settings.route) {
            SettingsScreen(navController, context, onToggleDarkMode = onToggleDarkMode,darkModeEnabled)
        }
        composable(AppDashboardScreen.ClinicianLogin.route) {
            ClinicianLoginScreen(navController, context)
        }
        composable(AppDashboardScreen.ClinicianDashboard.route) {
            ClinicianDashboardScreen(navController, context)
        }
    }
}

@Composable
fun InitialLaunchScreen(navController: NavHostController, context: Context) {
    val sharedPref = context.getSharedPreferences("AppMemo", Context.MODE_PRIVATE)
    val currentSession = sharedPref.getString("currentSession", null)

    val answeredQuestionnaire = sharedPref.getBoolean("filled_$currentSession", false)

    LaunchedEffect(Unit) {
        if (currentSession == null) {
            navController.navigate(AppDashboardScreen.Welcome.route) {
                popUpTo(AppDashboardScreen.Launch.route) { inclusive = true }
            }
        } else {
            if (answeredQuestionnaire) {
                navController.navigate(AppDashboardScreen.Home.route) {
                    popUpTo(AppDashboardScreen.Launch.route) { inclusive = true }
                }
            } else {
                navController.navigate(AppDashboardScreen.Questionnaire.route) {
                    popUpTo(AppDashboardScreen.Launch.route) { inclusive = true }
                }
            }
        }
    }
}