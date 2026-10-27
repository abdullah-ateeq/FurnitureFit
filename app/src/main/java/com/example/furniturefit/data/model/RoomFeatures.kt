package com.example.furniturefit.data.model

enum class WallSide(val label: String) {
    LEFT("Left wall"),
    RIGHT("Right wall"),
    TOP("Top wall"),
    BOTTOM("Bottom wall")
}

enum class DoorSwing(val label: String) {
    INWARD("Inward"),
    OUTWARD("Outward"),
    SLIDING("Sliding")
}

data class DoorFeature(
    val enabled: Boolean = false,
    val widthMm: Double = 900.0,
    val wall: WallSide = WallSide.LEFT,
    val distanceFromCornerMm: Double = 500.0,
    val swing: DoorSwing = DoorSwing.INWARD,
    val checkObstruction: Boolean = false
)

data class WindowFeature(
    val enabled: Boolean = false,
    val widthMm: Double = 1500.0,
    val wall: WallSide = WallSide.TOP,
    val distanceFromCornerMm: Double = 1000.0,
    val keepClear: Boolean = false
)

enum class CheckMode(val label: String) {
    FURNITURE_ONLY("Furniture Only"),
    FURNITURE_AND_CLEARANCE("Furniture + Clearance"),
    FURNITURE_CLEARANCE_ACCESS("Furniture + Clearance + Access")
}
