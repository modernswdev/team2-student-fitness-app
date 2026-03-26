package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.studentfitness.ui.theme.CardBg
import com.team2.studentfitness.ui.theme.NeonOrange
import com.team2.studentfitness.ui.theme.NeonTeal
import com.team2.studentfitness.ui.theme.TextDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    onSaveProfile: (
        displayName: String,
        passcode: String,
        age: String,
        sex: String,
        height: String,
        weight: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onSaveWorkoutPreferences: (
        goal: String,
        activityLevel: String,
        targetWeight: String,
        dietaryPreference: String,
        useMetric: Boolean,
        weeklyGoal: Int
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    val gyms = remember {
        listOf("UALR Fitness Center", "Planet Fitness", "LA Fitness", "Anytime Fitness", "Other")
    }

    val sexOptions = remember {
        listOf("Male", "Female", "Other", "Prefer not to say")
    }

    val goalOptions = remember {
        listOf("Lose Fat", "Maintain", "Gain Muscle")
    }

    val activityOptions = remember {
        listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")
    }

    val dietaryOptions = remember {
        listOf("Balanced", "High Protein", "Lower Carb", "Vegetarian", "Vegan", "Other")
    }

    var displayName by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf(sexOptions.first()) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    var homeGym by remember { mutableStateOf(gyms.first()) }
    var gymDropdownExpanded by remember { mutableStateOf(false) }

    var remindersEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    var useMetric by remember { mutableStateOf(false) }
    var weeklyGoal by remember { mutableStateOf(3f) }

    var goal by remember { mutableStateOf(goalOptions[1]) }
    var activityLevel by remember { mutableStateOf(activityOptions[1]) }
    var targetWeight by remember { mutableStateOf("") }
    var dietaryPreference by remember { mutableStateOf(dietaryOptions.first()) }

    var sexExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }
    var activityExpanded by remember { mutableStateOf(false) }
    var dietaryExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeonTeal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "SETTINGS",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = NeonOrange,
                letterSpacing = (-1).sp
            )

            Text(
                text = "Make the app fit your routine.",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "Profile") {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Name") },
                    placeholder = { Text("Change name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = settingsTextFieldColors()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it },
                    label = { Text("Passcode") },
                    placeholder = { Text("Change passcode") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = settingsTextFieldColors()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit) },
                    label = { Text("Age") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = settingsTextFieldColors()
                )

                Spacer(Modifier.height(12.dp))

                DropdownField(
                    label = "Sex",
                    value = sex,
                    expanded = sexExpanded,
                    onExpandedChange = { sexExpanded = !sexExpanded },
                    options = sexOptions,
                    onOptionSelected = {
                        sex = it
                        sexExpanded = false
                    }
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text(if (useMetric) "Height (cm)" else "Height (in)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = settingsTextFieldColors()
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text(if (useMetric) "Weight (kg)" else "Weight (lb)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = settingsTextFieldColors()
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSaveProfile(
                            displayName,
                            passcode,
                            age,
                            sex,
                            height,
                            weight
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange)
                ) {
                    Text("Save Profile", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "Gym") {
                Text("Home gym", color = TextDim, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = gymDropdownExpanded,
                    onExpandedChange = { gymDropdownExpanded = !gymDropdownExpanded }
                ) {
                    TextField(
                        value = homeGym,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CardBg,
                            unfocusedContainerColor = CardBg,
                            focusedIndicatorColor = NeonOrange,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = gymDropdownExpanded)
                        },
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = gymDropdownExpanded,
                        onDismissRequest = { gymDropdownExpanded = false }
                    ) {
                        gyms.forEach { gym ->
                            DropdownMenuItem(
                                text = { Text(gym) },
                                onClick = {
                                    homeGym = gym
                                    gymDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                SettingsToggleRow(
                    label = "Gym reminders",
                    description = "Get nudges to stay consistent.",
                    checked = remindersEnabled,
                    onCheckedChange = { remindersEnabled = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "Workout") {
                Text("Units", color = TextDim, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = !useMetric,
                        onClick = { useMetric = false },
                        label = { Text("Imperial") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonOrange,
                            containerColor = CardBg
                        )
                    )

                    FilterChip(
                        selected = useMetric,
                        onClick = { useMetric = true },
                        label = { Text("Metric") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonOrange,
                            containerColor = CardBg
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                DropdownField(
                    label = "Primary goal",
                    value = goal,
                    expanded = goalExpanded,
                    onExpandedChange = { goalExpanded = !goalExpanded },
                    options = goalOptions,
                    onOptionSelected = {
                        goal = it
                        goalExpanded = false
                    }
                )

                Spacer(Modifier.height(12.dp))

                DropdownField(
                    label = "Activity level",
                    value = activityLevel,
                    expanded = activityExpanded,
                    onExpandedChange = { activityExpanded = !activityExpanded },
                    options = activityOptions,
                    onOptionSelected = {
                        activityLevel = it
                        activityExpanded = false
                    }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = targetWeight,
                    onValueChange = { targetWeight = it },
                    label = { Text(if (useMetric) "Target Weight (kg)" else "Target Weight (lb)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = settingsTextFieldColors()
                )

                Spacer(Modifier.height(12.dp))

                DropdownField(
                    label = "Dietary preference",
                    value = dietaryPreference,
                    expanded = dietaryExpanded,
                    onExpandedChange = { dietaryExpanded = !dietaryExpanded },
                    options = dietaryOptions,
                    onOptionSelected = {
                        dietaryPreference = it
                        dietaryExpanded = false
                    }
                )

                Spacer(Modifier.height(16.dp))

                Text("Weekly goal", color = TextDim, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${weeklyGoal.toInt()} days/week",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )

                Slider(
                    value = weeklyGoal,
                    onValueChange = { weeklyGoal = it },
                    valueRange = 0f..7f,
                    steps = 6,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonOrange,
                        activeTrackColor = NeonOrange,
                        inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                    )
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSaveWorkoutPreferences(
                            goal,
                            activityLevel,
                            targetWeight,
                            dietaryPreference,
                            useMetric,
                            weeklyGoal.toInt()
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange)
                ) {
                    Text("Save Workout Preferences", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingsSection(title = "App") {
                SettingsToggleRow(
                    label = "Dark mode",
                    description = "Switch theme appearance.",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )

                Spacer(Modifier.height(10.dp))

                SettingsClickableRow(
                    label = "About",
                    description = "Version, credits, and info.",
                    onClick = { }
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonOrange)
            ) {
                Text("Log Out", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = settingsTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = onExpandedChange
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NeonOrange
            )
        )
    }
}

@Composable
private fun SettingsClickableRow(
    label: String,
    description: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun settingsTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = NeonOrange,
        unfocusedBorderColor = Color.Black.copy(alpha = 0.25f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = NeonOrange,
        unfocusedLabelColor = Color.Black.copy(alpha = 0.7f),
        cursorColor = NeonOrange
    )
}
