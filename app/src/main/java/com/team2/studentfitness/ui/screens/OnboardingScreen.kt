package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.team2.studentfitness.viewmodels.OnboardingViewModel
import com.team2.studentfitness.viewmodels.HealthCalculations

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var setPin by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    
    var isMetric by remember { mutableStateOf(true) }
    var selectedSex by remember { mutableStateOf(HealthCalculations.Sex.MALE) }
    var selectedActivityLevel by remember { mutableStateOf(HealthCalculations.ActivityLevel.SEDENTARY) }

    val scrollState = rememberScrollState()

    // Explicitly set background to white for readability
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Welcome! Let's get started.", 
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Text("Unit Preference", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isMetric, 
                    onClick = { isMetric = true },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text("Metric (kg, cm)", color = Color.Black)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = !isMetric, 
                    onClick = { isMetric = false },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text("Imperial (lb, in)", color = Color.Black)
            }

            Text("Personal Info", style = MaterialTheme.typography.titleMedium, color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedSex == HealthCalculations.Sex.MALE, 
                    onClick = { selectedSex = HealthCalculations.Sex.MALE },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text("Male", color = Color.Black)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = selectedSex == HealthCalculations.Sex.FEMALE, 
                    onClick = { selectedSex = HealthCalculations.Sex.FEMALE },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text("Female", color = Color.Black)
            }

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text(if (isMetric) "Weight (kg)" else "Weight (lb)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text(if (isMetric) "Height (cm)" else "Height (in)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Text("Activity Level", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            HealthCalculations.ActivityLevel.entries.forEach { level ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().clickable { selectedActivityLevel = level }
                ) {
                    RadioButton(
                        selected = selectedActivityLevel == level, 
                        onClick = { selectedActivityLevel = level },
                        colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                    )
                    Text(
                        level.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        color = Color.Black
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = setPin, 
                    onCheckedChange = { setPin = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color.Black)
                )
                Text("Set up a PIN for future logins?", color = Color.Black)
            }

            if (setPin) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("PIN (Numbers only)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }

            Button(
                onClick = {
                    viewModel.completeOnboarding(
                        name = name,
                        age = age.toIntOrNull() ?: 0,
                        weight = weight.toFloatOrNull() ?: 0f,
                        height = height.toFloatOrNull() ?: 0f,
                        pin = if (setPin) pin.toIntOrNull() else null,
                        isMetric = isMetric,
                        sex = selectedSex.name,
                        activityLevel = selectedActivityLevel.name,
                        onComplete = onOnboardingComplete
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && age.isNotBlank() && weight.isNotBlank() && height.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text("Complete Onboarding")
            }
        }
    }
}
