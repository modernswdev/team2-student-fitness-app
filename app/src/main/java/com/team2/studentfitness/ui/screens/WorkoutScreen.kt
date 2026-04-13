package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import com.team2.studentfitness.database.WorkEx
import com.team2.studentfitness.database.Workouts
import com.team2.studentfitness.ui.navigation.AppRoutes
import com.team2.studentfitness.ui.theme.*
import com.team2.studentfitness.viewmodels.WorkoutViewModel

@Composable
fun WorkoutScreen(navController: NavController, workoutViewModel: WorkoutViewModel = viewModel()) {
    val context = LocalContext.current
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()
    val userSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
    val isDark = userSettings?.theme == 1

    val workouts by workoutViewModel.workouts.collectAsState()
    val isLoading by workoutViewModel.isLoading.collectAsState()

    val textColor = if (isDark) Color.White else Color.Black
    val backgroundColor = if (isDark) Color.Black else Teal
    val cardBackground = if (isDark) Color(0xFF1E1E1E) else Color.White

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
                text = "Workout",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color.Black
            )
            IconButton(onClick = { navController.navigate(AppRoutes.Settings) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (isDark) Color.White else Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Difficulty and Muscle Group Selection Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Difficulty Selection (Left)
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF2C3E50) else Color(0xFF648CF4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Difficulty", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    DifficultyButton(1) { workoutViewModel.loadWorkoutsByDifficulty(0) }
                    DifficultyButton(2) { workoutViewModel.loadWorkoutsByDifficulty(1) }
                    DifficultyButton(3) { workoutViewModel.loadWorkoutsByDifficulty(2) }
                }
            }

            // Muscle Group Selection (Right)
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF5D4037) else Color(0xFFF7A643))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Muscle Group", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Removed "Glutes" as it was causing empty results in the current dataset
                    val muscleGroups = listOf("Biceps", "Legs", "Chest", "Core", "Back")
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        muscleGroups.forEach { group ->
                            MuscleGroupChip(group) { workoutViewModel.loadWorkoutsByMuscleGroup(group) }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Workout List
        Text(
            text = "Workouts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Orange)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(workouts) { workout ->
                    WorkoutItem(workout, cardBackground, textColor) {
                        navController.navigate(AppRoutes.exercises(workout.uid, workout.name))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Build Button
        Button(
            onClick = { /* Handle Build */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Orange else Color(0xFFF7A643)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Build", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DifficultyButton(stars: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = Color.White.copy(alpha = 0.9f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Icon(
                    imageVector = if (index < stars) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (index < stars) Color(0xFFF1C40F) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun MuscleGroupChip(group: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = group,
            modifier = Modifier.padding(vertical = 4.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun WorkoutItem(workout: Workouts, backgroundColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(workout.name, fontWeight = FontWeight.Bold, color = textColor)
                Text("${workout.duration} min • ${workout.focus}", style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.7f))
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "View Exercises",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
