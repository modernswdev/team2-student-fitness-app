package com.team2.studentfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.team2.studentfitness.ui.navigation.AppRoutes
import com.team2.studentfitness.ui.screens.*
import com.team2.studentfitness.ui.theme.StudentFitnessTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.team2.studentfitness.viewmodels.LoginViewModel
import com.team2.studentfitness.viewmodels.OnboardingViewModel
import com.team2.studentfitness.viewmodels.SecurePinManager
import com.team2.studentfitness.viewmodels.WorkoutViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        lifecycleScope.launch(Dispatchers.IO) {
            (application as DatabaseCreation).database
        }

        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database = (application as DatabaseCreation).database
            val settingsDao = database.settingsDao()

            // Use Flow to observe theme changes in real-time across the app
            val latestSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
            val isDarkMode = latestSettings?.theme == 1

            StudentFitnessTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                val securePinManager = remember { SecurePinManager(context) }
                val loginViewModel = remember { LoginViewModel(securePinManager) }
                val onboardingViewModel = remember { OnboardingViewModel(application, securePinManager) }
                val workoutViewModel: WorkoutViewModel = viewModel()

                val startDestination = if (onboardingViewModel.isOnboardingCompleted()) {
                    if (securePinManager.isPinSet()) AppRoutes.Login else AppRoutes.Dashboard
                } else {
                    AppRoutes.Onboarding
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppRoutes.Onboarding) {
                            OnboardingScreen(
                                viewModel = onboardingViewModel,
                                onOnboardingComplete = {
                                    val nextDest = if (securePinManager.isPinSet()) AppRoutes.Login else AppRoutes.Dashboard
                                    navController.navigate(nextDest) {
                                        popUpTo(AppRoutes.Onboarding) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(AppRoutes.Login) {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navController.navigate(AppRoutes.Dashboard) {
                                        popUpTo(AppRoutes.Login) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(AppRoutes.DeveloperMenu) {
                            DeveloperMenuScreen(navController = navController)
                        }
                        composable(AppRoutes.Dashboard) {
                            Dashboard(navController = navController)
                        }
                        composable(AppRoutes.Settings) {
                            SettingsScreen(
                                onLogout = {
                                    val nextDest = if (securePinManager.isPinSet()) AppRoutes.Login else AppRoutes.Dashboard
                                    navController.navigate(nextDest) {
                                        popUpTo(AppRoutes.Dashboard) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(AppRoutes.VideoDemo) {
                            VideoDemoScreen()
                        }
                        composable(AppRoutes.OfficialYouTubeDemo) {
                            OfficialYouTubeDemoScreen()
                        }
                        composable(AppRoutes.Workouts) {
                            WorkoutScreen(navController = navController, workoutViewModel = workoutViewModel)
                        }
                        composable(AppRoutes.BuildWorkout) {
                            BuildWorkoutScreen(navController = navController, workoutViewModel = workoutViewModel)
                        }
                        composable(
                            route = AppRoutes.ExerciseSelection,
                            arguments = listOf(navArgument("muscleGroup") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val muscleGroup = backStackEntry.arguments?.getString("muscleGroup") ?: ""
                            ExerciseSelectionScreen(
                                navController = navController,
                                muscleGroup = muscleGroup,
                                workoutViewModel = workoutViewModel
                            )
                        }
                        composable(AppRoutes.Macros) {
                            MacroScreen(navController = navController)
                        }
                        composable(AppRoutes.WeightProgress) {
                            WeightProgressScreen(navController = navController)
                        }
                        composable(
                            route = AppRoutes.ExerciseListTemplate,
                            arguments = listOf(
                                navArgument("workoutId") { type = NavType.IntType },
                                navArgument("workoutName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getInt("workoutId") ?: 0
                            val workoutName = backStackEntry.arguments?.getString("workoutName") ?: "Exercises"
                            ExerciseListScreen(
                                navController = navController,
                                workoutId = workoutId,
                                workoutName = workoutName,
                                workoutViewModel = workoutViewModel
                            )
                        }
                        composable(
                            route = AppRoutes.DetailTemplate,
                            arguments = listOf(navArgument("feature") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val feature = backStackEntry.arguments?.getString("feature")
                            DetailScreen(navController = navController, feature = feature)
                        }
                    }
                }
            }
        }
    }
}
