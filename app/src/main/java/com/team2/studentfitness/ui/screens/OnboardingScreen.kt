package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.team2.studentfitness.viewmodels.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var bodyTemp by remember { mutableStateOf("") }
    var totalSteps by remember { mutableStateOf("") }
    var setPin by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Welcome! Let's get started.", style = MaterialTheme.typography.headlineMedium)
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Biometric Info", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = heartRate,
            onValueChange = { heartRate = it },
            label = { Text("Heart Rate") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bodyTemp,
            onValueChange = { bodyTemp = it },
            label = { Text("Body Temp") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = totalSteps,
            onValueChange = { totalSteps = it },
            label = { Text("Total Steps") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = setPin, onCheckedChange = { setPin = it })
            Text("Set up a PIN for future logins?")
        }

        if (setPin) {
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN (Numbers only)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                viewModel.completeOnboarding(
                    name = name,
                    password = password,
                    heartRate = heartRate.toIntOrNull() ?: 0,
                    bodyTemp = bodyTemp.toIntOrNull() ?: 0,
                    totalSteps = totalSteps.toIntOrNull() ?: 0,
                    pin = if (setPin) pin.toIntOrNull() else null,
                    onComplete = onOnboardingComplete
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Onboarding")
        }
    }
}
