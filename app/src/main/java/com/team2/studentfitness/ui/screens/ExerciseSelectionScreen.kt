package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.database.Exercises
import com.team2.studentfitness.ui.components.youtube.EmbeddedYouTubeVideo
import com.team2.studentfitness.ui.theme.Teal
import com.team2.studentfitness.viewmodels.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionScreen(
    navController: NavController,
    muscleGroup: String,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()
    val userSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
    val isDark = userSettings?.theme == 1

    val categoryExercises by workoutViewModel.categoryExercises.collectAsState()
    val selectedExercises by workoutViewModel.newWorkoutExercises.collectAsState()
    val isLoading by workoutViewModel.isLoading.collectAsState()

    LaunchedEffect(muscleGroup) {
        workoutViewModel.loadExercisesByCategory(muscleGroup)
    }

    val textColor = if (isDark) Color.White else Color.Black
    val backgroundColor = if (isDark) Color.Black else Teal
    val cardBackground = if (isDark) Color(0xFF1E1E1E) else Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(muscleGroup, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFF7A643))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(categoryExercises) { exercise ->
                    val isSelected = selectedExercises.any { it.uid == exercise.uid }
                    
                    SelectionExerciseCard(
                        exercise = exercise,
                        isSelected = isSelected,
                        backgroundColor = cardBackground,
                        textColor = textColor,
                        onSelect = {
                            if (isSelected) {
                                workoutViewModel.removeExerciseFromNewWorkout(exercise)
                            } else {
                                workoutViewModel.addExerciseToNewWorkout(exercise)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionExerciseCard(
    exercise: Exercises,
    isSelected: Boolean,
    backgroundColor: Color,
    textColor: Color,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFF7A643)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                
                IconButton(onClick = onSelect) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isSelected) "Selected" else "Add",
                        tint = if (isSelected) Color(0xFFF7A643) else textColor.copy(alpha = 0.5f)
                    )
                }
            }

            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f),
                maxLines = 3
            )

            exercise.videoID?.let { videoId ->
                if (videoId.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    EmbeddedYouTubeVideo(
                        videoId = videoId,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                }
            }
        }
    }
}
