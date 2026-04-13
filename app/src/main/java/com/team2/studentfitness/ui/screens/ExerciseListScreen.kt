package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.team2.studentfitness.database.WorkEx
import com.team2.studentfitness.ui.theme.Teal
import com.team2.studentfitness.viewmodels.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    navController: NavController,
    workoutId: Int,
    workoutName: String,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()
    val userSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
    val isDark = userSettings?.theme == 1

    val selectedWorkoutExercises by workoutViewModel.selectedWorkoutExercises.collectAsState()

    LaunchedEffect(workoutId) {
        // We need a way to load by ID directly or find the workout object
        val workout = database.workoutsDao().getById(workoutId)
        if (workout != null) {
            workoutViewModel.loadExercisesForWorkout(workout)
        }
    }

    val textColor = if (isDark) Color.White else Color.Black
    val backgroundColor = if (isDark) Color.Black else Teal
    val cardBackground = if (isDark) Color(0xFF1E1E1E) else Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workoutName, fontWeight = FontWeight.Bold) },
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
                    titleContentColor = if (isDark) Color.White else Color.Black,
                    navigationIconContentColor = if (isDark) Color.White else Color.Black
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            if (selectedWorkoutExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFF7A643))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(selectedWorkoutExercises) { (workEx, exercise) ->
                        ExerciseCard(workEx, exercise, cardBackground, textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(workEx: WorkEx, exercise: Exercises, backgroundColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Surface(
                    color = Teal.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = exercise.muscleGroup,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (backgroundColor == Color.White) Teal else Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text("SETS", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.6f))
                    Text("${workEx.sets}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textColor)
                }
                Column {
                    Text("REPS", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.6f))
                    Text("${workEx.reps}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textColor)
                }
                if (workEx.restTime > 0) {
                    Column {
                        Text("REST", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.6f))
                        Text("${workEx.restTime}s", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = textColor)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )
        }
    }
}
