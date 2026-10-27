package com.example.furniturefit.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.furniturefit.data.calculator.FurnitureFitCalculator
import com.example.furniturefit.data.calculator.UnitConverter
import com.example.furniturefit.data.model.AppSettings
import com.example.furniturefit.data.model.AppThemeMode
import com.example.furniturefit.data.model.CheckMode
import com.example.furniturefit.data.model.Clearance
import com.example.furniturefit.data.model.DoorFeature
import com.example.furniturefit.data.model.DoorSwing
import com.example.furniturefit.data.model.FurnitureDimensions
import com.example.furniturefit.data.model.FurnitureFitHistory
import com.example.furniturefit.data.model.FurniturePreset
import com.example.furniturefit.data.model.MeasurementType
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.PlacedFurniture
import com.example.furniturefit.data.model.RoomDimensions
import com.example.furniturefit.data.model.RoomShape
import com.example.furniturefit.data.model.UnitType
import com.example.furniturefit.data.model.WallSide
import com.example.furniturefit.data.model.WindowFeature
import com.example.furniturefit.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class FurnitureFitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)

    private val _uiState = MutableStateFlow(FurnitureFitUiState())
    val uiState: StateFlow<FurnitureFitUiState> = _uiState.asStateFlow()

    init {
        loadSettingsAndHistory()
        recalculate()
    }

    private fun loadSettingsAndHistory() {
        val settings = repository.getSettings()
        val history = repository.getHistory()
        _uiState.update { current ->
            current.copy(
                settings = settings,
                history = history,
                unit = settings.defaultUnit,
                orientation = settings.defaultOrientation
            )
        }
    }

    // ==========================================
    // Unit Change
    // ==========================================

    fun setUnit(newUnit: UnitType) {
        val current = _uiState.value
        if (current.unit == newUnit) return

        // Convert the current inputs to the new unit
        val rLen = UnitConverter.formatMm(current.room.lengthMm, newUnit, 2, false)
        val rWid = UnitConverter.formatMm(current.room.widthMm, newUnit, 2, false)
        val rHei = current.room.heightMm?.let { UnitConverter.formatMm(it, newUnit, 2, false) } ?: ""

        val fLen = UnitConverter.formatMm(current.furniture.lengthMm, newUnit, 2, false)
        val fWid = UnitConverter.formatMm(current.furniture.widthMm, newUnit, 2, false)
        val fHei = current.furniture.heightMm?.let { UnitConverter.formatMm(it, newUnit, 2, false) } ?: ""

        val cLeft = UnitConverter.formatMm(current.clearance.leftMm, newUnit, 2, false)
        val cRight = UnitConverter.formatMm(current.clearance.rightMm, newUnit, 2, false)
        val cFront = UnitConverter.formatMm(current.clearance.frontMm, newUnit, 2, false)
        val cBack = UnitConverter.formatMm(current.clearance.backMm, newUnit, 2, false)
        val cSymm = UnitConverter.formatMm(current.clearance.leftMm, newUnit, 2, false)

        _uiState.update {
            it.copy(
                unit = newUnit,
                roomLengthInput = rLen,
                roomWidthInput = rWid,
                roomHeightInput = rHei,
                furnitureLengthInput = fLen,
                furnitureWidthInput = fWid,
                furnitureHeightInput = fHei,
                clearanceLeftInput = cLeft,
                clearanceRightInput = cRight,
                clearanceFrontInput = cFront,
                clearanceBackInput = cBack,
                symmetricClearanceInput = cSymm
            )
        }
        recalculate()
    }

    // ==========================================
    // Room Input Changes
    // ==========================================

    fun onRoomLengthChange(input: String) {
        _uiState.update { it.copy(roomLengthInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onRoomWidthChange(input: String) {
        _uiState.update { it.copy(roomWidthInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onRoomHeightChange(input: String) {
        _uiState.update { it.copy(roomHeightInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onRoomShapeChange(shape: RoomShape) {
        _uiState.update { it.copy(roomShape = shape) }
        recalculate()
    }

    fun onMeasurementTypeChange(type: MeasurementType) {
        _uiState.update { it.copy(measurementType = type) }
        recalculate()
    }

    // ==========================================
    // Furniture Input Changes & Presets
    // ==========================================

    fun onFurnitureNameChange(name: String) {
        _uiState.update { it.copy(furnitureName = name) }
    }

    fun onPresetSelected(preset: FurniturePreset) {
        val unit = _uiState.value.unit
        val fLen = UnitConverter.formatMm(preset.typicalLengthMm, unit, 2, false)
        val fWid = UnitConverter.formatMm(preset.typicalWidthMm, unit, 2, false)
        val fHei = preset.typicalHeightMm?.let { UnitConverter.formatMm(it, unit, 2, false) } ?: ""

        _uiState.update {
            it.copy(
                selectedPresetId = preset.id,
                furnitureName = preset.defaultName,
                furnitureLengthInput = fLen,
                furnitureWidthInput = fWid,
                furnitureHeightInput = fHei
            )
        }
        recalculate()
    }

    fun onFurnitureLengthChange(input: String) {
        _uiState.update { it.copy(furnitureLengthInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onFurnitureWidthChange(input: String) {
        _uiState.update { it.copy(furnitureWidthInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onFurnitureHeightChange(input: String) {
        _uiState.update { it.copy(furnitureHeightInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    // ==========================================
    // Orientation
    // ==========================================

    fun onOrientationChange(orientation: Orientation) {
        _uiState.update { it.copy(orientation = orientation) }
        recalculate()
    }

    // ==========================================
    // Clearance Changes
    // ==========================================

    fun onClearanceModeSymmetric(isSymmetric: Boolean) {
        _uiState.update { it.copy(clearanceSymmetric = isSymmetric) }
        if (isSymmetric) {
            val symmVal = _uiState.value.symmetricClearanceInput
            _uiState.update {
                it.copy(
                    clearanceLeftInput = symmVal,
                    clearanceRightInput = symmVal,
                    clearanceFrontInput = symmVal,
                    clearanceBackInput = symmVal
                )
            }
        }
        recalculate()
    }

    fun onSymmetricClearanceChange(input: String) {
        _uiState.update {
            it.copy(
                symmetricClearanceInput = input,
                clearanceLeftInput = input,
                clearanceRightInput = input,
                clearanceFrontInput = input,
                clearanceBackInput = input
            )
        }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onClearanceLeftChange(input: String) {
        _uiState.update { it.copy(clearanceLeftInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onClearanceRightChange(input: String) {
        _uiState.update { it.copy(clearanceRightInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onClearanceFrontChange(input: String) {
        _uiState.update { it.copy(clearanceFrontInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onClearanceBackChange(input: String) {
        _uiState.update { it.copy(clearanceBackInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onNeedAccessPathChange(enabled: Boolean) {
        _uiState.update { it.copy(needAccessPath = enabled) }
        recalculate()
    }

    fun onAccessPathWidthChange(input: String) {
        _uiState.update { it.copy(accessPathWidthInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onReserveWalkingClearanceChange(enabled: Boolean) {
        _uiState.update { it.copy(reserveWalkingClearance = enabled) }
        recalculate()
    }

    fun onWalkingWidthChange(input: String) {
        _uiState.update { it.copy(walkingWidthInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onWallClearanceEnabledChange(enabled: Boolean) {
        _uiState.update { it.copy(wallClearanceEnabled = enabled) }
        recalculate()
    }

    fun onWallClearanceChange(input: String) {
        _uiState.update { it.copy(wallClearanceInput = input) }
        if (_uiState.value.settings.liveCalculation) recalculate()
    }

    fun onCheckModeChange(mode: CheckMode) {
        _uiState.update { it.copy(checkMode = mode) }
        recalculate()
    }

    // ==========================================
    // Doorway Delivery Check
    // ==========================================

    fun onToggleDoorwayCheck(show: Boolean) {
        _uiState.update { it.copy(showDoorwayCheck = show) }
        if (show) calculateDoorwayCheck()
    }

    fun onDoorwayWidthChange(input: String) {
        _uiState.update { it.copy(doorwayWidthInput = input) }
        calculateDoorwayCheck()
    }

    fun onDoorwayHeightChange(input: String) {
        _uiState.update { it.copy(doorwayHeightInput = input) }
        calculateDoorwayCheck()
    }

    private fun calculateDoorwayCheck() {
        val state = _uiState.value
        val dWidth = state.doorwayWidthInput.toDoubleOrNull()
        val dHeight = state.doorwayHeightInput.toDoubleOrNull()
        val fLen = state.furniture.lengthMm
        val fWid = state.furniture.widthMm
        val fHei = state.furniture.heightMm ?: 850.0 // fallback if optional

        if (dWidth != null && dWidth > 0 && dHeight != null && dHeight > 0) {
            val dWidthMm = UnitConverter.toMillimeters(dWidth, state.unit)
            val dHeightMm = UnitConverter.toMillimeters(dHeight, state.unit)

            val result = FurnitureFitCalculator.checkDoorwayDelivery(
                doorWidthMm = dWidthMm,
                doorHeightMm = dHeightMm,
                furnitureLengthMm = fLen,
                furnitureWidthMm = fWid,
                furnitureHeightMm = fHei
            )
            _uiState.update { it.copy(doorwayCheckResult = result) }
        } else {
            _uiState.update { it.copy(doorwayCheckResult = null) }
        }
    }

    // ==========================================
    // Architectural Features (Door & Window)
    // ==========================================

    fun onDoorFeatureChange(door: DoorFeature) {
        _uiState.update { it.copy(doorFeature = door) }
        updateFloorPlanCollisions()
    }

    fun onWindowFeatureChange(window: WindowFeature) {
        _uiState.update { it.copy(windowFeature = window) }
    }

    // ==========================================
    // Recalculate Core Fit
    // ==========================================

    fun recalculate() {
        val state = _uiState.value
        val unit = state.unit

        val rLen = state.roomLengthInput.toDoubleOrNull()
        val rWid = state.roomWidthInput.toDoubleOrNull()
        val rHei = state.roomHeightInput.toDoubleOrNull()

        val fLen = state.furnitureLengthInput.toDoubleOrNull()
        val fWid = state.furnitureWidthInput.toDoubleOrNull()
        val fHei = state.furnitureHeightInput.toDoubleOrNull()

        val cLeft = state.clearanceLeftInput.toDoubleOrNull() ?: 0.0
        val cRight = state.clearanceRightInput.toDoubleOrNull() ?: 0.0
        val cFront = state.clearanceFrontInput.toDoubleOrNull() ?: 0.0
        val cBack = state.clearanceBackInput.toDoubleOrNull() ?: 0.0

        // Validation
        if (rLen == null || rLen <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid room length greater than zero.", fitResult = null) }
            return
        }
        if (rWid == null || rWid <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid room width greater than zero.", fitResult = null) }
            return
        }
        if (fLen == null || fLen <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Furniture length must be greater than zero.", fitResult = null) }
            return
        }
        if (fWid == null || fWid <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Furniture width must be greater than zero.", fitResult = null) }
            return
        }
        if (cLeft < 0 || cRight < 0 || cFront < 0 || cBack < 0) {
            _uiState.update { it.copy(errorMessage = "Clearance cannot be negative.", fitResult = null) }
            return
        }

        // Inside vs Outside wall thickness
        var usableLengthMm = UnitConverter.toMillimeters(rLen, unit)
        var usableWidthMm = UnitConverter.toMillimeters(rWid, unit)
        if (state.measurementType == MeasurementType.OUTSIDE_WALL) {
            usableLengthMm = maxOf(0.0, usableLengthMm - (2.0 * state.wallThicknessMm))
            usableWidthMm = maxOf(0.0, usableWidthMm - (2.0 * state.wallThicknessMm))
        }

        val roomDims = RoomDimensions(
            lengthMm = usableLengthMm,
            widthMm = usableWidthMm,
            heightMm = if (rHei != null && rHei > 0) UnitConverter.toMillimeters(rHei, unit) else null
        )

        val furnDims = FurnitureDimensions(
            lengthMm = UnitConverter.toMillimeters(fLen, unit),
            widthMm = UnitConverter.toMillimeters(fWid, unit),
            heightMm = if (fHei != null && fHei > 0) UnitConverter.toMillimeters(fHei, unit) else null
        )

        // Effective clearance based on checkMode and extra options
        var leftMm = UnitConverter.toMillimeters(cLeft, unit)
        var rightMm = UnitConverter.toMillimeters(cRight, unit)
        var frontMm = UnitConverter.toMillimeters(cFront, unit)
        var backMm = UnitConverter.toMillimeters(cBack, unit)

        if (state.checkMode == CheckMode.FURNITURE_ONLY) {
            leftMm = 0.0
            rightMm = 0.0
            frontMm = 0.0
            backMm = 0.0
        } else {
            // If wall clearance is enabled, add it
            if (state.wallClearanceEnabled) {
                val wallClr = (state.wallClearanceInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                val wallClrMm = UnitConverter.toMillimeters(wallClr, unit)
                leftMm += wallClrMm
                rightMm += wallClrMm
                frontMm += wallClrMm
                backMm += wallClrMm
            }
            // If access path or walking space enabled
            if (state.needAccessPath || state.checkMode == CheckMode.FURNITURE_CLEARANCE_ACCESS) {
                val accessW = (state.accessPathWidthInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                val accessMm = UnitConverter.toMillimeters(accessW, unit)
                // Add access path to front clearance
                frontMm += accessMm
            }
            if (state.reserveWalkingClearance) {
                val walkW = (state.walkingWidthInput.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                val walkMm = UnitConverter.toMillimeters(walkW, unit)
                leftMm += walkMm
                rightMm += walkMm
            }
        }

        val clearance = Clearance(
            leftMm = leftMm,
            rightMm = rightMm,
            frontMm = frontMm,
            backMm = backMm
        )

        val fitResult = FurnitureFitCalculator.calculateFit(
            room = roomDims,
            furniture = furnDims,
            clearance = clearance,
            orientation = state.orientation,
            tightFitThresholdMm = state.settings.tightFitThresholdMm
        )

        // Ensure floor plan has at least the primary item synchronized
        val currentPlaced = state.placedFurnitureList.toMutableList()
        val primaryItem = if (currentPlaced.isNotEmpty()) {
            val first = currentPlaced[0]
            first.copy(
                name = state.furnitureName,
                lengthMm = furnDims.lengthMm,
                widthMm = furnDims.widthMm,
                heightMm = furnDims.heightMm,
                clearance = clearance,
                rotationDegrees = if (fitResult.orientation == Orientation.NINETY_DEGREES) 90 else 0
            )
        } else {
            PlacedFurniture(
                id = UUID.randomUUID().toString(),
                name = state.furnitureName,
                lengthMm = furnDims.lengthMm,
                widthMm = furnDims.widthMm,
                heightMm = furnDims.heightMm,
                positionXMm = clearance.leftMm,
                positionYMm = clearance.backMm,
                rotationDegrees = if (fitResult.orientation == Orientation.NINETY_DEGREES) 90 else 0,
                clearance = clearance
            )
        }

        if (currentPlaced.isEmpty()) {
            currentPlaced.add(primaryItem)
        } else {
            currentPlaced[0] = primaryItem
        }

        _uiState.update {
            it.copy(
                room = roomDims,
                furniture = furnDims,
                clearance = clearance,
                fitResult = fitResult,
                errorMessage = null,
                placedFurnitureList = currentPlaced
            )
        }

        updateFloorPlanCollisions()
        if (state.showDoorwayCheck) calculateDoorwayCheck()
    }

    // ==========================================
    // Floor Plan Furniture Manipulation
    // ==========================================

    fun updatePlacedFurniturePosition(id: String, newXMm: Double, newYMm: Double) {
        val state = _uiState.value
        val snap = state.settings.snapToGrid
        val gridStep = state.settings.gridSpacingMm

        val snappedX = if (snap) Math.round(newXMm / gridStep) * gridStep else newXMm
        val snappedY = if (snap) Math.round(newYMm / gridStep) * gridStep else newYMm

        val updated = state.placedFurnitureList.map { item ->
            if (item.id == id) {
                item.copy(positionXMm = snappedX, positionYMm = snappedY)
            } else item
        }
        _uiState.update { it.copy(placedFurnitureList = updated) }
        updateFloorPlanCollisions()
    }

    fun rotatePlacedFurniture(id: String) {
        val updated = _uiState.value.placedFurnitureList.map { item ->
            if (item.id == id) {
                val newRot = if (item.rotationDegrees == 0) 90 else 0
                item.copy(rotationDegrees = newRot)
            } else item
        }
        _uiState.update { it.copy(placedFurnitureList = updated) }
        updateFloorPlanCollisions()
    }

    fun addAdditionalFurniture(preset: FurniturePreset) {
        val state = _uiState.value
        if (state.placedFurnitureList.size >= 10) {
            _uiState.update { it.copy(snackbarMessage = "Maximum 10 furniture items allowed.") }
            return
        }

        val colors = listOf(
            0xFF1B4965L, 0xFF2B580CL, 0xFF632B6CL, 0xFF8B4513L,
            0xFF005F73L, 0xFF9B2226L, 0xFF0A9396L, 0xFFCA6702L
        )
        val color = colors[state.placedFurnitureList.size % colors.size]

        // Position staggered inside room
        val offset = state.placedFurnitureList.size * 300.0
        val newItem = PlacedFurniture(
            id = UUID.randomUUID().toString(),
            name = preset.defaultName,
            lengthMm = preset.typicalLengthMm,
            widthMm = preset.typicalWidthMm,
            heightMm = preset.typicalHeightMm,
            positionXMm = (offset).coerceAtMost(maxOf(0.0, state.room.lengthMm - preset.typicalLengthMm)),
            positionYMm = (offset).coerceAtMost(maxOf(0.0, state.room.widthMm - preset.typicalWidthMm)),
            rotationDegrees = 0,
            clearance = Clearance(300.0, 300.0, 300.0, 300.0),
            colorArgb = color
        )

        val updated = state.placedFurnitureList + newItem
        _uiState.update {
            it.copy(
                placedFurnitureList = updated,
                activeFurnitureIndex = updated.lastIndex,
                snackbarMessage = "Added ${preset.name} to room."
            )
        }
        updateFloorPlanCollisions()
    }

    fun removePlacedFurniture(id: String) {
        val current = _uiState.value.placedFurnitureList
        if (current.size <= 1) {
            _uiState.update { it.copy(snackbarMessage = "Cannot remove primary item.") }
            return
        }
        val updated = current.filterNot { it.id == id }
        _uiState.update {
            it.copy(
                placedFurnitureList = updated,
                activeFurnitureIndex = (it.activeFurnitureIndex).coerceAtMost(updated.lastIndex)
            )
        }
        updateFloorPlanCollisions()
    }

    fun setActiveFurnitureIndex(index: Int) {
        if (index in _uiState.value.placedFurnitureList.indices) {
            _uiState.update { it.copy(activeFurnitureIndex = index) }
        }
    }

    private fun updateFloorPlanCollisions() {
        val state = _uiState.value
        val collisions = FurnitureFitCalculator.checkFurnitureCollisions(state.placedFurnitureList)
        val wallCollisions = FurnitureFitCalculator.checkWallCollisions(state.room, state.placedFurnitureList)
        val doorObstructions = FurnitureFitCalculator.checkDoorObstruction(state.room, state.doorFeature, state.placedFurnitureList)

        _uiState.update {
            it.copy(
                floorPlanCollisions = collisions,
                floorPlanWallCollisions = wallCollisions,
                floorPlanDoorObstructions = doorObstructions
            )
        }
    }

    // ==========================================
    // History Actions
    // ==========================================

    fun saveCurrentCheckToHistory() {
        val state = _uiState.value
        val result = state.fitResult ?: return

        val historyItem = FurnitureFitHistory(
            id = UUID.randomUUID().toString(),
            furnitureName = state.furnitureName,
            roomLength = state.room.lengthMm,
            roomWidth = state.room.widthMm,
            furnitureLength = state.furniture.lengthMm,
            furnitureWidth = state.furniture.widthMm,
            furnitureHeight = state.furniture.heightMm,
            unit = state.unit,
            clearance = state.clearance,
            orientation = state.orientation,
            fits = result.fits,
            createdAt = System.currentTimeMillis()
        )

        val updated = repository.saveHistoryItem(historyItem)
        _uiState.update {
            it.copy(
                history = updated,
                snackbarMessage = "Saved check to History."
            )
        }
    }

    fun restoreFromHistory(item: FurnitureFitHistory) {
        val unit = item.unit
        _uiState.update {
            it.copy(
                unit = unit,
                furnitureName = item.furnitureName,
                roomLengthInput = UnitConverter.formatMm(item.roomLength, unit, 2, false),
                roomWidthInput = UnitConverter.formatMm(item.roomWidth, unit, 2, false),
                roomHeightInput = "",
                furnitureLengthInput = UnitConverter.formatMm(item.furnitureLength, unit, 2, false),
                furnitureWidthInput = UnitConverter.formatMm(item.furnitureWidth, unit, 2, false),
                furnitureHeightInput = item.furnitureHeight?.let { h -> UnitConverter.formatMm(h, unit, 2, false) } ?: "",
                clearanceLeftInput = UnitConverter.formatMm(item.clearance.leftMm, unit, 2, false),
                clearanceRightInput = UnitConverter.formatMm(item.clearance.rightMm, unit, 2, false),
                clearanceFrontInput = UnitConverter.formatMm(item.clearance.frontMm, unit, 2, false),
                clearanceBackInput = UnitConverter.formatMm(item.clearance.backMm, unit, 2, false),
                orientation = item.orientation
            )
        }
        recalculate()
    }

    fun deleteHistoryItem(id: String) {
        val updated = repository.deleteHistoryItem(id)
        _uiState.update { it.copy(history = updated, snackbarMessage = "History item deleted.") }
    }

    fun clearAllHistory() {
        repository.clearAllHistory()
        _uiState.update { it.copy(history = emptyList(), snackbarMessage = "All history cleared.") }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // ==========================================
    // Settings Actions
    // ==========================================

    fun updateSettings(settings: AppSettings) {
        repository.saveSettings(settings)
        _uiState.update { it.copy(settings = settings) }
        recalculate()
    }

    fun resetSettingsToDefaults() {
        val defaults = repository.resetSettingsToDefaults()
        _uiState.update { it.copy(settings = defaults, snackbarMessage = "Settings reset to defaults.") }
        recalculate()
    }
}
