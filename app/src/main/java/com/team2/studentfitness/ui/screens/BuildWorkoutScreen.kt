package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.team2.studentfitness.ui.navigation.AppRoutes
import com.team2.studentfitness.ui.theme.Teal
import com.team2.studentfitness.ui.theme.Orange
import com.team2.studentfitness.viewmodels.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildWorkoutScreen(navController: NavController, workoutViewModel: WorkoutViewModel = viewModel()) {
    val context = LocalContext.current
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()
    val userSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
    val isDark = userSettings?.theme == 1

    val newWorkoutExercises by workoutViewModel.newWorkoutExercises.collectAsState()
    var workoutName by remember { mutableStateOf("") }

    val textColor = if (isDark) Color.White else Color.Black
    val backgroundColor = if (isDark) Color.Black else Teal
    val cardBackground = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF7A643).copy(alpha = 0.8f)
    val listBackground = if (isDark) Color(0xFF2C2C2C) else Color.White.copy(alpha = 0.9f)

    val muscleGroups = listOf(
        "Biceps", "Legs", "Core", "Chest", "Back", "Glutes"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Muscle Group",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            IconButton(onClick = { navController.navigate(AppRoutes.Settings) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar (Workout Name)
        TextField(
            value = workoutName,
            onValueChange = { workoutName = it },
            placeholder = { Text("Workout Name", color = textColor.copy(alpha = 0.5f)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = if (isDark) Color(0xFF2C2C2C) else Color.White.copy(alpha = 0.8f),
                unfocusedContainerColor = if (isDark) Color(0xFF2C2C2C) else Color.White.copy(alpha = 0.8f),
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            shape = RoundedCornerShape(28.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Muscle Group Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(360.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(muscleGroups) { group ->
                MuscleGroupCard(group, cardBackground, textColor) {
                    navController.navigate(AppRoutes.exerciseSelection(group))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Exercises List
        Text(
            text = "Current Workout Exercises",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(listBackground, RoundedCornerShape(16.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (newWorkoutExercises.isEmpty()) {
                item {
                    Text(
                        "No exercises added yet.",
                        modifier = Modifier.padding(16.dp),
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
            } else {
                items(newWorkoutExercises) { exercise ->
                    AddedExerciseItem(exercise, textColor) {
                        workoutViewModel.removeExerciseFromNewWorkout(exercise)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Build Button
        Button(
            onClick = {
                if (workoutName.isNotBlank() && newWorkoutExercises.isNotEmpty()) {
                    workoutViewModel.saveCustomWorkout(workoutName) {
                        navController.navigateUp()
                    }
                }
            },
            enabled = workoutName.isNotBlank() && newWorkoutExercises.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDark) Orange else Color(0xFFF7A643),
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Build", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun MuscleGroupCard(group: String, backgroundColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Placeholder for exercise icons - in real app we'd use local drawables
            Text(
                text = group,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AddedExerciseItem(exercise: Exercises, textColor: Color, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(exercise.name, fontWeight = FontWeight.Bold, color = textColor)
            Text(exercise.muscleGroup, fontSize = 12.sp, color = textColor.copy(alpha = 0.7f))
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Red)
        }
    }
}
