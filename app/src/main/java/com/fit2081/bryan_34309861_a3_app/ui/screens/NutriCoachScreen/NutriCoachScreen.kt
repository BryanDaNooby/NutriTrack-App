package com.fit2081.bryan_34309861_a3_app.ui.screens.NutriCoachScreen

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.fit2081.bryan_34309861_a3_app.data.database.NutriCoachTip
import com.fit2081.bryan_34309861_a3_app.data.viewModel.GenAIViewModel
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import com.fit2081.bryan_34309861_a3_app.data.viewModel.FruitApiViewModel
import com.fit2081.bryan_34309861_a3_app.data.viewModel.NutriCoachTipViewModel
import com.fit2081.bryan_34309861_a3_app.data.viewModel.PicsumApiViewModel
import com.fit2081.bryan_34309861_a3_app.ui.composables.ErrorContent
import com.fit2081.bryan_34309861_a3_app.ui.composables.Loading
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@Composable
fun NutriCoachScreen(
    navController: NavHostController,
    context: Context
) {
    val fruitApiViewModel: FruitApiViewModel = viewModel(
        factory = FruitApiViewModel.FruitApiViewModelFactory(context)
    )

    val nutriCoachTipViewModel: NutriCoachTipViewModel = viewModel(
        factory = NutriCoachTipViewModel.NutriCoachTipViewModelFactory(context)
    )

    val picsumApiViewModel: PicsumApiViewModel = viewModel(
        factory = PicsumApiViewModel.PicsumApiViewModelFactory(context)
    )

    val patientUiState = fruitApiViewModel.patientUiState
        .observeAsState()

    when (val state = patientUiState.value) {
        is UiState.Loading -> {
            Loading()
        }

        is UiState.Success -> {
            NutriCoachScreenContent(fruitApiViewModel, nutriCoachTipViewModel, picsumApiViewModel)
        }

        else -> Unit
    }
}

@Composable
fun NutriCoachScreenContent(
    fruitApiViewModel: FruitApiViewModel,
    nutriCoachTipViewModel: NutriCoachTipViewModel,
    picsumApiViewModel: PicsumApiViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Title of the screen
            Text(
                text = "NutriCoach",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Fruit detail section
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(min = 300.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!fruitApiViewModel.isFruitScoreOptimal()) {
                    FruitDetailsSection(fruitApiViewModel)
                } else {
                    RandomImage(picsumApiViewModel)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // NutriCoachTips
            GenAISection(nutriCoachTipViewModel)
        }
    }
}

@Composable
fun RandomImage(
    picsumApiViewModel: PicsumApiViewModel
) {
    val imageUrl = picsumApiViewModel.imageUrl
        .observeAsState()

    val uiState = picsumApiViewModel.uiState
        .observeAsState()

    when (val state = uiState.value) {
        is UiState.Loading -> { Loading() }
        is UiState.Success -> {
            AsyncImage(
                model = imageUrl.value,
                contentDescription = "Random Image from picsum",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        is UiState.Error -> { Text("NO IMAGE FOUND") }
        else -> Unit
    }
}

@Composable
fun FruitDetailsSection(
    fruitApiViewModel: FruitApiViewModel,
) {
    val uiState = fruitApiViewModel.uiState
        .observeAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Fruit Name:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(6.dp))
        // Text field for user input
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = fruitApiViewModel.fruitName.value,
                onValueChange = { fruitApiViewModel.fruitName.value = it },
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        // Button to get the retrieve fruit information from API
        Button(
            onClick = {
                fruitApiViewModel.getFruitDetailByName()
            },
            enabled = fruitApiViewModel.buttonEnable(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search button"
            )
            Text(
                text = "Details",
                fontSize = 12.sp
            )
        }
    }
    uiState.value?.let { FruitDetailsTable(fruitApiViewModel, it) }
}

@Composable
fun FruitDetailsTable(
    fruitApiViewModel: FruitApiViewModel,
    uiState: UiState
) {

    when (uiState) {
        is UiState.Initial -> {
            Text(
                text = "Please type in a fruit type"
            )
        }
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Success -> {
            FruitDetailsTableContent(fruitApiViewModel)
        }
        is UiState.Error -> {
            Text(
                text = "Error: ${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun FruitDetailsTableContent(
    fruitApiViewModel: FruitApiViewModel
) {
    val details = fruitApiViewModel.getFruitDetailsMap()
    // Card to show the details of the fruit
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            details.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "$label icon",
                            tint = Color(0xFF6C63FF),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$label:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun GenAISection(
    nutriCoachTipViewModel: NutriCoachTipViewModel,
    genAIViewModel: GenAIViewModel = viewModel()
) {
    val showModal = rememberSaveable { mutableStateOf(false) }
    val prompt = nutriCoachTipViewModel.generatePrompt()
    val uiState = genAIViewModel.uiState
        .observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Button to generate response
            Button(
                onClick = {
                    genAIViewModel.sendPrompt(prompt)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.value !is UiState.Loading
            ) {
                Icon(
                    imageVector = Icons.Filled.Create,
                    contentDescription = "Generate AI response"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Motivational Message (AI)",
                    fontSize = 16.sp
                )
            }

            // Button to show all the tips
            Button(
                onClick = { showModal.value = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.value !is UiState.Loading
            ) {
                Text("Show All Tips")
            }
        }
        uiState.value?.let { GenAIResponse(genAIViewModel, nutriCoachTipViewModel, it) }
    }
    ShowTips(nutriCoachTipViewModel, showModal)
}


@Composable
fun GenAIResponse(
    genAIViewModel: GenAIViewModel,
    nutriCoachTipViewModel: NutriCoachTipViewModel,
    uiState: UiState
) {
    when (uiState) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Success -> {
            GenAIResponseContent(uiState.outputText, nutriCoachTipViewModel)
        }
        is UiState.Error -> {
            ErrorContent(uiState.errorMessage)
        }
        else -> Unit
    }
}

@Composable
fun GenAIResponseContent(
    response: String,
    nutriCoachTipViewModel: NutriCoachTipViewModel
) {
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedTime = currentTime.format(formatter)

    nutriCoachTipViewModel.insertTip(
        NutriCoachTip(
            patientId = nutriCoachTipViewModel.patientId,
            tip = response,
            timeAdded = formattedTime,
        )
    )

    // Response of the GenAI
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = response,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ShowTips(
    nutriCoachTipViewModel: NutriCoachTipViewModel,
    showModal: MutableState<Boolean>
) {
    val uiState = nutriCoachTipViewModel.allTipsUiState
        .observeAsState()

    when (val state = uiState.value) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Success -> {
            ShowTipsContent(nutriCoachTipViewModel, showModal)
        }
        is UiState.Error -> {
            ErrorContent(state.errorMessage)
        }
        else -> Unit
    }
}

@Composable
fun ShowTipsContent(
    nutriCoachTipViewModel: NutriCoachTipViewModel,
    showModal: MutableState<Boolean>
) {
    if (showModal.value) {
        val allTips = nutriCoachTipViewModel.allTips
            .observeAsState().value

        // Modal
        AlertDialog(
            onDismissRequest = { showModal.value = false },
            dismissButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = {
                        showModal.value = false
                    }) {
                        Text("Close")
                    }
                }
            },
            confirmButton = {},
            title = { Text("All Motivational Tips") },
            text = {
                if (allTips?.isEmpty() == true) {
                    Text("No tips were generated previously")
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        allTips?.forEach { tip ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = tip.tip,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = tip.timeAdded,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
