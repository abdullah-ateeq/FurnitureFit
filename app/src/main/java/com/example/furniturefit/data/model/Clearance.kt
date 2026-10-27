package com.example.furniturefit.data.model

data class Clearance(
    val leftMm: Double = 300.0,
    val rightMm: Double = 300.0,
    val frontMm: Double = 600.0,
    val backMm: Double = 300.0
) {
    val totalLengthClearanceMm: Double
        get() = leftMm + rightMm

    val totalWidthClearanceMm: Double
        get() = frontMm + backMm

    fun isSymmetric(): Boolean {
        return leftMm == rightMm && rightMm == frontMm && frontMm == backMm
    }

    companion object {
        fun symmetric(clearanceMm: Double): Clearance {
            return Clearance(
                leftMm = clearanceMm,
                rightMm = clearanceMm,
                frontMm = clearanceMm,
                backMm = clearanceMm
            )
        }
    }
}
