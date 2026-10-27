package com.example.furniturefit.data.model

data class FurnitureFitHistory(
    val id: String,
    val furnitureName: String,
    val roomLength: Double,
    val roomWidth: Double,
    val furnitureLength: Double,
    val furnitureWidth: Double,
    val furnitureHeight: Double?,
    val unit: UnitType,
    val clearance: Clearance,
    val orientation: Orientation,
    val fits: Boolean,
    val createdAt: Long
)
