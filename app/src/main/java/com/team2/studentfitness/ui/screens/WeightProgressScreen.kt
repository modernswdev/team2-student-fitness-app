package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.team2.studentfitness.DatabaseCreation
import com.team2.studentfitness.database.HealthData
import com.team2.studentfitness.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightProgressScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = (context.applicationContext as DatabaseCreation).database
    val healthDao = database.healthDao()
    val settingsDao = database.settingsDao()

    val userSettings by settingsDao.getLatestFlow().collectAsState(initial = null)
    var healthHistory by remember { mutableStateOf<List<HealthData>>(emptyList()) }
    var showAddWeight by remember { mutableStateOf(false) }

    val isDark = userSettings?.theme == 1
    val backgroundColor = if (isDark) Color(0xFF121212) else Cream
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDark) Color.White else Color.Black

    LaunchedEffect(Unit) {
        healthHistory = healthDao.getAll().sortedByDescending { it.id }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight Progress", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        },
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddWeight = true },
                containerColor = Orange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Weight")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Current Weight Highlight
            val latestWeight = healthHistory.firstOrNull()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MonitorWeight,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Teal
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Current Weight", style = MaterialTheme.typography.labelMedium, color = textColor.copy(alpha = 0.6f))
                        val weightText = latestWeight?.let { 
                            if (userSettings?.isMetric != false) "%.1f kg".format(it.weight) 
                            else "%.1f lb".format(it.weight * 2.20462f)
                        } ?: "--"
                        Text(weightText, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = textColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("History", fontWeight = FontWeight.Bold, color = textColor, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(healthHistory) { data ->
                    WeightHistoryItem(data, userSettings?.isMetric != false, cardColor, textColor)
                }
            }
        }

        if (showAddWeight) {
            AddWeightDialog(
                isDark = isDark,
                isMetric = userSettings?.isMetric != false,
                onDismiss = { showAddWeight = false },
                onSave = { newWeightKg ->
                    scope.launch {
                        val latest = healthDao.getLatest() ?: HealthData(heartRate = 70, bodyTemp = 37, totalSteps = 0, stepCount = 0)
                        healthDao.insert(latest.copy(id = 0, weight = newWeightKg, timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())))
                        healthHistory = healthDao.getAll().sortedByDescending { it.id }
                        showAddWeight = false
                    }
                }
            )
        }
    }
}

@Composable
fun WeightHistoryItem(data: HealthData, isMetric: Boolean, cardColor: Color, textColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                val weight = if (isMetric) "%.1f kg".format(data.weight) else "%.1f lb".format(data.weight * 2.20462f)
                Text(weight, fontWeight = FontWeight.Bold, color = textColor)
                Text(data.timestamp ?: "Unknown Date", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.5f))
            }
            Text("Logged", color = Teal, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AddWeightDialog(isDark: Boolean, isMetric: Boolean, onDismiss: () -> Unit, onSave: (Float) -> Unit) {
    var weightInput by remember { mutableStateOf("") }
    val unit = if (isMetric) "kg" else "lb"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Weight", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter your current weight in $unit")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight ($unit)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val weight = weightInput.toFloatOrNull()
                    if (weight != null) {
                        val weightKg = if (isMetric) weight else weight / 2.20462f
                        onSave(weightKg)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Save", color = Color.White)
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
