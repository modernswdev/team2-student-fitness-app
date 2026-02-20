package com.example.healthapitest

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodGlucoseRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeightRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.example.healthapitest.ui.theme.HealthAPITestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthAPITestTheme {
                HealthConnectApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectApp() {
    var healthData by remember { mutableStateOf<HealthConnectManager.HealthDataResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val manager = remember(context) { HealthConnectManager(context) }
    val toast = remember { Toast.makeText(context, "", Toast.LENGTH_SHORT) }

    fun showToast(msg: String) {
        toast.setText(msg)
        toast.show()
    }

    val permissions = remember {
        setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(BodyTemperatureRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(BloodGlucoseRecord::class),
            HealthPermission.getReadPermission(OxygenSaturationRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getReadPermission(HeightRecord::class)
        )
    }

    val requestPermissions = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        permissionGranted = granted.containsAll(permissions)
        Log.d("HC", "Permissions: ${granted.size}/${permissions.size} granted")
        showToast("${granted.size}/${permissions.size} permissions granted")
    }

    LaunchedEffect(Unit) {
        val providerCandidates = listOf("com.google.android.apps.healthdata", "com.android.healthconnect", "android.health.connect")
        for (pkg in providerCandidates) {
            val s = HealthConnectClient.getSdkStatus(context, pkg)
            if (s != SDK_UNAVAILABLE) break
        }

        try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            permissionGranted = granted.containsAll(permissions)
        } catch (e: Exception) {
            Log.d("HC", "Error: ${e.message}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Connect", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        if (healthData == null) {
            WelcomeScreen(paddingValues, permissionGranted, isLoading,
                onPermissionRequest = { requestPermissions.launch(permissions) },
                onFetchData = {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            healthData = manager.readAllHealthData()
                            Log.d("HC", "Health Connect data loaded successfully")
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )
        } else {
            DataScreen(paddingValues, healthData!!, onBack = { healthData = null })
        }
    }
}

@Composable
fun WelcomeScreen(paddingValues: androidx.compose.foundation.layout.PaddingValues, permissionGranted: Boolean, isLoading: Boolean, onPermissionRequest: () -> Unit, onFetchData: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(50.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Favorite, contentDescription = "Health", tint = Color.White, modifier = Modifier.size(60.dp))
        }
        Text("Health Data", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp))
        Text(if (permissionGranted) "Ready to view your data" else "Grant permissions to continue", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Button(onClick = { if (permissionGranted) onFetchData() else onPermissionRequest() }, modifier = Modifier.fillMaxWidth().padding(top = 32.dp).height(56.dp), enabled = !isLoading, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Text(if (isLoading) "Loading..." else if (permissionGranted) "Fetch Health Data" else "Grant Permissions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DataScreen(paddingValues: androidx.compose.foundation.layout.PaddingValues, healthData: HealthConnectManager.HealthDataResult, onBack: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("← Back", fontWeight = FontWeight.Bold) } }
        item { HealthCard("Steps", healthData.steps, Color(0xFF4CAF50)) }
        item { HealthCard("Heart Rate", healthData.heartRate, Color(0xFFE53935)) }
        item { HealthCard("Distance", healthData.distance, Color(0xFF2196F3)) }
        item { HealthCard("Calories", healthData.caloriesBurned, Color(0xFFFFA726)) }
        item { HealthCard("Temperature", healthData.bodyTemperature, Color(0xFFEC407A)) }
        item { HealthCard("Blood Pressure", healthData.bloodPressure, Color(0xFF5E35B1)) }
        item { HealthCard("Blood Glucose", healthData.bloodGlucose, Color(0xFFFF6F00)) }
        item { HealthCard("Oxygen", healthData.oxygenSaturation, Color(0xFF00ACC1)) }
        item { HealthCard("Sleep", healthData.sleep, Color(0xFF1A237E)) }
        item { HealthCard("Weight", healthData.weight, Color(0xFF616161)) }
        item { HealthCard("Height", healthData.height, Color(0xFF8D6E63)) }
    }
}

@Composable
fun HealthCard(title: String, data: List<String>, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(color, shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Favorite, contentDescription = title, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp), color = color)
            }
            if (data.isEmpty()) {
                Text("No data available", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 12.dp))
            } else {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    data.take(3).forEach { item -> Text("• $item", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 4.dp)) }
                    if (data.size > 3) Text("+ ${data.size - 3} more", fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

