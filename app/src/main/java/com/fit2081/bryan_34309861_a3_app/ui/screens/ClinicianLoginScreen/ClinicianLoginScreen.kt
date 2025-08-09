package com.fit2081.bryan_34309861_a3_app.ui.screens.ClinicianLoginScreen

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.R

@Composable
fun ClinicianLoginScreen(
    navController: NavHostController,
    context: Context
) {
    val clinicianLoginViewModel: ClinicianLoginViewModel = viewModel(
        factory = ClinicianLoginViewModel.ClinicianLoginViewModelFactory(context)
    )

    val inputKey = clinicianLoginViewModel.inputKey
    val keyVisible = clinicianLoginViewModel.keyVisible

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Title for the Clinician Login Screen
            Text(
                text = stringResource(R.string.clinicianLogin),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
        }

        item {
            // Text field for user input
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 12.dp),
                value = inputKey.value,
                onValueChange = { inputKey.value = it },
                label = { Text(stringResource(R.string.clinicianKey), fontSize = 14.sp) },
                visualTransformation = if (keyVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (keyVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (keyVisible.value) "Hide password" else "Show password"
                    IconButton(onClick = { keyVisible.value = !keyVisible.value }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true
            )
        }

        item {
            // Button to verify the key and navigate to Clinician Dashboard
            Button(
                onClick = clinicianLoginViewModel.validateKey(context, navController),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(vertical = 12.dp)
            ) {
                Text("Clinician Login")
            }
        }
    }
}

