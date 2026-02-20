package com.example.healthapitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthapitest.ui.theme.HealthAPITestTheme

/**
 * Onboarding Activity for Health Connect integration.
 * This activity is launched by Health Connect when users connect your app to Health Connect,
 * allowing you to provide additional onboarding experience beyond just permission granting.
 *
 * This is required by Health Connect API compliance checks and is triggered by:
 * - Android 13 and lower: androidx.health.ACTION_SHOW_ONBOARDING
 * - Android 14+: android.health.connect.action.SHOW_ONBOARDING
 */
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthAPITestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome to Health Connect",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = """
                                Your Health API Test App is now connected to Health Connect!
                                
                                With this connection, you can:
                                
                                ✓ View your daily steps and activity
                                ✓ Track your heart rate measurements
                                ✓ Monitor distance traveled
                                ✓ See calories burned
                                ✓ Track body temperature
                                ✓ View blood pressure readings
                                ✓ Monitor blood glucose levels
                                ✓ Check oxygen saturation
                                ✓ Review sleep data
                                ✓ Track exercise sessions
                                ✓ View nutrition information
                                ✓ Monitor body composition
                                
                                Your health data is always under your control. You can manage app permissions anytime through Health Connect settings.
                                
                                Let's get started!
                            """.trimIndent(),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Button(
                            onClick = { finish() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Get Started")
                        }
                    }
                }
            }
        }
    }
}

