package com.fit2081.bryan_34309861_a3_app.ui.screens.RegisterScreen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.fit2081.bryan_34309861_a3_app.ui.composables.PasswordRequirementItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    context: Context
) {
    val registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.RegisterViewModelFactory(context)
    )
    val patientId = registerViewModel.patientIdPlaceholder
    val phoneNumber = registerViewModel.phoneNumberPlaceholder

    val allUnregisteredPatients = registerViewModel.getAllUnregisteredPatient()

    val expanded = registerViewModel.expanded

    val isVerified = registerViewModel.isVerified

    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(verticalScroll),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
        Text(
            text = stringResource(R.string.patientRegister),
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        ExposedDropdownMenuBox(
            expanded = if (!isVerified.value) expanded.value else false, // prevents it from expanding
            onExpandedChange = {
                if (!isVerified.value) {
                    expanded.value = !expanded.value
                }
            },
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
                enabled = !isVerified.value,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                singleLine = true
            )
            if (!isVerified.value) {
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = {
                        expanded.value = false
                    },
                ) {
                    allUnregisteredPatients.forEach { patient ->
                        DropdownMenuItem(
                            text = { Text(patient.patientId) },
                            onClick = {
                                patientId.value = patient.patientId
                                registerViewModel.getPatientById(patientId.value)
                                expanded.value = !expanded.value
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.85f),
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it },
            enabled = !isVerified.value,
            label = { Text("Phone Number", fontSize = 14.sp) },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (!isVerified.value) {
            Button(
                onClick = {
                    val verified = registerViewModel.verifyPatient(phoneNumber.value, context)
                    isVerified.value = verified
                }
            ) {
                Text("Verify Identity")
            }
        } else {
            RegisterField(context, navController, registerViewModel)
        }
    }
}

@Composable
fun RegisterField(
    context: Context,
    navController: NavHostController,
    registerViewModel: RegisterViewModel
) {
    val patientName = registerViewModel.patientNamePlaceholder
    val password = registerViewModel.passwordPlaceholder
    val confirmPassword = registerViewModel.confirmPasswordPlaceholder

    val passwordVisible = registerViewModel.passwordVisible
    val confirmPasswordVisible = registerViewModel.confirmPasswordVisible

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.85f),
        value = patientName.value,
        onValueChange = { patientName.value = it },
        label = { Text("Enter Name", fontSize = 14.sp) },
        singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))
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
    Spacer(modifier = Modifier.height(8.dp))
    PasswordRequirementItem("At least 8 characters", registerViewModel.isLongEnough.value)
    PasswordRequirementItem("Contains uppercase", registerViewModel.hasUppercase.value)
    PasswordRequirementItem("Contains lowercase", registerViewModel.hasLowercase.value)
    PasswordRequirementItem("Contains a number", registerViewModel.hasDigit.value)
    PasswordRequirementItem("Contains a special character", registerViewModel.hasSpecialChar.value)
    PasswordRequirementItem("No spaces", registerViewModel.noSpaces.value)
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.85f),
        value = confirmPassword.value,
        onValueChange = { confirmPassword.value = it },
        label = { Text("Confirm Password", fontSize = 14.sp) },
        visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None
                                else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (confirmPasswordVisible.value) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff
            val description = if (confirmPasswordVisible.value) "Hide password"
                                    else "Show password"
            IconButton(
                onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }
            ) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        stringResource(R.string.loginDisclaimer),
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        lineHeight = 20.sp
    )
    Spacer(modifier = Modifier.height(12.dp))
    Button(
        onClick = registerViewModel.validatePatient(
                patientName.value,
                password.value,
                confirmPassword.value,
                context,
                navController
            ),
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(0.85f)
    ) {
        Text("Register")
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Login",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.clickable {
                navController.navigate(AppDashboardScreen.PatientLogin.route)
            }
        )
    }
}