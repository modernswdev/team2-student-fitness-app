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
 * Activity that displays the privacy policy and rationale for Health Connect permissions.
 * This is required by Health Connect API for compliance with Android 13 and lower,
 * and is triggered when users click the "privacy policy" link in Health Connect permissions screen.
 */
class PermissionsRationaleActivity : ComponentActivity() {
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
                            text = "Health Data Privacy Policy",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = """
                                This app requests access to your health data from Health Connect to:
                                
                                • Display your daily steps and activity
                                • Show your heart rate measurements
                                • Track distance traveled
                                • Monitor calories burned
                                • Display body temperature readings
                                • Show blood pressure measurements
                                • Track blood glucose levels
                                • Monitor oxygen saturation
                                • Display sleep data
                                • Track exercise sessions
                                • Monitor nutrition information
                                • Track body composition
                                • Display height and weight data
                                
                                Your health data is:
                                • Never shared with third parties
                                • Only used to display your personal health information
                                • Stored securely on your device
                                • Under your complete control via Health Connect permissions
                                
                                You can revoke these permissions at any time through the Health Connect app settings.
                            """.trimIndent(),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = { finish() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

