package com.example.furniturefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.furniturefit.ui.navigation.Screen
import com.example.furniturefit.ui.screens.about.AboutScreen
import com.example.furniturefit.ui.screens.floorplan.FloorPlanScreen
import com.example.furniturefit.ui.screens.history.HistoryScreen
import com.example.furniturefit.ui.screens.home.HomeScreen
import com.example.furniturefit.ui.screens.settings.SettingsScreen
import com.example.furniturefit.ui.screens.splash.SplashScreen
import com.example.furniturefit.ui.theme.FurnitureFitTheme
import com.example.furniturefit.ui.viewmodel.FurnitureFitViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FurnitureFitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val navController = rememberNavController()

            FurnitureFitTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToFloorPlan = {
                                navController.navigate(Screen.FloorPlan.route)
                            },
                            onNavigateToHistory = {
                                navController.navigate(Screen.History.route)
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Settings.route)
                            },
                            onNavigateToAbout = {
                                navController.navigate(Screen.About.route)
                            }
                        )
                    }

                    composable(Screen.FloorPlan.route) {
                        FloorPlanScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.History.route) {
                        HistoryScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onOpenFloorPlan = {
                                navController.navigate(Screen.FloorPlan.route)
                            }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.About.route) {
                        AboutScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
