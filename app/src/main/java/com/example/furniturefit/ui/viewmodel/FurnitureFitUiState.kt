package com.example.furniturefit.ui.viewmodel

import com.example.furniturefit.data.calculator.DoorObstruction
import com.example.furniturefit.data.calculator.FurnitureCollision
import com.example.furniturefit.data.calculator.WallCollision
import com.example.furniturefit.data.model.AppSettings
import com.example.furniturefit.data.model.CheckMode
import com.example.furniturefit.data.model.Clearance
import com.example.furniturefit.data.model.DoorFeature
import com.example.furniturefit.data.model.DoorwayCheckResult
import com.example.furniturefit.data.model.FitResult
import com.example.furniturefit.data.model.FurnitureDimensions
import com.example.furniturefit.data.model.FurnitureFitHistory
import com.example.furniturefit.data.model.FurniturePreset
import com.example.furniturefit.data.model.MeasurementType
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.PlacedFurniture
import com.example.furniturefit.data.model.RoomDimensions
import com.example.furniturefit.data.model.RoomShape
import com.example.furniturefit.data.model.UnitType
import com.example.furniturefit.data.model.WindowFeature

data class FurnitureFitUiState(
    // Base required fields
    val unit: UnitType = UnitType.CM,
    val room: RoomDimensions = RoomDimensions(
        lengthMm = 5000.0,
        widthMm = 4000.0
    ),
    val furniture: FurnitureDimensions = FurnitureDimensions(
        lengthMm = 2200.0,
        widthMm = 900.0,
        heightMm = 850.0
    ),
    val furnitureName: String = "Three-Seater Sofa",
    val clearance: Clearance = Clearance(),
    val orientation: Orientation = Orientation.AUTO,
    val fitResult: FitResult? = null,
    val errorMessage: String? = null,

    // Text inputs for seamless user typing
    val roomLengthInput: String = "500",
    val roomWidthInput: String = "400",
    val roomHeightInput: String = "",
    val furnitureLengthInput: String = "220",
    val furnitureWidthInput: String = "90",
    val furnitureHeightInput: String = "85",
    val clearanceLeftInput: String = "30",
    val clearanceRightInput: String = "30",
    val clearanceFrontInput: String = "60",
    val clearanceBackInput: String = "30",
    val symmetricClearanceInput: String = "30",

    // Room shape & measurement type
    val roomShape: RoomShape = RoomShape.RECTANGLE,
    val measurementType: MeasurementType = MeasurementType.INSIDE_WALL,
    val wallThicknessMm: Double = 150.0,

    // Preset
    val selectedPresetId: String = "sofa",

    // Clearance modes
    val clearanceSymmetric: Boolean = false,
    val needAccessPath: Boolean = false,
    val accessPathWidthInput: String = "80",
    val reserveWalkingClearance: Boolean = false,
    val walkingWidthInput: String = "80",
    val wallClearanceEnabled: Boolean = false,
    val wallClearanceInput: String = "10",
    val checkMode: CheckMode = CheckMode.FURNITURE_AND_CLEARANCE,

    // Doorway delivery check
    val showDoorwayCheck: Boolean = false,
    val doorwayWidthInput: String = "90",
    val doorwayHeightInput: String = "210",
    val doorwayCheckResult: DoorwayCheckResult? = null,

    // Architectural features
    val doorFeature: DoorFeature = DoorFeature(),
    val windowFeature: WindowFeature = WindowFeature(),

    // Floor Plan Interactive state
    val placedFurnitureList: List<PlacedFurniture> = emptyList(),
    val activeFurnitureIndex: Int = 0,
    val floorPlanCollisions: List<FurnitureCollision> = emptyList(),
    val floorPlanWallCollisions: List<WallCollision> = emptyList(),
    val floorPlanDoorObstructions: List<DoorObstruction> = emptyList(),

    // History & Settings
    val history: List<FurnitureFitHistory> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val snackbarMessage: String? = null
)
