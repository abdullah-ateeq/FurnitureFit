package com.example.furniturefit.data.model

enum class AppThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    // Appearance
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,

    // Calculator Settings
    val defaultUnit: UnitType = UnitType.CM,
    val defaultClearanceMm: Double = 300.0,
    val defaultFrontClearanceMm: Double = 600.0,
    val tightFitThresholdMm: Double = 100.0,
    val defaultOrientation: Orientation = Orientation.AUTO,

    // Floor Plan Settings
    val showGrid: Boolean = true,
    val gridSpacingMm: Double = 250.0,
    val showDimensions: Boolean = true,
    val showClearance: Boolean = true,
    val showFurnitureLabels: Boolean = true,
    val snapToGrid: Boolean = true,

    // Behavior
    val liveCalculation: Boolean = true,
    val haptics: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastCalculation: Boolean = true
)
