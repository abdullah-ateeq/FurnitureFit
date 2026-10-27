package com.example.furniturefit.data.model

enum class UnitType(val symbol: String, val label: String) {
    MM("mm", "Millimeters"),
    CM("cm", "Centimeters"),
    M("m", "Meters"),
    INCH("in", "Inches"),
    FEET("ft", "Feet");

    override fun toString(): String = symbol
}
