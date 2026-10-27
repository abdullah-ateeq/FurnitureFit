package com.example.furniturefit.data.model

data class RectMm(
    val left: Double,
    val top: Double,
    val right: Double,
    val bottom: Double
) {
    val width: Double get() = right - left
    val height: Double get() = bottom - top

    fun intersects(other: RectMm): Boolean {
        return left < other.right && right > other.left &&
                top < other.bottom && bottom > other.top
    }

    fun intersection(other: RectMm): RectMm? {
        if (!intersects(other)) return null
        return RectMm(
            left = maxOf(left, other.left),
            top = maxOf(top, other.top),
            right = minOf(right, other.right),
            bottom = minOf(bottom, other.bottom)
        )
    }
}

data class PlacedFurniture(
    val id: String,
    val name: String,
    val lengthMm: Double,
    val widthMm: Double,
    val heightMm: Double? = null,
    val positionXMm: Double = 0.0,
    val positionYMm: Double = 0.0,
    val rotationDegrees: Int = 0,
    val clearance: Clearance = Clearance(),
    val colorArgb: Long = 0xFF1B4965L
) {
    val effectiveLengthMm: Double
        get() = if (rotationDegrees % 180 == 0) lengthMm else widthMm

    val effectiveWidthMm: Double
        get() = if (rotationDegrees % 180 == 0) widthMm else lengthMm

    val furnitureBounds: RectMm
        get() = RectMm(
            left = positionXMm,
            top = positionYMm,
            right = positionXMm + effectiveLengthMm,
            bottom = positionYMm + effectiveWidthMm
        )

    val clearanceBounds: RectMm
        get() {
            // Note: In 0°, left/right is along length, front/back is along width.
            // In 90°, the furniture rotated, so clearances map accordingly.
            val cLeft = if (rotationDegrees % 180 == 0) clearance.leftMm else clearance.frontMm
            val cRight = if (rotationDegrees % 180 == 0) clearance.rightMm else clearance.backMm
            val cTop = if (rotationDegrees % 180 == 0) clearance.backMm else clearance.leftMm
            val cBottom = if (rotationDegrees % 180 == 0) clearance.frontMm else clearance.rightMm

            return RectMm(
                left = positionXMm - cLeft,
                top = positionYMm - cTop,
                right = positionXMm + effectiveLengthMm + cRight,
                bottom = positionYMm + effectiveWidthMm + cBottom
            )
        }
}
