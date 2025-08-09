package com.fit2081.bryan_34309861_a3_app.ui.screens.QuestionnaireScreen

import TimePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.R
import java.util.Calendar

@Composable
fun QuestionnaireScreen(
    navController: NavHostController,
    context: Context
) {
    val questionnaireViewModel: QuestionnaireViewModel = viewModel(
        factory = QuestionnaireViewModel.QuestionnaireViewModelFactory(context)
    )

    val eatTime = questionnaireViewModel.eatTimePlaceholder
    val sleepTime = questionnaireViewModel.sleepTimePlaceholder
    val wakeUpTime = questionnaireViewModel.wakeUpTimePlaceholder

    val eatTimeDialogState = questionnaireViewModel.eatTimePickerState
    val sleepTimeDialogState = questionnaireViewModel.sleepTimePickerState
    val wakeUpTimeDialogState = questionnaireViewModel.wakeUpTimePickerState

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        item {
            // Checkbox section
            CheckBoxQuestion(questionnaireViewModel)
        }

        item {
            HorizontalDivider()
        }

        item {
            // Persona section
            Persona(questionnaireViewModel)
        }

        item {
            HorizontalDivider()
        }

        item {
            // Timings section
            Timings(
                eatTime,
                sleepTime,
                wakeUpTime,
                eatTimeDialogState,
                sleepTimeDialogState,
                wakeUpTimeDialogState
            )
        }

        item {
            // Button to save questionnaire
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = questionnaireViewModel.validateQuestionnaire(context, navController)
                ) {
                    Text("Save Preferences")
                }
            }
        }
    }

    ShowTimePickerDialog(eatTimeDialogState) { selectedTime ->
        eatTime.value = selectedTime
    }

    ShowTimePickerDialog(sleepTimeDialogState) { selectedTime ->
        sleepTime.value = selectedTime
    }

    ShowTimePickerDialog(wakeUpTimeDialogState) { selectedTime ->
        wakeUpTime.value = selectedTime
    }

}

@Composable
fun CheckBoxQuestion(
    questionnaireViewModel: QuestionnaireViewModel,
) {
    val categories = questionnaireViewModel.getFoodCategories()

    val checkboxStates = questionnaireViewModel.checkboxPlaceholder.value

    Text(
        text = stringResource(R.string.foodIntake),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.chunked(3).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEachIndexed { i, item ->
                    val index = categories.indexOf(item)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = checkboxStates[index],
                            onCheckedChange = {
                                checkboxStates[index] = it
                            }
                        )
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Persona(
    questionnaireViewModel: QuestionnaireViewModel,
) {
    val modalStates = questionnaireViewModel.personaModalStates
    val expanded = questionnaireViewModel.personaExpanded
    val personaList = questionnaireViewModel.getPersonaList()
    val persona = questionnaireViewModel.personaPlaceholder

    Text(
        text = "Your Persona",
        fontWeight = FontWeight.Bold
    )
    Text(
        text = stringResource(R.string.personaDesc),
        fontSize = 12.sp,
        lineHeight = 18.sp
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        personaList.chunked(3).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                rowItems.forEach { name ->
                    val index = personaList.indexOf(name)
                    Button(
                        onClick = { modalStates[index] = true },
                        modifier = Modifier
                            .weight(1f) ,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            softWrap = true,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    ShowModal(questionnaireViewModel ,modalStates, index)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Which persona best fits you?",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .menuAnchor(),
            value = persona.value,
            onValueChange = {},
            textStyle = TextStyle(fontSize = 12.sp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            readOnly = true,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            personaList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        persona.value = option
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun ShowModal(
    questionnaireViewModel: QuestionnaireViewModel,
    modalStates: SnapshotStateList<Boolean>,
    index: Int
) {
    if (modalStates[index]) {
        val thePersona = questionnaireViewModel.getPersonaInfo(index)
        AlertDialog(
            onDismissRequest = { modalStates[index] = false },
            text = { },
            title = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = thePersona.imageRes),
                        contentDescription = "Persona Image",
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        text = thePersona.name,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(thePersona.descriptionRes),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            modalStates[index] = false
                        }) {
                            Text("Dismiss")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun Timings(
    eatTime: MutableState<String>,
    sleepTime: MutableState<String>,
    wakeUpTime: MutableState<String>,
    eatTimeDialogState: MutableState<Boolean>,
    sleepTmeDialogState: MutableState<Boolean>,
    wakeUpTimeDialogState: MutableState<Boolean>
) {
    Text(
        "Timings",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    InputRow(
        stringResource(R.string.eatTime),
        eatTime,
        eatTimeDialogState
    )
    Spacer(modifier = Modifier.height(10.dp))
    InputRow(
        stringResource(R.string.sleepTime),
        sleepTime,
        sleepTmeDialogState
    )
    Spacer(modifier = Modifier.height(10.dp))
    InputRow(
        stringResource(R.string.wakeUpTime),
        wakeUpTime,
        wakeUpTimeDialogState
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun InputRow(
    question: String,
    time: MutableState<String>,
    timeState: MutableState<Boolean>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                fontSize = 12.sp,
                modifier = Modifier.weight(0.7f)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .weight(0.3f)
                    .clickable {
                        timeState.value = true
                    }
            ) {
                ReadOnlyTimeBox(
                    timeText = time.value,
                    onIconClick = {
                        timeState.value = true
                    }
                )
            }
        }
    }
}

@Composable
fun ReadOnlyTimeBox(
    timeText: String,
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onIconClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = "Pick time",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = timeText,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ShowTimePickerDialog(
    state: MutableState<Boolean>,
    onTimeSelected: (String) -> Unit
) {
    if (state.value) {
        TimePickerDialog(
            onCancel = { state.value = false },
            onConfirm = { calendar ->
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                onTimeSelected(String.format("%02d:%02d", hour, minute))
                state.value = false
            }
        )
    }
}
