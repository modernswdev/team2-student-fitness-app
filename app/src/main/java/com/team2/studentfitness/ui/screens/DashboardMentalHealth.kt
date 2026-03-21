package com.team2.studentfitness.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.team2.studentfitness.ui.theme.*

@Composable
fun DashboardMentalHealth(navController: NavController) {
    // State to track which tab is selected (0 = Workout, 1 = Mental Health)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Workout", "Mental Health")

    Scaffold(
        containerColor = Teal, // Matches your --bg-color
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Good Morning,", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                        Text("Student", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    // Profile Circle
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .background(Orange, CircleShape)
                            .clickable { navController.navigate("settings") }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = if (selectedTab == 0) Orange else MentalAccent
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> WorkoutTabContent(navController)
                1 -> MentalHealthTabContent(navController)
            }
        }
    }
}

@Composable
fun DashboardHeader(selectedTabIndex: Int, tabs: List<String>, onTabSelected: (Int) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Good Morning,", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                Text("Student", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            // Profile Pic circle from your HTML
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Orange, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tab Row (Replaces your .tab-container buttons)
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = if (selectedTabIndex == 0) Orange else MentalAccent
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun WorkoutTabContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Original Workout Goal Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3B82F6))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Goal", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("80% Completed", color = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("Resume", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
                    }
                }
                CircularProgressIndicator(
                    progress = { 0.8f },
                    color = Color.White,
                    strokeWidth = 4.dp
                )
            }
        }

        Text("Your Activity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

        // Using your original grid structure
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HealthMetricCard("Heart Rate", "82 bpm", Orange, Modifier.weight(1f)) {}
            HealthMetricCard("Calories", "450 kcal", Mint, Modifier.weight(1f)) {}
        }
    }
}

@Composable
fun MentalHealthTabContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Mental Health Card using the Purple Accent from your CSS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MentalAccent)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Mindfulness", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("5 min breathing", color = Color.White.copy(alpha = 0.8f))
                    Button(
                        onClick = { navController.navigate("detail/Mental Health") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Start", color = MentalAccent, fontWeight = FontWeight.Bold)
                    }
                }
                Text("🧘", fontSize = 48.sp)
            }
        }

        // Reflection Box (Replaces your HTML textarea and JS saveEntry)
        Column {
            Text("Daily Reflection", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            var journalText by remember { mutableStateOf("") }

            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("What's on your mind?", color = Color.Gray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MentalAccent
                )
            )

            Button(
                onClick = { /* Save logic will go here */ },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MentalAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Note", fontWeight = FontWeight.Bold)
            }
        }
    }
}
