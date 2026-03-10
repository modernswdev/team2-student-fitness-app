package com.team2.studentfitness.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.team2.studentfitness.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Hey, Michelle",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "Pro Member",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            var searchQuery by remember { mutableStateOf("") }
            
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search metrics...", color = Color.Gray) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Teal) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Teal,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Text(
                text = "Smart Health Metrics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Teal,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            // METRIC CARDS
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HealthMetricCard(title = "Heart Rate", value = "70 bpm", color = Mint, modifier = Modifier.weight(1f)) { 
                        navController.navigate("detail/Heart Rate") 
                    }
                    HealthMetricCard(title = "Calories Burned", value = "430 kcal", color = Orange, modifier = Modifier.weight(1f)) { 
                        navController.navigate("detail/Calories") 
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HealthMetricCard(title = "Gym Hours", value = "8am-10pm", color = Teal, modifier = Modifier.weight(1f)) { 
                        navController.navigate("detail/Gym Hours") 
                    }
                    HealthMetricCard(title = "Workout Timer", value = "30 min", color = Orange, modifier = Modifier.weight(1f)) { 
                        navController.navigate("detail/Workout Timer") 
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    HealthMetricCard(title = "Protein Intake", value = "80g / 120g", color = Orange, modifier = Modifier.weight(1f)) { 
                        navController.navigate("detail/Protein Intake") 
                    }
                    HealthMetricCard(title = "Workout Tutorials", value = "Learn", color = Mint, modifier = Modifier.weight(1f)) { 
                        navController.navigate("detail/Workout Tutorials") 
                    }
                }
                HealthMetricCard(title = "Mental Health", value = "Breathing", color = Teal, modifier = Modifier.fillMaxWidth()) { 
                    navController.navigate("detail/Mental Health") 
                }
            }
        }
    }
}

@Composable
fun HealthMetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleSmall, 
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value, 
                style = MaterialTheme.typography.titleLarge, 
                color = color, 
                fontWeight = FontWeight.Bold
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
