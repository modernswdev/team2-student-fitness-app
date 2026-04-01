package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.database.UserSettings
import com.team2.studentfitness.ui.theme.Teal
import com.team2.studentfitness.ui.theme.Orange
import com.team2.studentfitness.viewmodels.SecurePinManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    onThemeChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()
    val securePinManager = remember { SecurePinManager(context) }

    val gyms = remember {
        listOf("UALR Fitness Center", "Planet Fitness", "LA Fitness", "Anytime Fitness", "Other")
    }

    var userSettings by remember { mutableStateOf<UserSettings?>(null) }
    
    var homeGym by remember { mutableStateOf(gyms.first()) }
    var gymDropdownExpanded by remember { mutableStateOf(false) }

    var remindersEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    var useMetric by remember { mutableStateOf(true) }
    var weeklyGoal by remember { mutableStateOf(3f) } // 0..7
    
    // PIN management
    var showPinDialog by remember { mutableStateOf(false) }
    var newPin by remember { mutableStateOf("") }
    var isPinSet by remember { mutableStateOf(securePinManager.isPinSet()) }

    LaunchedEffect(Unit) {
        userSettings = settingsDao.getLatest()
        userSettings?.let {
            useMetric = it.isMetric
            remindersEnabled = it.notifsOn
            darkModeEnabled = it.theme == 1
            homeGym = if (it.homeGym in gyms.indices) gyms[it.homeGym] else gyms.first()
        }
    }

    val cardBg = if (darkModeEnabled) Color(0xFF1E1E1E) else Color(0xFFFAF3F3)
    val textColor = if (darkModeEnabled) Color.White else Color.Black
    val secondaryTextColor = if (darkModeEnabled) Color.LightGray else Color.Gray

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = "SETTINGS",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = if (darkModeEnabled) Teal else Orange,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Make the app fit your routine.",
                color = textColor.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Spacer(Modifier.height(16.dp))
            //  Gym Settings
            SettingsSection(title = "Gym", cardBg = cardBg, textColor = textColor) {
                Text("Home gym", color = secondaryTextColor, fontSize = 13.sp)
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
                            focusedContainerColor = if (darkModeEnabled) Color.DarkGray else Color.White,
                            unfocusedContainerColor = if (darkModeEnabled) Color.DarkGray else Color.White,
                            focusedIndicatorColor = if (darkModeEnabled) Teal else Orange,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gymDropdownExpanded) },
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = gymDropdownExpanded,
                        onDismissRequest = { gymDropdownExpanded = false }
                    ) {
                        gyms.forEachIndexed { index, gym ->
                            DropdownMenuItem(
                                text = { Text(gym) },
                                onClick = {
                                    homeGym = gym
                                    gymDropdownExpanded = false
                                    userSettings?.let { settings ->
                                        scope.launch { settingsDao.updateHomeGym(index, settings.uid) }
                                    }
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
                    onCheckedChange = { 
                        remindersEnabled = it
                        userSettings?.let { settings ->
                            scope.launch { settingsDao.updateNotifs(it, settings.uid) }
                        }
                    },
                    accentColor = if (darkModeEnabled) Teal else Orange,
                    textColor = textColor
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---- Workout Section ----
            SettingsSection(title = "Workout", cardBg = cardBg, textColor = textColor) {
                Text("Units", color = secondaryTextColor, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = !useMetric,
                        onClick = { 
                            useMetric = false
                            userSettings?.let { settings ->
                                scope.launch { settingsDao.updateIsMetric(false, settings.uid) }
                            }
                        },
                        label = { Text("Imperial") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (darkModeEnabled) Teal else Orange,
                            containerColor = if (darkModeEnabled) Color.DarkGray else Color.White,
                            selectedLabelColor = Color.White,
                            labelColor = textColor
                        )
                    )
                    FilterChip(
                        selected = useMetric,
                        onClick = { 
                            useMetric = true
                            userSettings?.let { settings ->
                                scope.launch { settingsDao.updateIsMetric(true, settings.uid) }
                            }
                        },
                        label = { Text("Metric") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (darkModeEnabled) Teal else Orange,
                            containerColor = if (darkModeEnabled) Color.DarkGray else Color.White,
                            selectedLabelColor = Color.White,
                            labelColor = textColor
                        )
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text("Weekly goal", color = secondaryTextColor, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${weeklyGoal.toInt()} days/week",
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )

                Slider(
                    value = weeklyGoal,
                    onValueChange = { weeklyGoal = it },
                    valueRange = 0f..7f,
                    steps = 6, 
                    colors = SliderDefaults.colors(
                        thumbColor = if (darkModeEnabled) Teal else Orange,
                        activeTrackColor = if (darkModeEnabled) Teal else Orange,
                        inactiveTrackColor = textColor.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---- App Section ----
            SettingsSection(title = "App", cardBg = cardBg, textColor = textColor) {
                SettingsToggleRow(
                    label = "Dark mode",
                    description = "Switch theme appearance.",
                    checked = darkModeEnabled,
                    onCheckedChange = { 
                        darkModeEnabled = it
                        onThemeChanged(it)
                        userSettings?.let { settings ->
                            scope.launch { settingsDao.updateDarkMode(if (it) 1 else 0, settings.uid) }
                        }
                    },
                    accentColor = if (darkModeEnabled) Teal else Orange,
                    textColor = textColor
                )

                Spacer(Modifier.height(10.dp))
                
                SettingsClickableRow(
                    label = if (isPinSet) "Change PIN" else "Set PIN",
                    description = "Secure your app access.",
                    textColor = textColor,
                    onClick = { showPinDialog = true }
                )

                if (isPinSet) {
                    Spacer(Modifier.height(10.dp))
                    SettingsClickableRow(
                        label = "Clear PIN",
                        description = "Remove security requirement.",
                        textColor = textColor,
                        onClick = {
                            securePinManager.clearPin()
                            isPinSet = false
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))

                SettingsClickableRow(
                    label = "About",
                    description = "Version, credits, and info.",
                    textColor = textColor,
                    onClick = { /* UI-only */ }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (darkModeEnabled) Teal else Orange)
            ) {
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))
        }
    }
    
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Set Secure PIN") },
            text = {
                Column {
                    Text("Enter a 4-digit numeric PIN.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) newPin = it },
                        label = { Text("PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newPin.length == 4) {
                            securePinManager.setPin(newPin.toInt())
                            isPinSet = true
                            showPinDialog = false
                            newPin = ""
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPinDialog = false
                    newPin = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    cardBg: Color,
    textColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = textColor, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = textColor, fontWeight = FontWeight.SemiBold)
            Text(description, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor
            )
        )
    }
}

@Composable
private fun SettingsClickableRow(
    label: String,
    description: String,
    textColor: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(label, color = textColor, fontWeight = FontWeight.SemiBold)
            Text(description, color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
