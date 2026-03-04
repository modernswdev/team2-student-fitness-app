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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team2.studentfitness.ui.theme.NeonTeal
import com.team2.studentfitness.ui.theme.NeonOrange
import com.team2.studentfitness.ui.theme.CardBg
import com.team2.studentfitness.ui.theme.TextDim


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    // Optional callbacks if your team later wires backend/navigation
    onLogout: () -> Unit = {},
) {
    // ---- UI-only state (frontend) ----
    val gyms = remember {
        listOf("UALR Fitness Center", "Planet Fitness", "LA Fitness", "Anytime Fitness", "Other")
    }

    var homeGym by remember { mutableStateOf(gyms.first()) }
    var gymDropdownExpanded by remember { mutableStateOf(false) }

    var remindersEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    var useMetric by remember { mutableStateOf(false) } // false = imperial
    var weeklyGoal by remember { mutableStateOf(3f) } // 0..7

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
            // Title
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
            //  Gym Settings
            SettingsSection(title = "Gym") {
                // Home Gym Dropdown
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
                    onCheckedChange = { remindersEnabled = it }
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---- Workout Section ----
            SettingsSection(title = "Workout") {
                Text("Units", color = TextDim, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                // Simple 2-option segmented style using FilterChips
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

                Spacer(Modifier.height(18.dp))

                Text("Weekly goal", color = TextDim, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${weeklyGoal.toInt()} days/week",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Slider(
                    value = weeklyGoal,
                    onValueChange = { weeklyGoal = it },
                    valueRange = 0f..7f,
                    steps = 6, // gives integer stops
                    colors = SliderDefaults.colors(
                        thumbColor = NeonOrange,
                        activeTrackColor = NeonOrange,
                        inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // ---- App Section ----
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
                    onClick = { /* UI-only: could show dialog later */ }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Logout
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
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
            Text(title, fontWeight = FontWeight.Bold, color = Color.Black)
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
    onCheckedChange: (Boolean) -> Unit
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
            Text(label, color = Color.Black, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color.Black.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}
