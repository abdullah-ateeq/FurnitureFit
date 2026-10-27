package com.example.furniturefit.data.model

enum class Orientation(val label: String, val angleDegrees: Int?) {
    AUTO("Auto-check both", null),
    ZERO_DEGREES("0° (Length along room length)", 0),
    NINETY_DEGREES("90° (Length along room width)", 90);

    val shortLabel: String
        get() = when (this) {
            AUTO -> "Auto"
            ZERO_DEGREES -> "0°"
            NINETY_DEGREES -> "90°"
        }
}
