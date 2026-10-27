package com.example.furniturefit.data.model

data class RoomDimensions(
    val lengthMm: Double,
    val widthMm: Double,
    val heightMm: Double? = null
) {
    val areaMm2: Double
        get() = lengthMm * widthMm

    val perimeterMm: Double
        get() = 2.0 * (lengthMm + widthMm)

    val areaM2: Double
        get() = areaMm2 / 1_000_000.0

    val perimeterM: Double
        get() = perimeterMm / 1000.0
}
