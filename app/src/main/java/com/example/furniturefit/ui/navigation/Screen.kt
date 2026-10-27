package com.example.furniturefit.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home")
    object FloorPlan : Screen("floor_plan", "Floor Plan")
    object History : Screen("history", "History")
    object Settings : Screen("settings", "Settings")
    object About : Screen("about", "About")
}
