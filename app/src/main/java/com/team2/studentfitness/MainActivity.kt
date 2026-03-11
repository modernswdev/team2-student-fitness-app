package com.team2.studentfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.team2.studentfitness.ui.navigation.AppRoutes
import com.team2.studentfitness.ui.screens.DeveloperMenuScreen
import com.team2.studentfitness.ui.screens.DetailScreen
import com.team2.studentfitness.ui.screens.Dashboard
import com.team2.studentfitness.ui.screens.LoginScreen
import com.team2.studentfitness.ui.screens.SettingsScreen
import com.team2.studentfitness.ui.theme.StudentFitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentFitnessTheme {
                val navController = rememberNavController()
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        if (currentDestination != AppRoutes.DeveloperMenu) {
                            FloatingActionButton(onClick = { navController.navigate(AppRoutes.DeveloperMenu) }) {
                                Icon(imageVector = Icons.Default.Build, contentDescription = "Open developer menu")
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.Login,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(AppRoutes.Login) {
                            LoginScreen(
                                onOpenDevMenu = { navController.navigate(AppRoutes.DeveloperMenu) }
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
                                onLogout = { navController.navigate(AppRoutes.Login) }
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
