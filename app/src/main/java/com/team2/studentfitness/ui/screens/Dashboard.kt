package com.team2.studentfitness.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.R
import com.team2.studentfitness.database.HealthData
import com.team2.studentfitness.database.MentalHealth
import com.team2.studentfitness.database.UserSettings
import com.team2.studentfitness.ui.navigation.AppRoutes
import com.team2.studentfitness.ui.theme.*
import com.team2.studentfitness.viewmodels.HealthCalculations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun Dashboard(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = (context.applicationContext as DatabaseCreation).database
    val settingsDao = database.settingsDao()
    val mentalHealthDao = database.mentalHealthDao()
    val healthDao = database.healthDao()

    var userName by remember { mutableStateOf("Student") }
    var selectedTab by remember { mutableStateOf("Workout") }
    
    // Health State
    var latestHealthData by remember { mutableStateOf<HealthData?>(null) }
    var userSettings by remember { mutableStateOf<UserSettings?>(null) }
    
    // Mental Health State
    var selectedMood by remember { mutableStateOf("") }
    var reflectionText by remember { mutableStateOf("") }
    var mentalHealthLogs by remember { mutableStateOf<List<MentalHealth>>(emptyList()) }
    var showLogs by remember { mutableStateOf(false) }

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        else -> "Good Evening,"
    }

    LaunchedEffect(Unit) {
        try {
            userSettings = settingsDao.getLatest()
            userSettings?.let {
                userName = it.name
            }
            latestHealthData = healthDao.getLatest()
            mentalHealthLogs = mentalHealthDao.getAll()
        } catch (_: Exception) {
            // Suppress unused exception warning
        }
    }

    val isDark = userSettings?.theme == 1
    val textColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor.copy(alpha = 0.8f)
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .clickable { navController.navigate(AppRoutes.Settings) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color.DarkGray else Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabButton(
                    text = "Workout",
                    isSelected = selectedTab == "Workout",
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = "Workout" }
                )
                TabButton(
                    text = "Mental Health",
                    isSelected = selectedTab == "Mental Health",
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTab = "Mental Health" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content Area
            if (selectedTab == "Workout") {
                WorkoutTabContent(navController, latestHealthData, userSettings)
            } else {
                MentalHealthTabContent(
                    selectedMood = selectedMood,
                    onMoodSelected = { selectedMood = it },
                    reflectionText = reflectionText,
                    onReflectionChanged = { reflectionText = it },
                    userSettings = userSettings,
                    logs = mentalHealthLogs,
                    showLogs = showLogs,
                    onToggleLogs = { showLogs = !showLogs },
                    onSave = {
                        scope.launch {
                            mentalHealthDao.insert(
                                MentalHealth(mood = selectedMood, reflection = reflectionText)
                            )
                            reflectionText = ""
                            selectedMood = ""
                            mentalHealthLogs = mentalHealthDao.getAll()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, isDark: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else (if (isDark) Color.White else Color.Black),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GymStatusCard(userSettings: UserSettings?) {
    val gymNames = listOf("UALR Fitness Center", "Planet Fitness", "LA Fitness", "Anytime Fitness", "Other")
    val gymIndex = userSettings?.homeGym ?: 0
    val gymName = if (gymIndex in gymNames.indices) gymNames[gymIndex] else "Gym"
    
    val (hoursText, isOpen) = remember(gymIndex) {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        if (gymIndex == 0) { // UALR Fitness Center
            val (open, close, text) = when (dayOfWeek) {
                Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY -> Triple(6, 23, "6 a.m. – 11 p.m.")
                Calendar.FRIDAY -> Triple(6, 19, "6 a.m. – 7 p.m.")
                Calendar.SATURDAY -> Triple(11, 19, "11 a.m. – 7 p.m.")
                Calendar.SUNDAY -> Triple(11, 23, "11 a.m. – 11 p.m.")
                else -> Triple(0, 0, "Closed")
            }
            Triple(text, hour in open until close, Unit)
        } else {
            Triple("24 Hours", true, Unit)
        }
    }.let { it.first to it.second }

    val isDark = userSettings?.theme == 1
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFAF3F3)
    val textColor = if (isDark) Color.White else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = gymName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = "Today: $hoursText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isOpen) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFF44336).copy(alpha = 0.2f)
            ) {
                Text(
                    text = if (isOpen) "OPEN" else "CLOSED",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = if (isOpen) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun WorkoutTabContent(navController: NavController, healthData: HealthData?, userSettings: UserSettings?) {
    val healthCalc = remember { HealthCalculations() }
    val isDark = userSettings?.theme == 1
    val textColor = if (isDark) Color.White else Color.Black
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFAF3F3)
    
    val calorieGoal = if (healthData != null && userSettings != null) {
        val bmr = healthCalc.calculateBmrMifflinStJeor(
            weightKg = healthData.weight.toDouble(),
            heightCm = healthData.height.toDouble(),
            ageYears = healthData.age,
            sex = try { HealthCalculations.Sex.valueOf(userSettings.sex) } catch(_: Exception) { HealthCalculations.Sex.MALE }
        )
        val activityLevel = try { 
            HealthCalculations.ActivityLevel.valueOf(userSettings.activityLevel) 
        } catch(_: Exception) { 
            HealthCalculations.ActivityLevel.SEDENTARY 
        }
        healthCalc.calculateTdee(bmr, activityLevel).toInt()
    } else {
        2000 // Default
    }

    val weightDisplay = if (healthData != null) {
        if (userSettings?.isMetric != false) {
            String.format(Locale.US, "%.2fkg", healthData.weight)
        } else {
            "${(healthData.weight * 2.20462f).toInt()}lb"
        }
    } else {
        "--"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        GymStatusCard(userSettings)

        // Workout Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color.DarkGray else Color(0xFF648CF4))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Today's Progress",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Workout Pending", 
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isDark) Color.LightGray else Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate(AppRoutes.Workouts) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text("Start Workout", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0f }, 
                        modifier = Modifier.size(80.dp),
                        color = Color.White,
                        strokeWidth = 8.dp,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Health Stat Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Weight",
                value = weightDisplay,
                icon = Icons.Default.MonitorWeight,
                modifier = Modifier.weight(1f),
                cardBg = cardBg,
                textColor = textColor,
                onClick = { navController.navigate(AppRoutes.detail("Weight")) }
            )
            StatCard(
                title = "Calories",
                value = "0 / $calorieGoal", 
                icon = Icons.Default.Restaurant,
                modifier = Modifier.weight(1f),
                cardBg = cardBg,
                textColor = textColor,
                onClick = { navController.navigate(AppRoutes.detail("Calories")) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    cardBg: Color = Color(0xFFFAF3F3),
    textColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = textColor)
            }
        }
    }
}

@Composable
fun BreathingSection(userSettings: UserSettings?) {
    val isDark = userSettings?.theme == 1
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFAF3F3)
    val textColor = if (isDark) Color.White else Color.Black
    val accentColor = if (isDark) Teal else Color(0xFFB099FF)

    var isRunning by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("Ready") }
    var timeLeft by remember { mutableIntStateOf(4) }

    val animationProgress by animateFloatAsState(
        targetValue = when (phase) {
            "Inhale" -> 1f
            "Hold " -> 1f // Space added to distinguish from second hold if needed
            "Exhale" -> 0.3f
            "Hold" -> 0.3f
            else -> 0.3f
        },
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
        label = "BreathingAnimation"
    )

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                phase = "Inhale"
                for (i in 4 downTo 1) {
                    timeLeft = i
                    delay(1000)
                }
                phase = "Hold "
                for (i in 4 downTo 1) {
                    timeLeft = i
                    delay(1000)
                }
                phase = "Exhale"
                for (i in 4 downTo 1) {
                    timeLeft = i
                    delay(1000)
                }
                phase = "Hold"
                for (i in 4 downTo 1) {
                    timeLeft = i
                    delay(1000)
                }
            }
        } else {
            phase = "Ready"
            timeLeft = 4
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Guided Breathing",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = "Box breathing helps reduce stress",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                // Animated Circle
                Box(
                    modifier = Modifier
                        .size(140.dp * animationProgress)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f))
                )
                
                // Static outer border
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = phase.trim(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    if (isRunning) {
                        Text(
                            text = timeLeft.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRunning) "Stop" else "Start Exercise", 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MentalHealthTabContent(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    reflectionText: String,
    onReflectionChanged: (String) -> Unit,
    userSettings: UserSettings?,
    logs: List<MentalHealth>,
    showLogs: Boolean,
    onToggleLogs: () -> Unit,
    onSave: () -> Unit
) {
    val isDark = userSettings?.theme == 1
    val textColor = if (isDark) Color.White else Color.Black
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFAF3F3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val moods = listOf("😔", "😐", "🙂", "🤩")
                    moods.forEach { mood ->
                        Text(
                            text = mood,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedMood == mood) Color.LightGray else Color.Transparent)
                                .clickable { onMoodSelected(mood) }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BreathingSection(userSettings)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Daily Reflection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = reflectionText,
                    onValueChange = onReflectionChanged,
                    placeholder = { Text("What's on your mind?", color = textColor.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color.DarkGray else Color.White,
                        unfocusedContainerColor = if (isDark) Color.DarkGray else Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Teal else Color(0xFFB099FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Note", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onToggleLogs,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = if (isDark) Teal else Color(0xFFB099FF)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showLogs) "Hide Logs" else "Show Logs",
                            color = if (isDark) Teal else Color(0xFFB099FF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        AnimatedVisibility(visible = showLogs) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                logs.forEach { log ->
                    JournalEntryItem(log, cardBg, textColor)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun JournalEntryItem(log: MentalHealth, cardBg: Color, textColor: Color) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = log.mood, fontSize = 24.sp)
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = log.reflection,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    StudentFitnessTheme {
        Dashboard(navController = rememberNavController())
    }
}
