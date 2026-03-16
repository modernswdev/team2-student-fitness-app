@Composable
fun WellnessDashboard() {
    var activeTab by remember { mutableStateOf("workout") }

    // phone-container equivalent
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardColors.primary) // #078c8c
            .padding(24.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Good Morning,", color = Color.White)
                Text("Student", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            // Profile Pic
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(DashboardColors.secondary, CircleShape)
            )
        }

        // Tab Navigation (Tab-container)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardColors.background, RoundedCornerShape(15.dp))
                .padding(5.dp)
        ) {
            TabButton("Workout", activeTab == "workout") { activeTab = "workout" }
            TabButton("Mental Health", activeTab == "mental") { activeTab = "mental" }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Conditional Rendering based on activeTab
        if (activeTab == "workout") {
            WorkoutSection()
        } else {
            MentalHealthSection()
        }
    }
}

@Composable
fun RowScope.TabButton(label: String, isActive: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).padding(2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isActive) DashboardColors.secondary else Color.Transparent,
            contentColor = if (isActive) DashboardColors.primary else Color.Gray
        ),
        elevation = null
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}
