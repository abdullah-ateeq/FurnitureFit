package com.example.furniturefit.data.model

data class FurniturePreset(
    val id: String,
    val name: String,
    val defaultName: String,
    val typicalLengthMm: Double,
    val typicalWidthMm: Double,
    val typicalHeightMm: Double?
) {
    companion object {
        val ALL: List<FurniturePreset> = listOf(
            FurniturePreset("sofa", "Sofa", "Three-Seater Sofa", 2200.0, 900.0, 850.0),
            FurniturePreset("sectional_sofa", "Sectional Sofa", "L-Sectional Sofa", 2800.0, 1800.0, 850.0),
            FurniturePreset("bed", "Bed", "Queen Bed", 2000.0, 1600.0, 1000.0),
            FurniturePreset("wardrobe", "Wardrobe", "3-Door Wardrobe", 1800.0, 600.0, 2100.0),
            FurniturePreset("dining_table", "Dining Table", "6-Seater Dining Table", 1600.0, 900.0, 750.0),
            FurniturePreset("dining_chair", "Dining Chair", "Dining Chair", 500.0, 500.0, 900.0),
            FurniturePreset("center_table", "Center Table", "Coffee Table", 1200.0, 600.0, 450.0),
            FurniturePreset("side_table", "Side Table", "Nightstand / Side Table", 500.0, 500.0, 550.0),
            FurniturePreset("desk", "Desk", "Work Desk", 1400.0, 700.0, 750.0),
            FurniturePreset("office_table", "Office Table", "Executive Desk", 1800.0, 800.0, 750.0),
            FurniturePreset("cabinet", "Cabinet", "Storage Cabinet", 1000.0, 450.0, 1200.0),
            FurniturePreset("tv_unit", "TV Unit", "Media Console", 1800.0, 450.0, 500.0),
            FurniturePreset("dresser", "Dresser", "Bedroom Dresser", 1200.0, 500.0, 850.0),
            FurniturePreset("bookshelf", "Bookshelf", "Tall Bookshelf", 900.0, 350.0, 1800.0),
            FurniturePreset("refrigerator", "Refrigerator", "Double-Door Refrigerator", 800.0, 700.0, 1750.0),
            FurniturePreset("washing_machine", "Washing Machine", "Front-Load Washer", 600.0, 600.0, 850.0),
            FurniturePreset("easy_chair", "Easy Chair", "Lounge Recliner", 950.0, 900.0, 950.0),
            FurniturePreset("armchair", "Armchair", "Accent Armchair", 850.0, 850.0, 850.0),
            FurniturePreset("shoe_rack", "Shoe Rack", "Entryway Shoe Cabinet", 1000.0, 350.0, 1000.0),
            FurniturePreset("custom", "Custom", "Custom Item", 1500.0, 800.0, 750.0)
        )

        fun findById(id: String): FurniturePreset {
            return ALL.find { it.id == id } ?: ALL.last()
        }
    }
}
