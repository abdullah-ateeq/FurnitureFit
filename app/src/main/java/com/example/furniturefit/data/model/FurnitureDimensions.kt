package com.example.furniturefit.data.model

data class FurnitureDimensions(
    val lengthMm: Double,
    val widthMm: Double,
    val heightMm: Double? = null
) {
    val areaMm2: Double
        get() = lengthMm * widthMm

    val areaM2: Double
        get() = areaMm2 / 1_000_000.0
}
