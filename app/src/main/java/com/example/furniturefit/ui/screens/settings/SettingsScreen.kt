package com.example.furniturefit.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furniturefit.data.calculator.UnitConverter
import com.example.furniturefit.data.model.AppThemeMode
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.UnitType
import com.example.furniturefit.ui.components.MeasurementInput
import com.example.furniturefit.ui.components.SectionCard
import com.example.furniturefit.ui.components.UnitSelectorMenu
import com.example.furniturefit.ui.theme.TechnicalFail
import com.example.furniturefit.ui.viewmodel.FurnitureFitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: FurnitureFitViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val settings = state.settings

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Appearance
            SectionCard(title = "Appearance") {
                Text(text = "Theme", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = settings.themeMode == mode,
                                onClick = { viewModel.updateSettings(settings.copy(themeMode = mode)) }
                            )
                            Text(
                                text = mode.label,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    viewModel.updateSettings(settings.copy(themeMode = mode))
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Dynamic Color", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Material You palette on Android 12+", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.dynamicColor,
                        onCheckedChange = { viewModel.updateSettings(settings.copy(dynamicColor = it)) }
                    )
                }
            }

            // 2. Calculator Settings
            SectionCard(title = "Calculator Settings") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Default Unit", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Used when starting a new calculation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    UnitSelectorMenu(
                        currentUnit = settings.defaultUnit,
                        onUnitSelected = { viewModel.updateSettings(settings.copy(defaultUnit = it)) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)

                Text(text = "Default Orientation", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Orientation.values().forEach { orient ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = settings.defaultOrientation == orient,
                                onClick = { viewModel.updateSettings(settings.copy(defaultOrientation = orient)) }
                            )
                            Text(
                                text = orient.shortLabel,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable {
                                    viewModel.updateSettings(settings.copy(defaultOrientation = orient))
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MeasurementInput(
                        value = UnitConverter.formatValue(settings.defaultClearanceMm / 10.0, 0),
                        onValueChange = {
                            val v = it.toDoubleOrNull() ?: 30.0
                            viewModel.updateSettings(settings.copy(defaultClearanceMm = v * 10.0))
                        },
                        label = "Default Clearance",
                        unitSymbol = "cm",
                        modifier = Modifier.weight(1f)
                    )
                    MeasurementInput(
                        value = UnitConverter.formatValue(settings.defaultFrontClearanceMm / 10.0, 0),
                        onValueChange = {
                            val v = it.toDoubleOrNull() ?: 60.0
                            viewModel.updateSettings(settings.copy(defaultFrontClearanceMm = v * 10.0))
                        },
                        label = "Front Clearance",
                        unitSymbol = "cm",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                MeasurementInput(
                    value = UnitConverter.formatValue(settings.tightFitThresholdMm / 10.0, 0),
                    onValueChange = {
                        val v = it.toDoubleOrNull() ?: 10.0
                        viewModel.updateSettings(settings.copy(tightFitThresholdMm = v * 10.0))
                    },
                    label = "Tight Fit Warning Threshold",
                    unitSymbol = "cm",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Floor Plan Settings
            SectionCard(title = "Floor Plan Settings") {
                SettingSwitchRow(
                    title = "Show Grid",
                    subtitle = "Draw background architectural grid lines",
                    checked = settings.showGrid,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showGrid = it)) }
                )

                if (settings.showGrid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Grid Spacing", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(100.0 to "10 cm", 250.0 to "25 cm", 500.0 to "50 cm", 1000.0 to "100 cm").forEach { (spacingMm, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = settings.gridSpacingMm == spacingMm,
                                    onClick = { viewModel.updateSettings(settings.copy(gridSpacingMm = spacingMm)) }
                                )
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    modifier = Modifier.clickable {
                                        viewModel.updateSettings(settings.copy(gridSpacingMm = spacingMm))
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                SettingSwitchRow(
                    title = "Snap to Grid",
                    subtitle = "Align furniture to nearest grid mark when dragging",
                    checked = settings.snapToGrid,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(snapToGrid = it)) }
                )

                SettingSwitchRow(
                    title = "Show Dimensions",
                    subtitle = "Show wall and room dimension arrow lines",
                    checked = settings.showDimensions,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showDimensions = it)) }
                )

                SettingSwitchRow(
                    title = "Show Clearance Zones",
                    subtitle = "Render translucent clearance zones on canvas",
                    checked = settings.showClearance,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showClearance = it)) }
                )

                SettingSwitchRow(
                    title = "Show Furniture Labels",
                    subtitle = "Display item names on the floor plan items",
                    checked = settings.showFurnitureLabels,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(showFurnitureLabels = it)) }
                )
            }

            // 4. Behavior
            SectionCard(title = "Behavior") {
                SettingSwitchRow(
                    title = "Live Calculation",
                    subtitle = "Update fit results instantly as numbers are typed",
                    checked = settings.liveCalculation,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(liveCalculation = it)) }
                )

                SettingSwitchRow(
                    title = "Haptics",
                    subtitle = "Subtle feedback on collisions and actions",
                    checked = settings.haptics,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(haptics = it)) }
                )

                SettingSwitchRow(
                    title = "Animations",
                    subtitle = "Smooth transitions and canvas animations",
                    checked = settings.animations,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(animations = it)) }
                )

                SettingSwitchRow(
                    title = "Confirm Delete",
                    subtitle = "Prompt before clearing history",
                    checked = settings.confirmDelete,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(confirmDelete = it)) }
                )

                SettingSwitchRow(
                    title = "Restore Last Calculation",
                    subtitle = "Restore state on subsequent app launches",
                    checked = settings.restoreLastCalculation,
                    onCheckedChange = { viewModel.updateSettings(settings.copy(restoreLastCalculation = it)) }
                )
            }

            // Reset Button
            OutlinedButton(
                onClick = { viewModel.resetSettingsToDefaults() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text(text = "Reset Settings to Defaults", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Note: Resetting settings will not delete your saved calculation history.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
