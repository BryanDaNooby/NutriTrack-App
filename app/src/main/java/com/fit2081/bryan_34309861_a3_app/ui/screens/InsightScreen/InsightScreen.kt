package com.fit2081.bryan_34309861_a3_app.ui.screens.InsightScreen

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.bryan_34309861_a3_app.AppDashboardScreen
import com.fit2081.bryan_34309861_a3_app.data.util.UiState
import com.fit2081.bryan_34309861_a3_app.ui.composables.ErrorContent
import com.fit2081.bryan_34309861_a3_app.ui.composables.Loading

@Composable
fun InsightScreen(
    navController: NavHostController,
    context: Context
) {
    val insightViewModel: InsightViewModel = viewModel(
        factory = InsightViewModel.InsightViewModelFactory(context)
    )

    val uiState = insightViewModel.uiState
        .observeAsState()

    when (val state = uiState.value) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Error -> {
            ErrorContent(state.errorMessage)
        }
        is UiState.Success -> {
            InsightContent(
                navController,
                insightViewModel,
                context
            )
        }
        else -> Unit
    }
}

@Composable
fun InsightContent(
    navController: NavHostController,
    insightViewModel: InsightViewModel,
    context: Context
) {
    val scoreMap = insightViewModel.getPatientScore()
    val totalScore = insightViewModel.getPatientTotalScore()
    val modalState = insightViewModel.modalState

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
                text = "Insights: Food Score",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        itemsIndexed(scoreMap) { index, (label, score) ->
            val info = insightViewModel.getProgressInfo(label, score)
            // Score of each category
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Button(
                        onClick = {
                            modalState[index] = true
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Info"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Info",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                CategoryModal(modalState, insightViewModel, label, index)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProgressBarWithIndicator(
                        progress = info.first,
                        progressColor = info.third,
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                    )

                    Text(
                        text = "$score / ${info.second} ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }


        item {
            // Total Food Quality Score
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Total Food Quality Score",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }

        item {
            // Total Food Quality Score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                ProgressBarWithIndicator(
                    progress = totalScore / 100,
                    progressColor = Color.Blue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            // Total Food Quality Score
            Text(
                "$totalScore / 100",
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Share button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val shareIntent = Intent(ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Hi! I've got a HEIFA score of $totalScore!")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share text via"))
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text("Share with someone")
            }
        }

        item {
            // Improve my diet button (NutriCoachScreen)
            Button(
                onClick = {
                    navController.navigate(AppDashboardScreen.NutriCoach.route)
                },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text("Improve my diet!")
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun ProgressBarWithIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    barColor: Color = Color.LightGray,
    progressColor: Color = Color.Green,
    indicatorFillColor: Color = Color.White,
    indicatorBorderColor: Color = Color(0xFF008080),
    height: Dp = 10.dp,
    circleRadius: Dp = 10.dp,
    borderWidth: Dp = 2.dp
) {
    // Create a linear progress bar with a circle indicator
    Canvas(modifier = modifier.height(height)) {
        val width = size.width
        val heightPx = size.height
        val progressX = (progress.coerceIn(0f, 1f)) * width
        val center = Offset(progressX, heightPx / 2)

        // Draw background track
        drawRoundRect(
            color = barColor,
            size = size,
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )

        // Draw progress fill
        drawRoundRect(
            color = progressColor,
            size = Size(progressX, heightPx),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )

        // Draw circle border
        drawCircle(
            color = indicatorBorderColor,
            radius = circleRadius.toPx(),
            center = center
        )

        // Draw inner filled circle
        drawCircle(
            color = indicatorFillColor,
            radius = (circleRadius - borderWidth).toPx().coerceAtLeast(0f),
            center = center
        )
    }
}

@Composable
fun CategoryModal(
    modalState: SnapshotStateList<Boolean>,
    insightViewModel: InsightViewModel,
    category: String,
    index: Int
) {
    if (modalState[index]) {
        val theCategory = insightViewModel.getCategoryInfo(index)
        AlertDialog(
            onDismissRequest = { modalState[index] = false },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    theCategory.forEach { (label, content) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        append(label)
                                    }
                                    append("\n$content")
                                },
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            title = { Text("Score Breakdown")},
            confirmButton = {},
            dismissButton = {},
        )
    }
}