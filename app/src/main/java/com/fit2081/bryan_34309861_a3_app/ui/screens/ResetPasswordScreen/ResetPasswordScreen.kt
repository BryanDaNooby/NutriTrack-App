package com.fit2081.bryan_34309861_a3_app.ui.screens.ResetPasswordScreen

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
fun ResetPasswordScreen(
    navController: NavHostController,
    context: Context
) {
    val viewModel: ResetPasswordViewModel = viewModel(
        factory = ResetPasswordViewModel.ResetPasswordViewModelFactory(context)
    )

    val patientId = viewModel.patientIdPlaceholder
    val phoneNumber = viewModel.phoneNumberPlaceholder

    val allRegisteredPatients = viewModel.getAllRegisteredPatient()

    val expanded = viewModel.expanded
    val verticalScroll = rememberScrollState()

    val isVerified = viewModel.isVerified

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
            text = "Reset Password",
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
                    allRegisteredPatients.forEach { patient ->
                        DropdownMenuItem(
                            text = { Text(patient.patientId) },
                            onClick = {
                                patientId.value = patient.patientId
                                viewModel.getPatientById(patientId.value)
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
                    val verified = viewModel.verifyPatient(phoneNumber.value, context)
                    isVerified.value = verified
                }
            ) {
                Text("Verify Identity")
            }
        } else {
            ResetPasswordField(viewModel, navController, context)
        }
    }
}

@Composable
fun ResetPasswordField(
    viewModel: ResetPasswordViewModel,
    navController: NavHostController,
    context: Context
) {
    val newPassword = viewModel.passwordPlaceholder
    val newConfirmPassword = viewModel.confirmPasswordPlaceholder

    val newPasswordVisible = viewModel.passwordVisible
    val newConfirmPasswordVisible = viewModel.confirmPasswordVisible

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.85f),
        value = newPassword.value,
        onValueChange = { newPassword.value = it },
        label = { Text(stringResource(R.string.patientPasswordLabel), fontSize = 14.sp) },
        visualTransformation = if (newPasswordVisible.value) VisualTransformation.None
                                else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (newPasswordVisible.value) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff
            val description = if (newPasswordVisible.value) "Hide password"
                                    else "Show password"
            IconButton(
                onClick = { newPasswordVisible.value = !newPasswordVisible.value }
            ) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    PasswordRequirementItem("At least 8 characters", viewModel.isLongEnough.value)
    PasswordRequirementItem("Contains uppercase", viewModel.hasUppercase.value)
    PasswordRequirementItem("Contains lowercase", viewModel.hasLowercase.value)
    PasswordRequirementItem("Contains a number", viewModel.hasDigit.value)
    PasswordRequirementItem("Contains a special character", viewModel.hasSpecialChar.value)
    PasswordRequirementItem("No spaces", viewModel.noSpaces.value)
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(0.85f),
        value = newConfirmPassword.value,
        onValueChange = { newConfirmPassword.value = it },
        label = { Text("Confirm Password", fontSize = 14.sp) },
        visualTransformation = if (newConfirmPasswordVisible.value) VisualTransformation.None
                                else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (newConfirmPasswordVisible.value) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff
            val description = if (newConfirmPasswordVisible.value) "Hide password"
                                    else "Show password"
            IconButton(
                onClick = { newConfirmPasswordVisible.value = !newConfirmPasswordVisible.value }
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
        onClick = viewModel.resetPassword(
            newPassword.value,
            newConfirmPassword.value,
            context,
            navController
        ),
        modifier = Modifier
            .padding(top = 24.dp)
            .fillMaxWidth(0.85f)
    ) {
        Text("Reset Password")
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