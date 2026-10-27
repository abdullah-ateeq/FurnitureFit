package com.example.furniturefit.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furniturefit.data.calculator.UnitConverter
import com.example.furniturefit.data.model.CheckMode
import com.example.furniturefit.data.model.FurniturePreset
import com.example.furniturefit.data.model.MeasurementType
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.RoomShape
import com.example.furniturefit.ui.components.FitResultCard
import com.example.furniturefit.ui.components.MeasurementInput
import com.example.furniturefit.ui.components.SectionCard
import com.example.furniturefit.ui.components.UnitSelectorMenu
import com.example.furniturefit.ui.theme.TechnicalFail
import com.example.furniturefit.ui.theme.TechnicalFailContainer
import com.example.furniturefit.ui.theme.TechnicalPass
import com.example.furniturefit.ui.theme.TechnicalPassContainer
import com.example.furniturefit.ui.viewmodel.FurnitureFitViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: FurnitureFitViewModel,
    onNavigateToFloorPlan: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FurnitureFit",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Check whether furniture fits your room.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    UnitSelectorMenu(
                        currentUnit = state.unit,
                        onUnitSelected = { viewModel.setUnit(it) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    IconButton(onClick = onNavigateToFloorPlan) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Floor Plan",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Error Banner if invalid input
            state.errorMessage?.let { err ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TechnicalFailContainer, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = TechnicalFail,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = err,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TechnicalFail
                        )
                    }
                }
            }

            // ==========================================
            // 1. ROOM SECTION
            // ==========================================
            item {
                SectionCard(
                    title = "Room Dimensions",
                    badgeText = "${UnitConverter.formatArea(state.room.areaMm2, state.unit)} Area"
                ) {
                    // Room Shape & Wall thickness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Room Shape Selector
                        RoomShapeDropdown(
                            currentShape = state.roomShape,
                            onShapeSelected = { viewModel.onRoomShapeChange(it) },
                            modifier = Modifier.weight(1f)
                        )

                        // Measurement type: Inside vs Outside
                        MeasurementTypeDropdown(
                            currentType = state.measurementType,
                            onTypeSelected = { viewModel.onMeasurementTypeChange(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Room Dimension Inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MeasurementInput(
                            value = state.roomLengthInput,
                            onValueChange = { viewModel.onRoomLengthChange(it) },
                            label = "Length",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier.weight(1f)
                        )
                        MeasurementInput(
                            value = state.roomWidthInput,
                            onValueChange = { viewModel.onRoomWidthChange(it) },
                            label = "Width",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementInput(
                        value = state.roomHeightInput,
                        onValueChange = { viewModel.onRoomHeightChange(it) },
                        label = "Height",
                        unitSymbol = state.unit.symbol,
                        placeholder = "e.g. 280",
                        isOptional = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Area & Perimeter readout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Area: ${UnitConverter.formatArea(state.room.areaMm2, state.unit)}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Perimeter: ${UnitConverter.formatPerimeter(state.room.perimeterMm, state.unit)}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ==========================================
            // 2. FURNITURE SECTION
            // ==========================================
            item {
                SectionCard(
                    title = "Furniture",
                    badgeText = "${UnitConverter.formatArea(state.furniture.areaMm2, state.unit)} Footprint"
                ) {
                    // Preset Selector
                    FurniturePresetSelector(
                        selectedId = state.selectedPresetId,
                        onPresetSelected = { viewModel.onPresetSelected(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Furniture Name
                    OutlinedTextField(
                        value = state.furnitureName,
                        onValueChange = { viewModel.onFurnitureNameChange(it) },
                        label = { Text("Furniture Name", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Length, Width, Height
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MeasurementInput(
                            value = state.furnitureLengthInput,
                            onValueChange = { viewModel.onFurnitureLengthChange(it) },
                            label = "Length",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier.weight(1f)
                        )
                        MeasurementInput(
                            value = state.furnitureWidthInput,
                            onValueChange = { viewModel.onFurnitureWidthChange(it) },
                            label = "Width / Depth",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MeasurementInput(
                        value = state.furnitureHeightInput,
                        onValueChange = { viewModel.onFurnitureHeightChange(it) },
                        label = "Height",
                        unitSymbol = state.unit.symbol,
                        placeholder = "e.g. 85",
                        isOptional = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Orientation selection
                    Text(
                        text = "ORIENTATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.orientation == Orientation.AUTO,
                            onClick = { viewModel.onOrientationChange(Orientation.AUTO) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "Auto-check both",
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable { viewModel.onOrientationChange(Orientation.AUTO) }
                                .padding(end = 12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.orientation == Orientation.ZERO_DEGREES,
                            onClick = { viewModel.onOrientationChange(Orientation.ZERO_DEGREES) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "Length along room length (0°)",
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { viewModel.onOrientationChange(Orientation.ZERO_DEGREES) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = state.orientation == Orientation.NINETY_DEGREES,
                            onClick = { viewModel.onOrientationChange(Orientation.NINETY_DEGREES) },
                            colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = "Length along room width (90°)",
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { viewModel.onOrientationChange(Orientation.NINETY_DEGREES) }
                        )
                    }
                }
            }

            // ==========================================
            // 3. CLEARANCE REQUIREMENTS
            // ==========================================
            item {
                SectionCard(
                    title = "Clearance Requirements",
                    badgeText = "User-defined"
                ) {
                    Text(
                        text = "User-defined planning clearances (not building codes).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Symmetric toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = state.clearanceSymmetric,
                                onClick = { viewModel.onClearanceModeSymmetric(true) }
                            )
                            Text(
                                text = "Same on all sides",
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.onClearanceModeSymmetric(true) }
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !state.clearanceSymmetric,
                                onClick = { viewModel.onClearanceModeSymmetric(false) }
                            )
                            Text(
                                text = "Custom sides",
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.onClearanceModeSymmetric(false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (state.clearanceSymmetric) {
                        MeasurementInput(
                            value = state.symmetricClearanceInput,
                            onValueChange = { viewModel.onSymmetricClearanceChange(it) },
                            label = "Clearance on all sides",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // 4 independent sides
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MeasurementInput(
                                value = state.clearanceLeftInput,
                                onValueChange = { viewModel.onClearanceLeftChange(it) },
                                label = "Left",
                                unitSymbol = state.unit.symbol,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInput(
                                value = state.clearanceRightInput,
                                onValueChange = { viewModel.onClearanceRightChange(it) },
                                label = "Right",
                                unitSymbol = state.unit.symbol,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MeasurementInput(
                                value = state.clearanceFrontInput,
                                onValueChange = { viewModel.onClearanceFrontChange(it) },
                                label = "Front",
                                unitSymbol = state.unit.symbol,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInput(
                                value = state.clearanceBackInput,
                                onValueChange = { viewModel.onClearanceBackChange(it) },
                                label = "Back",
                                unitSymbol = state.unit.symbol,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Optional access path, walking space, and wall clearance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.needAccessPath,
                            onCheckedChange = { viewModel.onNeedAccessPathChange(it) }
                        )
                        Text(
                            text = "Need door / access path",
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { viewModel.onNeedAccessPathChange(!state.needAccessPath) }
                        )
                    }
                    if (state.needAccessPath) {
                        MeasurementInput(
                            value = state.accessPathWidthInput,
                            onValueChange = { viewModel.onAccessPathWidthChange(it) },
                            label = "Required access width",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.reserveWalkingClearance,
                            onCheckedChange = { viewModel.onReserveWalkingClearanceChange(it) }
                        )
                        Text(
                            text = "Reserve walking clearance around furniture",
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { viewModel.onReserveWalkingClearanceChange(!state.reserveWalkingClearance) }
                        )
                    }
                    if (state.reserveWalkingClearance) {
                        MeasurementInput(
                            value = state.walkingWidthInput,
                            onValueChange = { viewModel.onWalkingWidthChange(it) },
                            label = "Minimum walking width",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.wallClearanceEnabled,
                            onCheckedChange = { viewModel.onWallClearanceEnabledChange(it) }
                        )
                        Text(
                            text = "Extra wall clearance",
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { viewModel.onWallClearanceEnabledChange(!state.wallClearanceEnabled) }
                        )
                    }
                    if (state.wallClearanceEnabled) {
                        MeasurementInput(
                            value = state.wallClearanceInput,
                            onValueChange = { viewModel.onWallClearanceChange(it) },
                            label = "Wall clearance",
                            unitSymbol = state.unit.symbol,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Effective Footprint Preview Card
                    EffectiveFootprintSummary(
                        furnitureLength = state.furniture.lengthMm,
                        furnitureWidth = state.furniture.widthMm,
                        clearance = state.clearance,
                        unit = state.unit
                    )
                }
            }

            // ==========================================
            // 4. DOORWAY DELIVERY CHECK (OPTIONAL FEATURE)
            // ==========================================
            item {
                SectionCard(
                    title = "Doorway Delivery Check",
                    badgeText = "Can I get it through?"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = state.showDoorwayCheck,
                            onCheckedChange = { viewModel.onToggleDoorwayCheck(it) }
                        )
                        Text(
                            text = "Check whether furniture fits through doorway opening",
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { viewModel.onToggleDoorwayCheck(!state.showDoorwayCheck) }
                        )
                    }

                    if (state.showDoorwayCheck) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MeasurementInput(
                                value = state.doorwayWidthInput,
                                onValueChange = { viewModel.onDoorwayWidthChange(it) },
                                label = "Door Width",
                                unitSymbol = state.unit.symbol,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInput(
                                value = state.doorwayHeightInput,
                                onValueChange = { viewModel.onDoorwayHeightChange(it) },
                                label = "Door Height",
                                unitSymbol = state.unit.symbol,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        state.doorwayCheckResult?.let { dwResult ->
                            Spacer(modifier = Modifier.height(10.dp))
                            val dwPass = dwResult.passes
                            val dwColor = if (dwPass) TechnicalPass else TechnicalFail
                            val dwBg = if (dwPass) TechnicalPassContainer else TechnicalFailContainer

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = dwBg
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (dwPass) "✓ BASIC DOORWAY CHECK PASSES" else "⚠ BASIC DOORWAY CHECK FAILS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = dwColor
                                    )
                                    Text(
                                        text = dwResult.orientationUsed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Text(
                                        text = dwResult.note,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 5. FIT RESULT CARD
            // ==========================================
            state.fitResult?.let { res ->
                item {
                    FitResultCard(
                        result = res,
                        room = state.room,
                        furniture = state.furniture,
                        clearance = state.clearance,
                        unit = state.unit,
                        onSaveCheck = { viewModel.saveCurrentCheckToHistory() },
                        onOpenFloorPlan = onNavigateToFloorPlan
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun RoomShapeDropdown(
    currentShape: RoomShape,
    onShapeSelected: (RoomShape) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = currentShape.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Room Shape", fontSize = 11.sp) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            RoomShape.values().forEach { shape ->
                DropdownMenuItem(
                    text = { Text(shape.label) },
                    onClick = {
                        onShapeSelected(shape)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MeasurementTypeDropdown(
    currentType: MeasurementType,
    onTypeSelected: (MeasurementType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = if (currentType == MeasurementType.INSIDE_WALL) "Inside" else "Outside",
            onValueChange = {},
            readOnly = true,
            label = { Text("Wall Ref", fontSize = 11.sp) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MeasurementType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.label) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FurniturePresetSelector(
    selectedId: String,
    onPresetSelected: (FurniturePreset) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentPreset = FurniturePreset.findById(selectedId)

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentPreset.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Furniture Preset (Select to fill suggested name)", fontSize = 11.sp) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DoorFront,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            FurniturePreset.ALL.forEach { preset ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(preset.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "${preset.defaultName} (~${(preset.typicalLengthMm / 10).toInt()} × ${(preset.typicalWidthMm / 10).toInt()} cm)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onPresetSelected(preset)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EffectiveFootprintSummary(
    furnitureLength: Double,
    furnitureWidth: Double,
    clearance: com.example.furniturefit.data.model.Clearance,
    unit: com.example.furniturefit.data.model.UnitType
) {
    val reqLen = furnitureLength + clearance.leftMm + clearance.rightMm
    val reqWid = furnitureWidth + clearance.frontMm + clearance.backMm

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "EFFECTIVE FOOTPRINT CALCULATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Furniture: ${UnitConverter.formatMm(furnitureLength, unit, 0, false)} × ${UnitConverter.formatMm(furnitureWidth, unit, 0, true)}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Clearance: L${UnitConverter.formatMm(clearance.leftMm, unit, 0, false)} R${UnitConverter.formatMm(clearance.rightMm, unit, 0, false)} F${UnitConverter.formatMm(clearance.frontMm, unit, 0, false)} B${UnitConverter.formatMm(clearance.backMm, unit, 0, false)}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Required Space (0°):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${UnitConverter.formatMm(reqLen, unit, 1, false)} × ${UnitConverter.formatMm(reqWid, unit, 1, true)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
