package com.fit2081.bryan_34309861_a3_app.ui.screens.ClinicianDashboardScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.data.viewModel.GenAIViewModel
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import com.fit2081.bryan_34309861_a3_app.ui.composables.ErrorContent
import com.fit2081.bryan_34309861_a3_app.ui.composables.Loading

@Composable
fun ClinicianDashboardScreen(
    navController: NavHostController,
    context: Context
) {
    val dashboardViewModel: ClinicianDashboardViewModel = viewModel(
        factory = ClinicianDashboardViewModel.ClinicianDashboardViewModelFactory(context)
    )

    val maleScore = dashboardViewModel.maleScore.observeAsState()
    val femaleScore = dashboardViewModel.femaleScore.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Title of the screen
            Text(
                text = "Clinician Dashboard",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            // The average HEIFA score of male and female
            HEIFARow(label = "Average HEIFA (Male)", value = maleScore.value ?: 0f)
            Spacer(modifier = Modifier.height(8.dp))
            HEIFARow(label = "Average HEIFA (Female)", value = femaleScore.value ?: 0f)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            // Data Analysis section with GenAI
            DataAnalysisSection(context, dashboardViewModel)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun HEIFARow(label: String, value: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label :",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "%.2f".format(value),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DataAnalysisSection(
    context: Context,
    dashboardViewModel: ClinicianDashboardViewModel,
    genAIViewModel: GenAIViewModel = viewModel(),
) {
    val prompt = dashboardViewModel.getPrompt(context)
    val uiState = genAIViewModel.uiState
        .observeAsState()

    // Button to send prompt and get response from AI
    Button(
        onClick = { genAIViewModel.sendPrompt(prompt) },
        modifier = Modifier
            .fillMaxWidth(0.6f),
        enabled = uiState.value !is UiState.Loading
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Data Analysis"
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Find Data Pattern"
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    DataAnalysisResponse(dashboardViewModel, uiState.value!!)
}

@Composable
fun DataAnalysisResponse(
    dashboardViewModel: ClinicianDashboardViewModel,
    uiState: UiState
) {
    when (uiState) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Success -> {
            DataAnalysisContent(uiState.outputText, dashboardViewModel)
        }
        is UiState.Error -> {
            ErrorContent(uiState.errorMessage)
        }
        else -> Unit
    }
}

@Composable
fun DataAnalysisContent(
    response: String,
    dashboardViewModel: ClinicianDashboardViewModel
) {
    val responses = dashboardViewModel.extractInsights(response)

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        responses.forEach { (title, description) ->
            // Contents of the GenAI response
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                append(title)
                            }
                            append(" $description")
                        },
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}