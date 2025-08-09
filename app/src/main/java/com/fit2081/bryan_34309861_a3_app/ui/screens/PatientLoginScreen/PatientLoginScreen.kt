package com.fit2081.bryan_34309861_a3_app.ui.screens.PatientLoginScreen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientLoginScreen(
    navController: NavHostController,
    context: Context
) {
    val patientLoginViewModel: PatientLoginViewModel = viewModel(
        factory = PatientLoginViewModel.PatientLoginViewModelFactory(context)
    )

    val patientId = patientLoginViewModel.patientIdPlaceholder
    val password = patientLoginViewModel.patientPasswordPlaceholder
    val allRegisteredPatients = patientLoginViewModel.getAllRegisteredPatient()
    val expanded = patientLoginViewModel.expanded
    val passwordVisible = patientLoginViewModel.passwordVisible

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Arrow to go back to Welcome Screen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { navController.navigate(AppDashboardScreen.Welcome.route) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }

        item {
            // Title of Patient Login Screen
            Text(
                text = stringResource(R.string.patientLogin),
                modifier = Modifier.padding(bottom = 24.dp),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            // Dropdown menu for registered patient id
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                OutlinedTextField(
                    value = patientId.value,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.patientIdLabel), fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                    },
                    readOnly = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    allRegisteredPatients.forEach { patient ->
                        DropdownMenuItem(
                            text = { Text(patient.patientId) },
                            onClick = {
                                patientId.value = patient.patientId
                                expanded.value = !expanded.value
                                patientLoginViewModel.getPatientById(patientId.value)
                            }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            // Text field for password input
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.85f),
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.patientPasswordLabel), fontSize = 14.sp) },
                visualTransformation = if (passwordVisible.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible.value) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff
                    val description = if (passwordVisible.value) "Hide password"
                    else "Show password"
                    IconButton(
                        onClick = { passwordVisible.value = !passwordVisible.value }
                    ) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                singleLine = true
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            // Login Disclaimer
            Text(
                stringResource(R.string.loginDisclaimer),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 20.sp
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            // Button to verify user
            Button(
                onClick = patientLoginViewModel.isAuthorized(
                    patientId.value,
                    password.value,
                    context,
                    navController
                ),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(0.85f)
            ) {
                Text("Continue")
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            // Text that links to Register Screen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate(AppDashboardScreen.Register.route)
                    }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            // Text that links to Reset Password Screen
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Forget your password?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Reset password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate(AppDashboardScreen.ResetPassword.route)
                    }
                )
            }
        }
    }
}
