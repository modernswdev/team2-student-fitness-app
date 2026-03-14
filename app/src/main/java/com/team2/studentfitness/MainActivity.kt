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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.team2.studentfitness.ui.screens.LoginScreen
import com.team2.studentfitness.ui.theme.Dashboard
import com.team2.studentfitness.ui.theme.DetailScreen
import com.team2.studentfitness.ui.theme.StudentFitnessTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        lifecycleScope.launch(Dispatchers.IO) {
            (application as DatabaseCreation).database
        }

        enableEdgeToEdge()
        setContent {
            StudentFitnessTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                onBypassLogin = { navController.navigate("dashboard") }
                            )
                        }
                        composable("dashboard") {
                            Dashboard(navController = navController)
                        }
                        composable(
                            route = "detail/{feature}",
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
