package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.database.UserSettings
import com.team2.studentfitness.ui.theme.Teal
import com.team2.studentfitness.ui.theme.Orange
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()

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

    LaunchedEffect(Unit) {
        userSettings = settingsDao.getLatest()
        userSettings?.let {
            useMetric = it.isMetric
            remindersEnabled = it.notifsOn
            // ... load others if needed
        }
    }

    val cardBg = Color(0xFFFAF3F3)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Teal)
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
                color = Orange,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Make the app fit your routine.",
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Spacer(Modifier.height(16.dp))
            //  Gym Settings
            SettingsSection(title = "Gym", cardBg = cardBg) {
                // Home Gym Dropdown
                Text("Home gym", color = Color.Gray, fontSize = 13.sp)
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
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Orange,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gymDropdownExpanded) },
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
                    onCheckedChange = { 
                        remindersEnabled = it
                        userSettings?.let { settings ->
                            scope.launch { settingsDao.updateNotifs(it, settings.uid) }
                        }
                    },
                    orange = Orange
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---- Workout Section ----
            SettingsSection(title = "Workout", cardBg = cardBg) {
                Text("Units", color = Color.Gray, fontSize = 13.sp)
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
                            selectedContainerColor = Orange,
                            containerColor = Color.White,
                            selectedLabelColor = Color.White
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
                            selectedContainerColor = Orange,
                            containerColor = Color.White,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text("Weekly goal", color = Color.Gray, fontSize = 13.sp)
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
                    steps = 6, // gives integer stops
                    colors = SliderDefaults.colors(
                        thumbColor = Orange,
                        activeTrackColor = Orange,
                        inactiveTrackColor = Color.Black.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---- App Section ----
            SettingsSection(title = "App", cardBg = cardBg) {
                SettingsToggleRow(
                    label = "Dark mode",
                    description = "Switch theme appearance.",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it },
                    orange = Orange
                )

                Spacer(Modifier.height(10.dp))

                SettingsClickableRow(
                    label = "About",
                    description = "Version, credits, and info.",
                    onClick = { /* UI-only */ }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    cardBg: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
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
    orange: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.Black, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color.Black.copy(alpha = 0.7f), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = orange
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
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, color = Color.Black, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color.Black.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
