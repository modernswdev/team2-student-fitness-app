package com.team2.studentfitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team2.studentfitness.ui.theme.StudentFitnessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentFitnessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   // Pass the innerPadding to your LoginScreen so it respects the edge-to-edge layout
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
// Updated to accept a 'modifier' to handle the Scaffold padding correctly
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    StudentFitnessTheme {
        LoginScreen()
    }
}
