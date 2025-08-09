package com.fit2081.bryan_34309861_a3_app.ui.screens.HomeScreen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.R
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import com.fit2081.bryan_34309861_a3_app.ui.composables.ErrorContent
import com.fit2081.bryan_34309861_a3_app.ui.composables.Loading

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.HomeViewModelFactory(context)
    )

    val uiState = homeViewModel.uiState
        .observeAsState()

    when (val state = uiState.value) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Error -> {
            ErrorContent(state.errorMessage)
        }
        is UiState.Success -> {
            HomeScreenContent(navController, homeViewModel)
        }
        else -> Unit
    }
}

@Composable
fun HomeScreenContent(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Greetings User
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Hello, ${homeViewModel.getPatientName()}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            // Edit questionnaire
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.editQuestionnaire),
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.8f)
                )
                Button(
                    onClick = { navController.navigate(AppDashboardScreen.Questionnaire.route) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.weight(0.2f)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }
            }
        }

        item {
            // Image
            Image(
                painter = painterResource(R.drawable.homescreen),
                contentDescription = "plateOfFood",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1.5f)
                    .padding(vertical = 12.dp)
            )
        }

        item {
            HorizontalDivider()
        }

        item {
            // navigate to insight screen
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "My Score",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.clickable { navController.navigate(AppDashboardScreen.Insight.route) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("See all scores", fontSize = 12.sp)
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Insight")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
        }

        item {
            // Total score of patient
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Show", modifier = Modifier.padding(end = 10.dp))
                Text("Your Food Quality score", fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("${homeViewModel.getPatientTotalScore()} / 100", fontWeight = FontWeight.Bold)
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            // Explanation of food quality score
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        "What is the Food Quality Score?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.food_quality_score_desc),
                        fontSize = 12.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}