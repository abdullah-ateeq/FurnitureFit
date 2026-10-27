package com.example.furniturefit.data.model

enum class RoomShape(val label: String) {
    RECTANGLE("Rectangle"),
    SQUARE("Square"),
    L_SHAPED("L-Shaped")
}

enum class MeasurementType(val label: String) {
    INSIDE_WALL("Inside wall-to-wall"),
    OUTSIDE_WALL("Outside wall-to-wall")
}
