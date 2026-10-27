package com.example.furniturefit.data.calculator

import com.example.furniturefit.data.model.UnitType
import java.util.Locale

object UnitConverter {

    /**
     * Converts a given value in [fromUnit] to millimeters.
     */
    fun toMillimeters(value: Double, fromUnit: UnitType): Double {
        return when (fromUnit) {
            UnitType.MM -> value
            UnitType.CM -> value * 10.0
            UnitType.M -> value * 1000.0
            UnitType.INCH -> value * 25.4
            UnitType.FEET -> value * 304.8
        }
    }

    /**
     * Converts a value in millimeters to [toUnit].
     */
    fun fromMillimeters(valueMm: Double, toUnit: UnitType): Double {
        return when (toUnit) {
            UnitType.MM -> valueMm
            UnitType.CM -> valueMm / 10.0
            UnitType.M -> valueMm / 1000.0
            UnitType.INCH -> valueMm / 25.4
            UnitType.FEET -> valueMm / 304.8
        }
    }

    /**
     * Converts between any two units via millimeters.
     */
    fun convert(value: Double, from: UnitType, to: UnitType): Double {
        if (from == to) return value
        val mm = toMillimeters(value, from)
        return fromMillimeters(mm, to)
    }

    /**
     * Formats a millimeter value for display in the target unit.
     */
    fun formatMm(valueMm: Double, targetUnit: UnitType, decimals: Int = 2, showUnit: Boolean = true): String {
        val converted = fromMillimeters(valueMm, targetUnit)
        val formattedNumber = formatValue(converted, decimals)
        return if (showUnit) "$formattedNumber ${targetUnit.symbol}" else formattedNumber
    }

    /**
     * Formats a direct unit value.
     */
    fun formatValue(value: Double, decimals: Int = 2): String {
        // If it's essentially an integer, show no decimals or clean decimals
        return if (Math.abs(value - Math.round(value)) < 0.0001) {
            String.format(Locale.US, "%.0f", value)
        } else {
            String.format(Locale.US, "%.${decimals}f", value).trimEnd('0').trimEnd('.')
        }
    }

    /**
     * Formats an area in mm² to m² (standard display) or ft² if imperial.
     */
    fun formatArea(areaMm2: Double, targetUnit: UnitType = UnitType.CM): String {
        val areaM2 = areaMm2 / 1_000_000.0
        return if (targetUnit == UnitType.FEET || targetUnit == UnitType.INCH) {
            val areaFt2 = areaM2 * 10.7639104
            String.format(Locale.US, "%.2f ft²", areaFt2)
        } else {
            String.format(Locale.US, "%.2f m²", areaM2)
        }
    }

    /**
     * Formats perimeter in mm to meters or feet.
     */
    fun formatPerimeter(perimeterMm: Double, targetUnit: UnitType = UnitType.CM): String {
        val perimeterM = perimeterMm / 1000.0
        return if (targetUnit == UnitType.FEET || targetUnit == UnitType.INCH) {
            val perimeterFt = perimeterM * 3.28084
            String.format(Locale.US, "%.2f ft", perimeterFt)
        } else {
            String.format(Locale.US, "%.2f m", perimeterM)
        }
    }
}
