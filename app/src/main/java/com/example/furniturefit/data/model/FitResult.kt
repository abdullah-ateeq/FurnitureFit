package com.example.furniturefit.data.model

data class OrientationFitDetail(
    val orientation: Orientation,
    val fits: Boolean,
    val fitsPhysicallyWithoutClearance: Boolean,
    val requiredLengthMm: Double,
    val requiredWidthMm: Double,
    val remainingLengthMm: Double,
    val remainingWidthMm: Double,
    val shortageLengthMm: Double,
    val shortageWidthMm: Double,
    val fitMarginMm: Double
)

data class HeightCheckResult(
    val roomHeightMm: Double,
    val furnitureHeightMm: Double,
    val fits: Boolean,
    val remainingHeightMm: Double,
    val shortageHeightMm: Double
)

data class DoorwayCheckResult(
    val doorWidthMm: Double,
    val doorHeightMm: Double,
    val furnitureWidthMm: Double,
    val furnitureHeightMm: Double,
    val furnitureLengthMm: Double,
    val passes: Boolean,
    val orientationUsed: String,
    val note: String
)

data class FitResult(
    val fits: Boolean,
    val orientation: Orientation?,
    val requiredLengthMm: Double,
    val requiredWidthMm: Double,
    val remainingLengthMm: Double,
    val remainingWidthMm: Double,
    val shortageLengthMm: Double,
    val shortageWidthMm: Double,
    val furnitureAreaMm2: Double,
    val requiredAreaMm2: Double,
    val message: String,
    val fitsBothWays: Boolean = false,
    val fitsZeroDegrees: Boolean = false,
    val fitsNinetyDegrees: Boolean = false,
    val zeroDegreesDetail: OrientationFitDetail? = null,
    val ninetyDegreesDetail: OrientationFitDetail? = null,
    val furnitureFitsPhysicallyWithoutClearance: Boolean = true,
    val fitMarginMm: Double = 0.0,
    val isTightFit: Boolean = false,
    val heightCheck: HeightCheckResult? = null,
    val doorwayCheck: DoorwayCheckResult? = null
)
