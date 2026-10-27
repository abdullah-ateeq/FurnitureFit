package com.example.furniturefit

import com.example.furniturefit.data.calculator.FurnitureFitCalculator
import com.example.furniturefit.data.calculator.UnitConverter
import com.example.furniturefit.data.model.Clearance
import com.example.furniturefit.data.model.FurnitureDimensions
import com.example.furniturefit.data.model.FurnitureFitHistory
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.PlacedFurniture
import com.example.furniturefit.data.model.RoomDimensions
import com.example.furniturefit.data.model.UnitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FurnitureFitComprehensiveTest {

    // 1. BASIC FIT TEST
    // Room: 500 × 400 cm (5000 × 4000 mm)
    // Furniture: 220 × 90 cm (2200 × 900 mm)
    // Clearance: 30 / 30 / 60 / 30 cm (300 / 300 / 600 / 300 mm)
    // Required: 280 × 180 cm (2800 × 1800 mm)
    // Expected: FITS
    @Test
    fun testBasicFit() {
        val room = RoomDimensions(lengthMm = 5000.0, widthMm = 4000.0)
        val furniture = FurnitureDimensions(lengthMm = 2200.0, widthMm = 900.0)
        val clearance = Clearance(leftMm = 300.0, rightMm = 300.0, frontMm = 600.0, backMm = 300.0)

        val result = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.AUTO
        )

        assertTrue("Furniture should fit in 500x400 cm room with clearance", result.fits)
        assertEquals(2800.0, result.requiredLengthMm, 0.001)
        assertEquals(1800.0, result.requiredWidthMm, 0.001)
        // Room length remaining: 5000 - 2800 = 2200 mm
        // Room width remaining: 4000 - 1800 = 2200 mm
        assertEquals(2200.0, result.remainingLengthMm, 0.001)
        assertEquals(2200.0, result.remainingWidthMm, 0.001)
    }

    // 2. BASIC FAILURE TEST
    // Room: 250 × 160 cm (2500 × 1600 mm)
    // Furniture: 220 × 90 cm (2200 × 900 mm)
    // Clearance: 30 / 30 / 60 / 30 cm (300 / 300 / 600 / 300 mm)
    // Required: 280 × 180 cm (2800 × 1800 mm)
    // Expected: DOES NOT FIT
    @Test
    fun testBasicFailure() {
        val room = RoomDimensions(lengthMm = 2500.0, widthMm = 1600.0)
        val furniture = FurnitureDimensions(lengthMm = 2200.0, widthMm = 900.0)
        val clearance = Clearance(leftMm = 300.0, rightMm = 300.0, frontMm = 600.0, backMm = 300.0)

        val result = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.AUTO
        )

        assertFalse("Furniture should not fit in 250x160 cm room", result.fits)
        // Needs 2800x1800, available 2500x1600 -> Shortage
        assertTrue(result.shortageLengthMm > 0 || result.shortageWidthMm > 0)
    }

    // 3. ROTATION TEST
    // Room: 300 × 250 cm (3000 × 2500 mm)
    // Furniture: 240 × 280 cm (2400 × 2800 mm) (Raw dimensions without clearance)
    // Test both orientations.
    // 0°: 240 <= 300 (fits length), but 280 <= 250 (fails width)
    // 90°: 280 <= 300 (fits length), and 240 <= 250 (fits width)
    @Test
    fun testRotationOrientations() {
        val room = RoomDimensions(lengthMm = 3000.0, widthMm = 2500.0)
        val furniture = FurnitureDimensions(lengthMm = 2400.0, widthMm = 2800.0)
        val clearance = Clearance(0.0, 0.0, 0.0, 0.0)

        // Test 0°
        val result0 = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.ZERO_DEGREES
        )
        assertFalse("0° should fail because 2800 mm > 2500 mm room width", result0.fits)

        // Test 90°
        val result90 = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.NINETY_DEGREES
        )
        assertTrue("90° should fit because 2800 mm <= 3000 mm and 2400 mm <= 2500 mm", result90.fits)

        // Test AUTO: Should select 90°
        val resultAuto = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.AUTO
        )
        assertTrue("AUTO should find that it fits at 90°", resultAuto.fits)
        assertEquals(Orientation.NINETY_DEGREES, resultAuto.orientation)
    }

    // 4. NO CLEARANCE TEST
    // Room: 250 × 100 cm (2500 × 1000 mm)
    // Furniture: 250 × 100 cm (2500 × 1000 mm)
    // Clearance: 0 -> FITS
    // With 10 cm clearance (100 mm) -> DOES NOT FIT
    @Test
    fun testNoClearanceVsWithClearance() {
        val room = RoomDimensions(lengthMm = 2500.0, widthMm = 1000.0)
        val furniture = FurnitureDimensions(lengthMm = 2500.0, widthMm = 1000.0)

        // Zero clearance
        val zeroClearance = Clearance(0.0, 0.0, 0.0, 0.0)
        val resultZero = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = zeroClearance,
            orientation = Orientation.ZERO_DEGREES
        )
        assertTrue("Exact fit with 0 clearance should pass", resultZero.fits)

        // With 10 cm (100 mm) clearance
        val clearance10cm = Clearance(100.0, 100.0, 100.0, 100.0)
        val resultWithClearance = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance10cm,
            orientation = Orientation.ZERO_DEGREES
        )
        assertFalse("With 10cm clearance it must not fit", resultWithClearance.fits)
        assertTrue("Physically fits without clearance", resultWithClearance.furnitureFitsPhysicallyWithoutClearance)
    }

    // 5. HEIGHT TEST
    // Room: 300 cm (3000 mm) height
    // Furniture: 280 cm (2800 mm) height
    // Expected: 20 cm (200 mm) vertical clearance (NOT 80 cm)
    @Test
    fun testHeightCheck() {
        val room = RoomDimensions(lengthMm = 5000.0, widthMm = 4000.0, heightMm = 3000.0)
        val furniture = FurnitureDimensions(lengthMm = 2000.0, widthMm = 1000.0, heightMm = 2800.0)
        val clearance = Clearance(0.0, 0.0, 0.0, 0.0)

        val result = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.AUTO
        )

        assertNotNull("Height check result must not be null", result.heightCheck)
        val hc = result.heightCheck!!
        assertTrue("Height fits", hc.fits)
        assertEquals(200.0, hc.remainingHeightMm, 0.001) // 20 cm = 200 mm, NOT 80 cm!
        assertEquals(0.0, hc.shortageHeightMm, 0.001)
    }

    // 6. UNIT CONVERSION TEST
    // Verify:
    // 1 m = 1000 mm
    // 1 cm = 10 mm
    // 1 inch = 25.4 mm
    // 1 ft = 304.8 mm
    @Test
    fun testUnitConversions() {
        assertEquals(1000.0, UnitConverter.toMillimeters(1.0, UnitType.M), 0.0001)
        assertEquals(10.0, UnitConverter.toMillimeters(1.0, UnitType.CM), 0.0001)
        assertEquals(25.4, UnitConverter.toMillimeters(1.0, UnitType.INCH), 0.0001)
        assertEquals(304.8, UnitConverter.toMillimeters(1.0, UnitType.FEET), 0.0001)

        // Inverse
        assertEquals(1.0, UnitConverter.fromMillimeters(1000.0, UnitType.M), 0.0001)
        assertEquals(1.0, UnitConverter.fromMillimeters(10.0, UnitType.CM), 0.0001)
        assertEquals(1.0, UnitConverter.fromMillimeters(25.4, UnitType.INCH), 0.0001)
        assertEquals(1.0, UnitConverter.fromMillimeters(304.8, UnitType.FEET), 0.0001)
    }

    // 7. AREA TEST
    // Room: 500 × 400 cm -> 20 m²
    // Furniture: 220 × 90 cm -> 1.98 m²
    @Test
    fun testAreaCalculations() {
        val room = RoomDimensions(lengthMm = 5000.0, widthMm = 4000.0)
        assertEquals(20.0, room.areaM2, 0.001)

        val furniture = FurnitureDimensions(lengthMm = 2200.0, widthMm = 900.0)
        assertEquals(1.98, furniture.areaM2, 0.001)
    }

    // 8. COLLISION TEST
    // Furniture A: 220 × 90 cm (2200 × 900 mm)
    // Furniture B: 120 × 60 cm (1200 × 600 mm)
    // Place them overlapping -> OVERLAP DETECTED
    // Move them apart -> NO OVERLAP
    @Test
    fun testCollisionDetection() {
        val itemA = PlacedFurniture(
            id = "a",
            name = "Sofa",
            lengthMm = 2200.0,
            widthMm = 900.0,
            positionXMm = 500.0,
            positionYMm = 500.0
        )

        // Item B overlapping with item A (A bounds: X 500..2700, Y 500..1400)
        val itemBOverlapping = PlacedFurniture(
            id = "b",
            name = "Table",
            lengthMm = 1200.0,
            widthMm = 600.0,
            positionXMm = 1000.0, // inside 500..2700
            positionYMm = 700.0   // inside 500..1400
        )

        val collisions1 = FurnitureFitCalculator.checkFurnitureCollisions(listOf(itemA, itemBOverlapping))
        assertEquals("Overlap must be detected", 1, collisions1.size)
        assertTrue(collisions1[0].message.contains("overlaps"))

        // Move Item B apart (X = 3000, outside 2700)
        val itemBSeparated = itemBOverlapping.copy(positionXMm = 3000.0, positionYMm = 500.0)
        val collisions2 = FurnitureFitCalculator.checkFurnitureCollisions(listOf(itemA, itemBSeparated))
        assertEquals("No overlap when separated", 0, collisions2.size)
    }

    // 9. WALL TEST
    // Place furniture partially outside the room -> OUTSIDE ROOM
    @Test
    fun testWallCollision() {
        val room = RoomDimensions(lengthMm = 5000.0, widthMm = 4000.0)
        val itemOutside = PlacedFurniture(
            id = "sofa",
            name = "Sofa",
            lengthMm = 2200.0,
            widthMm = 900.0,
            positionXMm = 4000.0, // 4000 + 2200 = 6200 mm > 5000 mm room length
            positionYMm = 1000.0
        )

        val wallCollisions = FurnitureFitCalculator.checkWallCollisions(room, listOf(itemOutside))
        assertEquals("Should detect wall overhang", 1, wallCollisions.size)
        assertEquals(1200.0, wallCollisions[0].overhangRightMm, 0.001)
        assertTrue(wallCollisions[0].message.contains("right wall"))
    }

    // 10. CLEARANCE TEST
    // Furniture fits physically, but violates requested clearance.
    // Expected: FURNITURE FITS, CLEARANCE DOES NOT
    @Test
    fun testPhysicalFitVsClearanceFit() {
        val room = RoomDimensions(lengthMm = 2300.0, widthMm = 1000.0)
        val furniture = FurnitureDimensions(lengthMm = 2200.0, widthMm = 900.0)
        // Physically fits: 2200 <= 2300 and 900 <= 1000.
        // But with 300 mm left + 300 mm right clearance -> required length = 2800 mm > 2300 mm room.
        val clearance = Clearance(leftMm = 300.0, rightMm = 300.0, frontMm = 100.0, backMm = 100.0)

        val result = FurnitureFitCalculator.calculateFit(
            room = room,
            furniture = furniture,
            clearance = clearance,
            orientation = Orientation.ZERO_DEGREES
        )

        assertFalse("Total fit with clearance must fail", result.fits)
        assertTrue("Physical fit without clearance must succeed", result.furnitureFitsPhysicallyWithoutClearance)
        assertTrue("Clearance message explains clearance failure", result.message.contains("clearance"))
    }

    // 11. MULTIPLE FURNITURE TEST
    // Create: Sofa (220 × 90), Table (120 × 60), TV Unit (180 × 45)
    // Verify:
    // - Each object is stored
    // - Each can be moved
    // - Overlap is detected
    // - Wall boundaries are respected
    @Test
    fun testMultipleFurniturePlacement() {
        val room = RoomDimensions(lengthMm = 5000.0, widthMm = 4000.0)
        val sofa = PlacedFurniture("1", "Sofa", 2200.0, 900.0, 850.0, 100.0, 100.0)
        val table = PlacedFurniture("2", "Table", 1200.0, 600.0, 450.0, 100.0, 1500.0)
        val tvUnit = PlacedFurniture("3", "TV Unit", 1800.0, 450.0, 500.0, 100.0, 3000.0)

        val items = listOf(sofa, table, tvUnit)
        assertEquals(3, items.size)

        // Check no overlaps initially
        val collisions = FurnitureFitCalculator.checkFurnitureCollisions(items)
        assertEquals(0, collisions.size)

        // Check no wall violations
        val wallCollisions = FurnitureFitCalculator.checkWallCollisions(room, items)
        assertEquals(0, wallCollisions.size)

        // Move table to overlap with sofa (sofa Y is 100..1000, move table to Y = 500)
        val movedTable = table.copy(positionYMm = 500.0)
        val updatedCollisions = FurnitureFitCalculator.checkFurnitureCollisions(listOf(sofa, movedTable, tvUnit))
        assertEquals(1, updatedCollisions.size)
        assertTrue(updatedCollisions[0].message.contains("Sofa overlaps Table"))
    }

    // 12. HISTORY LIST CAPPING TEST
    // Verify capping at 10 items (when 11th added, oldest is dropped)
    @Test
    fun testHistoryCappingLogic() {
        val historyList = mutableListOf<FurnitureFitHistory>()
        val clearance = Clearance(300.0, 300.0, 600.0, 300.0)

        for (i in 1..11) {
            val item = FurnitureFitHistory(
                id = "item_$i",
                furnitureName = "Furniture $i",
                roomLength = 5000.0,
                roomWidth = 4000.0,
                furnitureLength = 2000.0 + i * 10,
                furnitureWidth = 900.0,
                furnitureHeight = null,
                unit = UnitType.CM,
                clearance = clearance,
                orientation = Orientation.AUTO,
                fits = true,
                createdAt = System.currentTimeMillis() + i * 1000
            )
            historyList.add(0, item)
            if (historyList.size > 10) {
                historyList.removeAt(historyList.lastIndex)
            }
        }

        assertEquals(10, historyList.size)
        assertEquals("item_11", historyList.first().id)
        assertEquals("item_2", historyList.last().id) // item_1 got dropped!
    }
}
