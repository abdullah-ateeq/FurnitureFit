package com.example.furniturefit.data.calculator

import com.example.furniturefit.data.model.Clearance
import com.example.furniturefit.data.model.DoorFeature
import com.example.furniturefit.data.model.DoorSwing
import com.example.furniturefit.data.model.DoorwayCheckResult
import com.example.furniturefit.data.model.FitResult
import com.example.furniturefit.data.model.FurnitureDimensions
import com.example.furniturefit.data.model.HeightCheckResult
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.OrientationFitDetail
import com.example.furniturefit.data.model.PlacedFurniture
import com.example.furniturefit.data.model.RectMm
import com.example.furniturefit.data.model.RoomDimensions
import com.example.furniturefit.data.model.WallSide
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class FurnitureCollision(
    val item1: PlacedFurniture,
    val item2: PlacedFurniture,
    val overlapWidthMm: Double,
    val overlapHeightMm: Double,
    val message: String
)

data class WallCollision(
    val item: PlacedFurniture,
    val overhangLeftMm: Double,
    val overhangRightMm: Double,
    val overhangTopMm: Double,
    val overhangBottomMm: Double,
    val message: String
)

data class DoorObstruction(
    val item: PlacedFurniture,
    val message: String
)

object FurnitureFitCalculator {

    /**
     * Calculates fit for a single furniture item in a rectangular room with specified clearance and orientation.
     */
    fun calculateFit(
        room: RoomDimensions,
        furniture: FurnitureDimensions,
        clearance: Clearance,
        orientation: Orientation,
        tightFitThresholdMm: Double = 100.0
    ): FitResult {
        // Detail for 0° (Length along room length)
        val detail0 = calculateOrientationDetail(
            room = room,
            furnitureLength = furniture.lengthMm,
            furnitureWidth = furniture.widthMm,
            clearance = clearance,
            orientation = Orientation.ZERO_DEGREES
        )

        // Detail for 90° (Length along room width)
        val detail90 = calculateOrientationDetail(
            room = room,
            furnitureLength = furniture.widthMm, // rotated
            furnitureWidth = furniture.lengthMm, // rotated
            clearance = clearance,
            orientation = Orientation.NINETY_DEGREES
        )

        val furnitureAreaMm2 = furniture.lengthMm * furniture.widthMm

        // Optional Height check
        val heightCheck = if (room.heightMm != null && furniture.heightMm != null) {
            val remaining = room.heightMm - furniture.heightMm
            HeightCheckResult(
                roomHeightMm = room.heightMm,
                furnitureHeightMm = furniture.heightMm,
                fits = remaining >= 0,
                remainingHeightMm = max(0.0, remaining),
                shortageHeightMm = max(0.0, -remaining)
            )
        } else null

        return when (orientation) {
            Orientation.AUTO -> {
                val fitsBoth = detail0.fits && detail90.fits
                val chosenDetail = if (fitsBoth) {
                    // Recommend orientation with higher fit margin (or remaining space)
                    if (detail90.fitMarginMm > detail0.fitMarginMm) detail90 else detail0
                } else if (detail0.fits) {
                    detail0
                } else if (detail90.fits) {
                    detail90
                } else {
                    // Neither fits: choose the one with smaller max shortage
                    val maxShortage0 = max(detail0.shortageLengthMm, detail0.shortageWidthMm)
                    val maxShortage90 = max(detail90.shortageLengthMm, detail90.shortageWidthMm)
                    if (maxShortage90 < maxShortage0) detail90 else detail0
                }

                val physicallyFitsEither = detail0.fitsPhysicallyWithoutClearance || detail90.fitsPhysicallyWithoutClearance
                val isOverallFit = detail0.fits || detail90.fits
                val requiredAreaMm2 = chosenDetail.requiredLengthMm * chosenDetail.requiredWidthMm

                val fitMargin = if (isOverallFit) chosenDetail.fitMarginMm else 0.0
                val isTight = isOverallFit && fitMargin > 0.0 && fitMargin < tightFitThresholdMm

                val message = buildAutoFitMessage(fitsBoth, detail0, detail90, chosenDetail, physicallyFitsEither)

                FitResult(
                    fits = isOverallFit,
                    orientation = if (isOverallFit) chosenDetail.orientation else null,
                    requiredLengthMm = chosenDetail.requiredLengthMm,
                    requiredWidthMm = chosenDetail.requiredWidthMm,
                    remainingLengthMm = chosenDetail.remainingLengthMm,
                    remainingWidthMm = chosenDetail.remainingWidthMm,
                    shortageLengthMm = chosenDetail.shortageLengthMm,
                    shortageWidthMm = chosenDetail.shortageWidthMm,
                    furnitureAreaMm2 = furnitureAreaMm2,
                    requiredAreaMm2 = requiredAreaMm2,
                    message = message,
                    fitsBothWays = fitsBoth,
                    fitsZeroDegrees = detail0.fits,
                    fitsNinetyDegrees = detail90.fits,
                    zeroDegreesDetail = detail0,
                    ninetyDegreesDetail = detail90,
                    furnitureFitsPhysicallyWithoutClearance = physicallyFitsEither,
                    fitMarginMm = fitMargin,
                    isTightFit = isTight,
                    heightCheck = heightCheck
                )
            }
            Orientation.ZERO_DEGREES -> {
                buildSingleOrientationResult(
                    detail = detail0,
                    otherDetail = detail90,
                    furnitureAreaMm2 = furnitureAreaMm2,
                    tightFitThresholdMm = tightFitThresholdMm,
                    heightCheck = heightCheck
                )
            }
            Orientation.NINETY_DEGREES -> {
                buildSingleOrientationResult(
                    detail = detail90,
                    otherDetail = detail0,
                    furnitureAreaMm2 = furnitureAreaMm2,
                    tightFitThresholdMm = tightFitThresholdMm,
                    heightCheck = heightCheck
                )
            }
        }
    }

    private fun calculateOrientationDetail(
        room: RoomDimensions,
        furnitureLength: Double,
        furnitureWidth: Double,
        clearance: Clearance,
        orientation: Orientation
    ): OrientationFitDetail {
        val requiredLength = furnitureLength + clearance.leftMm + clearance.rightMm
        val requiredWidth = furnitureWidth + clearance.frontMm + clearance.backMm

        val fitsPhysically = furnitureLength <= room.lengthMm && furnitureWidth <= room.widthMm
        val fits = requiredLength <= room.lengthMm && requiredWidth <= room.widthMm

        val remainingLength = room.lengthMm - requiredLength
        val remainingWidth = room.widthMm - requiredWidth

        val shortageLength = max(0.0, -remainingLength)
        val shortageWidth = max(0.0, -remainingWidth)

        val fitMargin = if (fits) min(remainingLength, remainingWidth) else 0.0

        return OrientationFitDetail(
            orientation = orientation,
            fits = fits,
            fitsPhysicallyWithoutClearance = fitsPhysically,
            requiredLengthMm = requiredLength,
            requiredWidthMm = requiredWidth,
            remainingLengthMm = remainingLength,
            remainingWidthMm = remainingWidth,
            shortageLengthMm = shortageLength,
            shortageWidthMm = shortageWidth,
            fitMarginMm = fitMargin
        )
    }

    private fun buildSingleOrientationResult(
        detail: OrientationFitDetail,
        otherDetail: OrientationFitDetail,
        furnitureAreaMm2: Double,
        tightFitThresholdMm: Double,
        heightCheck: HeightCheckResult?
    ): FitResult {
        val requiredAreaMm2 = detail.requiredLengthMm * detail.requiredWidthMm
        val isTight = detail.fits && detail.fitMarginMm > 0.0 && detail.fitMarginMm < tightFitThresholdMm

        val message = if (detail.fits) {
            "The furniture fits at ${detail.orientation.shortLabel} with the selected clearance requirements."
        } else if (detail.fitsPhysicallyWithoutClearance) {
            "Furniture fits physically, but the requested clearance is not available."
        } else {
            "The furniture is too large for the room at ${detail.orientation.shortLabel}."
        }

        return FitResult(
            fits = detail.fits,
            orientation = if (detail.fits) detail.orientation else null,
            requiredLengthMm = detail.requiredLengthMm,
            requiredWidthMm = detail.requiredWidthMm,
            remainingLengthMm = detail.remainingLengthMm,
            remainingWidthMm = detail.remainingWidthMm,
            shortageLengthMm = detail.shortageLengthMm,
            shortageWidthMm = detail.shortageWidthMm,
            furnitureAreaMm2 = furnitureAreaMm2,
            requiredAreaMm2 = requiredAreaMm2,
            message = message,
            fitsBothWays = detail.fits && otherDetail.fits,
            fitsZeroDegrees = if (detail.orientation == Orientation.ZERO_DEGREES) detail.fits else otherDetail.fits,
            fitsNinetyDegrees = if (detail.orientation == Orientation.NINETY_DEGREES) detail.fits else otherDetail.fits,
            zeroDegreesDetail = if (detail.orientation == Orientation.ZERO_DEGREES) detail else otherDetail,
            ninetyDegreesDetail = if (detail.orientation == Orientation.NINETY_DEGREES) detail else otherDetail,
            furnitureFitsPhysicallyWithoutClearance = detail.fitsPhysicallyWithoutClearance,
            fitMarginMm = if (detail.fits) detail.fitMarginMm else 0.0,
            isTightFit = isTight,
            heightCheck = heightCheck
        )
    }

    private fun buildAutoFitMessage(
        fitsBoth: Boolean,
        detail0: OrientationFitDetail,
        detail90: OrientationFitDetail,
        chosen: OrientationFitDetail,
        physicallyFitsEither: Boolean
    ): String {
        return when {
            fitsBoth -> {
                "Fits both ways. Recommended orientation: ${chosen.orientation.shortLabel} with ${UnitConverter.formatValue(chosen.fitMarginMm / 10.0, 1)} cm margin."
            }
            chosen.fits -> {
                "Fits at ${chosen.orientation.shortLabel}. Other orientation does not fit."
            }
            physicallyFitsEither -> {
                "Furniture fits physically, but the requested clearance is not available in either orientation."
            }
            else -> {
                val maxShortage = max(chosen.shortageLengthMm, chosen.shortageWidthMm)
                "The furniture is too large for the available room area. Largest shortage: ${UnitConverter.formatValue(maxShortage / 10.0, 1)} cm."
            }
        }
    }

    /**
     * Checks if furniture can pass through a doorway.
     */
    fun checkDoorwayDelivery(
        doorWidthMm: Double,
        doorHeightMm: Double,
        furnitureLengthMm: Double,
        furnitureWidthMm: Double,
        furnitureHeightMm: Double
    ): DoorwayCheckResult {
        // A rectangular box can pass upright or sideways through a rectangular opening.
        // Opening dimensions: doorWidth, doorHeight.
        // Box cross sections:
        // 1. (Width x Height) - carrying upright or on its side
        // 2. (Length x Height) - carrying sideways
        // 3. (Width x Length) - carrying flat
        val doorMax = max(doorWidthMm, doorHeightMm)
        val doorMin = min(doorWidthMm, doorHeightMm)

        // Cross-section 1: width & height
        val c1Min = min(furnitureWidthMm, furnitureHeightMm)
        val c1Max = max(furnitureWidthMm, furnitureHeightMm)
        val passC1 = c1Min <= doorMin && c1Max <= doorMax

        // Cross-section 2: length & height
        val c2Min = min(furnitureLengthMm, furnitureHeightMm)
        val c2Max = max(furnitureLengthMm, furnitureHeightMm)
        val passC2 = c2Min <= doorMin && c2Max <= doorMax

        // Cross-section 3: width & length
        val c3Min = min(furnitureWidthMm, furnitureLengthMm)
        val c3Max = max(furnitureWidthMm, furnitureLengthMm)
        val passC3 = c3Min <= doorMin && c3Max <= doorMax

        val passes = passC1 || passC2 || passC3
        val orientationUsed = when {
            passC1 -> "Standard entry (Width × Height profile)"
            passC2 -> "Lengthwise edge entry (Length × Height profile)"
            passC3 -> "Flat entry (Width × Length profile)"
            else -> "No standard rectangular orientation fits"
        }

        return DoorwayCheckResult(
            doorWidthMm = doorWidthMm,
            doorHeightMm = doorHeightMm,
            furnitureWidthMm = furnitureWidthMm,
            furnitureHeightMm = furnitureHeightMm,
            furnitureLengthMm = furnitureLengthMm,
            passes = passes,
            orientationUsed = orientationUsed,
            note = "Actual delivery may depend on turning space, door frame thickness, handles, stairs, and other site conditions."
        )
    }

    /**
     * Checks for collisions among multiple placed furniture items.
     */
    fun checkFurnitureCollisions(items: List<PlacedFurniture>): List<FurnitureCollision> {
        val collisions = mutableListOf<FurnitureCollision>()
        for (i in 0 until items.size) {
            for (j in i + 1 until items.size) {
                val itemA = items[i]
                val itemB = items[j]
                val boundsA = itemA.furnitureBounds
                val boundsB = itemB.furnitureBounds

                val intersection = boundsA.intersection(boundsB)
                if (intersection != null) {
                    val overlapW = intersection.width
                    val overlapH = intersection.height
                    collisions.add(
                        FurnitureCollision(
                            item1 = itemA,
                            item2 = itemB,
                            overlapWidthMm = overlapW,
                            overlapHeightMm = overlapH,
                            message = "${itemA.name} overlaps ${itemB.name} by approximately ${UnitConverter.formatValue(max(overlapW, overlapH) / 10.0, 1)} cm."
                        )
                    )
                }
            }
        }
        return collisions
    }

    /**
     * Checks if placed furniture extends beyond the room walls.
     */
    fun checkWallCollisions(room: RoomDimensions, items: List<PlacedFurniture>): List<WallCollision> {
        val collisions = mutableListOf<WallCollision>()
        for (item in items) {
            val bounds = item.furnitureBounds
            val overhangLeft = if (bounds.left < 0) -bounds.left else 0.0
            val overhangRight = if (bounds.right > room.lengthMm) bounds.right - room.lengthMm else 0.0
            val overhangTop = if (bounds.top < 0) -bounds.top else 0.0
            val overhangBottom = if (bounds.bottom > room.widthMm) bounds.bottom - room.widthMm else 0.0

            if (overhangLeft > 0 || overhangRight > 0 || overhangTop > 0 || overhangBottom > 0) {
                val maxOverhang = maxOf(overhangLeft, overhangRight, overhangTop, overhangBottom)
                val wallName = when (maxOverhang) {
                    overhangLeft -> "left wall"
                    overhangRight -> "right wall"
                    overhangTop -> "top wall"
                    else -> "bottom wall"
                }
                collisions.add(
                    WallCollision(
                        item = item,
                        overhangLeftMm = overhangLeft,
                        overhangRightMm = overhangRight,
                        overhangTopMm = overhangTop,
                        overhangBottomMm = overhangBottom,
                        message = "${item.name} extends ${UnitConverter.formatValue(maxOverhang / 10.0, 1)} cm beyond the $wallName."
                    )
                )
            }
        }
        return collisions
    }

    /**
     * Checks if any furniture item obstructs the door swing if door feature is active.
     */
    fun checkDoorObstruction(
        room: RoomDimensions,
        door: DoorFeature,
        items: List<PlacedFurniture>
    ): List<DoorObstruction> {
        if (!door.enabled || !door.checkObstruction || door.swing != DoorSwing.INWARD) {
            return emptyList()
        }

        // Bounding box approximation of the 90° inward door swing arc
        val doorBox = when (door.wall) {
            WallSide.LEFT -> RectMm(
                left = 0.0,
                top = door.distanceFromCornerMm,
                right = door.widthMm,
                bottom = door.distanceFromCornerMm + door.widthMm
            )
            WallSide.RIGHT -> RectMm(
                left = room.lengthMm - door.widthMm,
                top = door.distanceFromCornerMm,
                right = room.lengthMm,
                bottom = door.distanceFromCornerMm + door.widthMm
            )
            WallSide.TOP -> RectMm(
                left = door.distanceFromCornerMm,
                top = 0.0,
                right = door.distanceFromCornerMm + door.widthMm,
                bottom = door.widthMm
            )
            WallSide.BOTTOM -> RectMm(
                left = door.distanceFromCornerMm,
                top = room.widthMm - door.widthMm,
                right = door.distanceFromCornerMm + door.widthMm,
                bottom = room.widthMm
            )
        }

        val obstructions = mutableListOf<DoorObstruction>()
        for (item in items) {
            if (item.furnitureBounds.intersects(doorBox)) {
                obstructions.add(
                    DoorObstruction(
                        item = item,
                        message = "Door swing obstructed by ${item.name}."
                    )
                )
            }
        }
        return obstructions
    }
}
