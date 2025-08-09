package com.fit2081.bryan_34309861_a3_app.ui.screens.WelcomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.R

@Composable
fun WelcomeScreen(
    navController: NavHostController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                "NutriTrack",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(16.dp)
            )
        }

        item {
            Text(
                stringResource(R.string.welcomePage),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }

        item {
            Button(
                onClick = {
                    navController.navigate(AppDashboardScreen.PatientLogin.route)
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Login")
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }

        item {
            Text(
                "Bryan Lau (34309861)",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}
