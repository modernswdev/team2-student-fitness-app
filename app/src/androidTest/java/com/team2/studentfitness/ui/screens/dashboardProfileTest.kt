import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*

// 1. Theme Colors (Matching your CSS :root)
val AppColors = object {
    val bgPrimary = Color(0xFF078C8C)
    val cardBg = Color(0xFFF7EEEE)
    val accent = Color(0xFFF2A444)
    val mentalAccent = Color(0xFFA78BFA)
    val textMain = Color(0xFF000000)
}

data class Reflection(val date: String, val content: String)

@Composable
fun ScholarStrongApp() {
    val navController = rememberNavController()
    // Mocking the shared state (journalEntries) for the team
    val journalEntries = remember { mutableStateListOf<Reflection>() }

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(journalEntries) { navController.navigate("profile") }
        }
        composable("profile") {
            ProfileScreen(journalEntries) { navController.popBackStack() }
        }
    }
}

// --- DASHBOARD SCREEN ---
@Composable
fun DashboardScreen(entries: MutableList<Reflection>, onProfileClick: () -> Unit) {
    var activeTab by remember { mutableStateOf("workout") }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.bgPrimary).padding(24.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 25.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Good Morning,", color = Color.White)
                Text("Student", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Box(modifier = Modifier.size(45.dp).background(AppColors.accent, CircleShape).clickable { onProfileClick() })
        }

        // Tab Nav
        Row(modifier = Modifier.fillMaxWidth().background(AppColors.cardBg, RoundedCornerShape(15.dp)).padding(5.dp)) {
            val tabs = listOf("workout" to "Workout", "mental" to "Mental Health")
            tabs.forEach { (id, label) ->
                Button(
                    onClick = { activeTab = id },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (activeTab == id) AppColors.accent else Color.Transparent,
                        contentColor = if (activeTab == id) AppColors.bgPrimary else Color.Gray
                    ),
                    elevation = null
                ) { Text(label, fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (activeTab == "workout") {
            WorkoutContent()
        } else {
            MentalHealthContent { content -> 
                entries.add(0, Reflection("3/15/2026", content)) 
            }
        }
    }
}

@Composable
fun WorkoutContent() {
    Column {
        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF6F90D8)))).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Goal", color = Color.White, fontSize = 18.sp)
                    Text("80% Completed", color = Color.White)
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(backgroundColor = Color.White), modifier = Modifier.padding(top = 10.dp)) {
                        Text("Resume")
                    }
                }
                CircularProgressIndicator(progress = 0.8f, color = Color.White)
            }
        }
        Text("Video Tutorials", color = Color.White, modifier = Modifier.padding(vertical = 15.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(listOf("Squat Form", "HIIT Cardio")) { VideoCard(it) }
        }
    }
}

@Composable
fun VideoCard(title: String) {
    Column {
        Box(modifier = Modifier.height(80.dp).fillMaxWidth().background(Color(0xFF334155), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
            Text("▶", color = AppColors.accent, fontSize = 24.sp)
        }
        Text(title, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun MentalHealthContent(onSave: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column {
        Card(shape = RoundedCornerShape(20.dp), backgroundColor = AppColors.cardBg, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("How are you feeling?")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("😔", "😐", "🙂", "🤩").forEach { Text(it, fontSize = 28.sp) }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.background(AppColors.cardBg, RoundedCornerShape(20.dp)).padding(15.dp)) {
            Text("Daily Reflection", fontWeight = FontWeight.Bold)
            TextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), placeholder = { Text("What's on your mind?") })
            Button(onClick = { onSave(text); text = "" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.mentalAccent)) {
                Text("Save Note", color = Color.White)
            }
        }
    }
}

// --- PROFILE SCREEN ---
@Composable
fun ProfileScreen(entries: MutableList<Reflection>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(AppColors.bgPrimary).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "", tint = Color.White) }
            Text("Profile", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(80.dp).background(AppColors.accent, CircleShape))
            Text("Scholar Student", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(top = 10.dp))
        }

        Text("My Reflections", color = Color.White, fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.weight(1f).padding(top = 15.dp)) {
            items(entries) { entry ->
                Card(modifier = Modifier.padding(bottom = 10.dp), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.intrinsicSize(IntrinsicSize.Min)) {
                        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(AppColors.accent))
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(entry.date, color = AppColors.bgPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(entry.content)
                        }
                    }
                }
            }
        }
        
        OutlinedButton(onClick = { entries.clear() }, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color.White), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
            Text("Clear All Entries")
        }
    }
}
