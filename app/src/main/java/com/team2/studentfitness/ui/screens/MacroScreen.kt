package com.team2.studentfitness.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.database.MacroLog
import com.team2.studentfitness.database.UserSettings
import com.team2.studentfitness.database.HealthData
import com.team2.studentfitness.ui.theme.*
import com.team2.studentfitness.viewmodels.HealthCalculations
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = (context.applicationContext as DatabaseCreation).database
    val macroLogDao = database.macroLogDao()
    val settingsDao = database.settingsDao()
    val healthDao = database.healthDao()

    val macroLogs by macroLogDao.getAllFlow().collectAsState(initial = emptyList())
    val userSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
    var healthData by remember { mutableStateOf<HealthData?>(null) }

    LaunchedEffect(Unit) {
        healthData = healthDao.getLatest()
    }
    
    val isDark = userSettings?.theme == 1
    val backgroundColor = if (isDark) Color(0xFF121212) else Cream
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    var showAddMeal by remember { mutableStateOf(false) }
    var showLogs by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var showCustomize by remember { mutableStateOf(false) }

    // Goal Calculations
    val healthCalc = remember { HealthCalculations() }
    val calorieGoal = if (healthData != null && userSettings != null) {
        val bmr = healthCalc.calculateBmrMifflinStJeor(
            weightKg = healthData!!.weight.toDouble(),
            heightCm = healthData!!.height.toDouble(),
            ageYears = healthData!!.age,
            sex = try { HealthCalculations.Sex.valueOf(userSettings!!.sex) } catch(_: Exception) { HealthCalculations.Sex.MALE }
        )
        val activityLevel = try { 
            HealthCalculations.ActivityLevel.valueOf(userSettings!!.activityLevel) 
        } catch(_: Exception) { 
            HealthCalculations.ActivityLevel.SEDENTARY 
        }
        healthCalc.calculateTdee(bmr, activityLevel).toInt()
    } else {
        2000
    }

    // Macro goals: standard split + estimates for others
    val proteinGoal = (calorieGoal * 0.30 / 4).toInt()
    val carbGoal = (calorieGoal * 0.40 / 4).toInt()
    val fatGoal = (calorieGoal * 0.30 / 9).toInt()
    val fiberGoal = 25 // Generic estimate
    val sugarGoal = (calorieGoal * 0.10 / 4).toInt() // Max 10% of calories
    val sodiumGoal = 2300 // Generic estimate (mg)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Macro Preview", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier.padding(end = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Orange
                    ) {
                        Text(
                            "Today",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "A quick look at your estimated daily nutrition targets.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.7f)
            )

            // Daily Calories Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Calories", fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    val totalCals = macroLogs.filter { isToday(it.timestamp) }.sumOf { it.calories }
                    Text(
                        "$totalCals / $calorieGoal kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                }
            }

            // Today's Intake Card
            TodayIntakeCard(
                logs = macroLogs,
                goals = mapOf(
                    "Protein" to proteinGoal,
                    "Carbs" to carbGoal,
                    "Fat" to fatGoal,
                    "Calories" to calorieGoal,
                    "Fiber" to fiberGoal,
                    "Sugar" to sugarGoal,
                    "Sodium" to sodiumGoal
                ),
                userSettings = userSettings,
                isDark = isDark,
                cardColor = cardColor,
                textColor = textColor,
                onAddClick = { showAddMeal = true },
                onMoreClick = { showCustomize = true }
            )

            Text(
                "Based on your profile, activity level, and workout goal. These values update when your targets or logged meals change.",
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.6f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showDetails = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Details", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { showLogs = !showLogs },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color.DarkGray else Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(if (showLogs) "Hide Logs" else "Show Logs", color = textColor, fontWeight = FontWeight.Bold)
                }
            }

            if (showLogs) {
                MacroLogsSection(macroLogs, isDark, cardColor, textColor)
            }

            if (showAddMeal) {
                AddMealDialog(
                    isDark = isDark,
                    onDismiss = { showAddMeal = false },
                    onSave = { log ->
                        scope.launch {
                            macroLogDao.insert(log)
                            showAddMeal = false
                        }
                    }
                )
            }

            if (showDetails) {
                MacroDetailsDialog(
                    logs = macroLogs.filter { isToday(it.timestamp) },
                    isDark = isDark,
                    onDismiss = { showDetails = false }
                )
            }

            if (showCustomize) {
                CustomizeMacrosDialog(
                    userSettings = userSettings,
                    isDark = isDark,
                    onDismiss = { showCustomize = false },
                    onSave = { updatedSettings ->
                        scope.launch {
                            settingsDao.update(updatedSettings)
                            showCustomize = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TodayIntakeCard(
    logs: List<MacroLog>,
    goals: Map<String, Int>,
    userSettings: UserSettings?,
    isDark: Boolean,
    cardColor: Color,
    textColor: Color,
    onAddClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val todayLogs = logs.filter { isToday(it.timestamp) }
    val protein = todayLogs.sumOf { it.protein }
    val carbs = todayLogs.sumOf { it.carbs }
    val fat = todayLogs.sumOf { it.fat }
    val calories = todayLogs.sumOf { it.calories }
    val fiber = todayLogs.sumOf { it.fiber }
    val sugar = todayLogs.sumOf { it.sugar }
    val sodium = todayLogs.sumOf { it.sodium }

    val visibleMacros = userSettings?.visibleMacros?.split(",")?.filter { it.isNotBlank() } ?: listOf("Protein", "Carbs", "Fat", "Calories")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Today's Intake", fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Meal", modifier = Modifier.size(16.dp), tint = textColor)
                    }
                }
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = textColor.copy(alpha = 0.5f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleMacros.chunked(2).forEach { rowMacros ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowMacros.forEach { macro ->
                            val macroTrimmed = macro.trim()
                            val (value, goal, label, unit) = when (macroTrimmed) {
                                "Protein" -> Quadruple(protein, goals["Protein"] ?: 0, "PROTEIN", "g")
                                "Carbs" -> Quadruple(carbs, goals["Carbs"] ?: 0, "CARBS", "g")
                                "Fat" -> Quadruple(fat, goals["Fat"] ?: 0, "FAT", "g")
                                "Calories" -> Quadruple(calories, goals["Calories"] ?: 0, "CALORIES", "kcal")
                                "Fiber" -> Quadruple(fiber, goals["Fiber"] ?: 0, "FIBER", "g")
                                "Sugar" -> Quadruple(sugar, goals["Sugar"] ?: 0, "SUGAR", "g")
                                "Sodium" -> Quadruple(sodium, goals["Sodium"] ?: 0, "SODIUM", "mg")
                                else -> Quadruple(0, 1, macroTrimmed.uppercase(), "")
                            }
                            MacroItem(
                                label = label,
                                value = "$value$unit",
                                goal = "$goal$unit",
                                progress = if (goal > 0) value.toFloat() / goal else 0f,
                                modifier = Modifier.weight(1f),
                                textColor = textColor
                            )
                        }
                        if (rowMacros.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun MacroItem(label: String, value: String, goal: String, progress: Float, modifier: Modifier, textColor: Color) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (label == "CALORIES") Orange else Teal,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("$value / $goal", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun MacroLogsSection(logs: List<MacroLog>, isDark: Boolean, cardColor: Color, textColor: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Historical Logs", fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(vertical = 8.dp))
        
        if (logs.isEmpty()) {
            Text("No logs yet. Start by adding a meal!", color = textColor.copy(alpha = 0.6f))
        } else {
            logs.forEach { log ->
                LogEntryItem(log, cardColor, textColor)
            }
        }
    }
}

@Composable
fun LogEntryItem(log: MacroLog, cardColor: Color, textColor: Color) {
    val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(log.timestamp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(log.name, fontWeight = FontWeight.Bold, color = textColor)
                Text(date, style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${log.calories} kcal | P: ${log.protein}g C: ${log.carbs}g F: ${log.fat}g",
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun MacroDetailsDialog(logs: List<MacroLog>, isDark: Boolean, onDismiss: () -> Unit) {
    val totalP = logs.sumOf { it.protein }
    val totalC = logs.sumOf { it.carbs }
    val totalF = logs.sumOf { it.fat }
    val totalCal = logs.sumOf { it.calories }
    val totalFiber = logs.sumOf { it.fiber }
    val totalSugar = logs.sumOf { it.sugar }
    val totalSodium = logs.sumOf { it.sodium }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Macro Rundown", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DetailRow("Calories", "$totalCal kcal", isDark)
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                DetailRow("Protein", "${totalP}g", isDark)
                DetailRow("Carbohydrates", "${totalC}g", isDark)
                DetailRow("Fat", "${totalF}g", isDark)
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                DetailRow("Fiber", "${totalFiber}g", isDark)
                DetailRow("Sugar", "${totalSugar}g", isDark)
                DetailRow("Sodium", "${totalSodium}mg", isDark)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Orange)) {
                Text("Close", color = Color.White)
            }
        },
        containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    )
}

@Composable
fun DetailRow(label: String, value: String, isDark: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = if (isDark) Color.LightGray else Color.DarkGray)
        Text(value, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
    }
}

@Composable
fun CustomizeMacrosDialog(userSettings: UserSettings?, isDark: Boolean, onDismiss: () -> Unit, onSave: (UserSettings) -> Unit) {
    val allMacros = listOf("Protein", "Carbs", "Fat", "Calories", "Fiber", "Sugar", "Sodium")
    var selectedMacros by remember { 
        mutableStateOf(userSettings?.visibleMacros?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.toSet() ?: listOf("Protein", "Carbs", "Fat", "Calories").toSet()) 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Customize Dashboard", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Select which macros to show on your today card:", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(16.dp))
                allMacros.forEach { macro ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedMacros = if (selectedMacros.contains(macro)) {
                                    selectedMacros - macro
                                } else {
                                    selectedMacros + macro
                                }
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selectedMacros.contains(macro),
                            onCheckedChange = null // Handled by row click
                        )
                        Text(macro, modifier = Modifier.padding(start = 8.dp), color = if (isDark) Color.White else Color.Black)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    userSettings?.let {
                        onSave(it.copy(visibleMacros = selectedMacros.joinToString(",")))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = if (isDark) Color.White else Color.Black)
            }
        },
        containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    )
}

@Composable
fun AddMealDialog(isDark: Boolean, onDismiss: () -> Unit, onSave: (MacroLog) -> Unit) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var sodium by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && calories.isNotEmpty()) {
                        onSave(
                            MacroLog(
                                name = name,
                                calories = calories.toIntOrNull() ?: 0,
                                protein = protein.toIntOrNull() ?: 0,
                                carbs = carbs.toIntOrNull() ?: 0,
                                fat = fat.toIntOrNull() ?: 0,
                                fiber = fiber.toIntOrNull() ?: 0,
                                sugar = sugar.toIntOrNull() ?: 0,
                                sodium = sodium.toIntOrNull() ?: 0
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Save Meal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = if (isDark) Color.White else Color.Black)
            }
        },
        title = { Text("Add Meal", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Meal Name") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein (g)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Fat (g)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fiber, onValueChange = { fiber = it }, label = { Text("Fiber (g)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = sugar, onValueChange = { sugar = it }, label = { Text("Sugar (g)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                OutlinedTextField(value = sodium, onValueChange = { sodium = it }, label = { Text("Sodium (mg)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        containerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    )
}

private fun isToday(timestamp: Long): Boolean {
    val cal = Calendar.getInstance()
    val today = cal.get(Calendar.DAY_OF_YEAR)
    val year = cal.get(Calendar.YEAR)
    
    val logCal = Calendar.getInstance()
    logCal.timeInMillis = timestamp
    return today == logCal.get(Calendar.DAY_OF_YEAR) && year == logCal.get(Calendar.YEAR)
}
