package com.fit2081.bryan_34309861_a3_app.ui.screens.SettingsScreen

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(
    navController: NavHostController,
    context: Context,
    onToggleDarkMode: (Boolean) -> Unit,
    darkModeEnabled: Boolean
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.SettingViewModelFactory(context)
    )

    val thePatient = settingsViewModel.thePatient
        .observeAsState()
    var isDark by rememberSaveable { mutableStateOf(darkModeEnabled) }
    val showModal = settingsViewModel.showModal
    val newName = settingsViewModel.newName

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        item {
            Text(
                text = "ACCOUNT",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        item {
            SettingRow(
                icon = Icons.Default.Person,
                text = thePatient.value?.name?: "",
                trailingIcon = Icons.Default.Edit,
                onClick = { showModal.value = true }
            )
            ChangeNameModal(newName, showModal, settingsViewModel, navController, context)
        }
        item { SettingRow(icon = Icons.Default.Phone, text = thePatient.value?.phoneNumber ?: "") }
        item { SettingRow(Icons.Default.Badge, text = thePatient.value?.patientId ?: "") }

        item { HorizontalDivider() }

        item {
            Text(
                text = "OTHER SETTINGS",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        item {
            SettingRow(
                icon = Icons.Default.ExitToApp,
                text = "Logout",
                trailingIcon = Icons.Default.ArrowForward,
                onClick = settingsViewModel.logout(navController, context)
            )
        }

        item {
            SettingRow(
                icon = Icons.Default.SupervisorAccount,
                text = "Clinician Login",
                trailingIcon = Icons.Default.ArrowForward,
                onClick = settingsViewModel.clinicianLogin(navController)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable {
                        isDark = !isDark
                        onToggleDarkMode(isDark)
                        settingsViewModel.toggleDarkMode(context, isDark)
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Dark Mode",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Dark Mode", fontSize = 16.sp)
                }

                Switch(
                    checked = isDark,
                    onCheckedChange = {
                        isDark = it
                        onToggleDarkMode(it)
                        settingsViewModel.toggleDarkMode(context, isDark)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black)
                )
            }
        }
    }
}

@Composable
fun SettingRow(
    icon: ImageVector,
    text: String,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 16.sp,
            )
        }
        if (trailingIcon != null) {
            Icon(
                trailingIcon,
                contentDescription = "Action",
                modifier = Modifier
                    .size(20.dp)
            )
        }
    }
}

@Composable
fun ChangeNameModal(
    newName: MutableState<String>,
    showModal: MutableState<Boolean>,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController,
    context: Context
) {
    if (showModal.value) {
        AlertDialog(
            onDismissRequest = { showModal.value = false },
            confirmButton = {
                Button(
                    onClick = { settingsViewModel.validateName(context, navController) }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Text(
                    "Cancel",
                    modifier = Modifier
                        .clickable { showModal.value = false }
                        .padding(8.dp)
                )
            },
            title = { Text("Edit Name") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName.value,
                        onValueChange = { newName.value = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                }
            }
        )
    }
}