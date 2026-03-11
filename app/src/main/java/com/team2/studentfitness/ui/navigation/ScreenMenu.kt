package com.team2.studentfitness.ui.navigation

data class ScreenMenuItem(
    val title: String,
    val route: String
)

object AppRoutes {
    const val Login = "login"
    const val Dashboard = "dashboard"
    const val Settings = "settings"
    const val DeveloperMenu = "dev-menu"
    const val DetailTemplate = "detail/{feature}"

    fun detail(feature: String): String = "detail/$feature"
}

// Source of truth for the developer screen picker.
val ScreenMenu = listOf(
    ScreenMenuItem(title = "Login", route = AppRoutes.Login),
    ScreenMenuItem(title = "Dashboard", route = AppRoutes.Dashboard),
    ScreenMenuItem(title = "Settings", route = AppRoutes.Settings),
    ScreenMenuItem(title = "Detail: Heart Rate", route = AppRoutes.detail("Heart Rate")),
    ScreenMenuItem(title = "Detail: Calories", route = AppRoutes.detail("Calories")),
    ScreenMenuItem(title = "Detail: Gym Hours", route = AppRoutes.detail("Gym Hours")),
    ScreenMenuItem(title = "Detail: Workout Timer", route = AppRoutes.detail("Workout Timer")),
    ScreenMenuItem(title = "Detail: Protein Intake", route = AppRoutes.detail("Protein Intake")),
    ScreenMenuItem(title = "Detail: Workout Tutorials", route = AppRoutes.detail("Workout Tutorials")),
    ScreenMenuItem(title = "Detail: Mental Health", route = AppRoutes.detail("Mental Health"))
)

