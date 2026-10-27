package com.example.furniturefit.ui.screens.floorplan

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furniturefit.data.calculator.UnitConverter
import com.example.furniturefit.data.model.DoorSwing
import com.example.furniturefit.data.model.FurniturePreset
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.PlacedFurniture
import com.example.furniturefit.data.model.WallSide
import com.example.furniturefit.ui.theme.CanvasClearanceBorder
import com.example.furniturefit.ui.theme.CanvasClearanceFill
import com.example.furniturefit.ui.theme.CanvasDoorArc
import com.example.furniturefit.ui.theme.CanvasFurnitureFill
import com.example.furniturefit.ui.theme.CanvasGrid
import com.example.furniturefit.ui.theme.CanvasRoomFill
import com.example.furniturefit.ui.theme.CanvasRoomWall
import com.example.furniturefit.ui.theme.CanvasWalkingPath
import com.example.furniturefit.ui.theme.TechnicalFail
import com.example.furniturefit.ui.theme.TechnicalFailContainer
import com.example.furniturefit.ui.theme.TechnicalPass
import com.example.furniturefit.ui.theme.TechnicalWarning
import com.example.furniturefit.ui.theme.TechnicalWarningContainer
import com.example.furniturefit.ui.viewmodel.FurnitureFitViewModel
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FloorPlanScreen(
    viewModel: FurnitureFitViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Zoom & Pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Grid menu state
    var showAddMenu by remember { mutableStateOf(false) }

    val activeItem = state.placedFurnitureList.getOrNull(state.activeFurnitureIndex)
        ?: state.placedFurnitureList.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Floor Plan Visualization",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Room ${UnitConverter.formatMm(state.room.lengthMm, state.unit, 0, false)} × ${UnitConverter.formatMm(state.room.widthMm, state.unit, 0, true)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scale = 1f
                        panOffset = Offset.Zero
                    }) {
                        Icon(
                            imageVector = Icons.Default.FitScreen,
                            contentDescription = "Fit to Screen",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                "FurnitureFit Floor Plan: ${state.furnitureName}"
                            )
                            val summary = buildString {
                                appendLine("=== FurnitureFit Room Report ===")
                                appendLine("Room: ${UnitConverter.formatMm(state.room.lengthMm, state.unit)} × ${UnitConverter.formatMm(state.room.widthMm, state.unit)}")
                                appendLine("Furniture: ${state.furnitureName} (${UnitConverter.formatMm(state.furniture.lengthMm, state.unit)} × ${UnitConverter.formatMm(state.furniture.widthMm, state.unit)})")
                                appendLine("Status: ${if (state.fitResult?.fits == true) "FITS ✓" else "DOES NOT FIT ✕"}")
                                state.fitResult?.let { res ->
                                    appendLine("Orientation: ${res.orientation?.shortLabel ?: "N/A"}")
                                    appendLine("Remaining: ${UnitConverter.formatMm(res.remainingLengthMm, state.unit)} × ${UnitConverter.formatMm(res.remainingWidthMm, state.unit)}")
                                }
                                appendLine("Clearances: Left ${UnitConverter.formatMm(state.clearance.leftMm, state.unit)}, Right ${UnitConverter.formatMm(state.clearance.rightMm, state.unit)}, Front ${UnitConverter.formatMm(state.clearance.frontMm, state.unit)}, Back ${UnitConverter.formatMm(state.clearance.backMm, state.unit)}")
                            }
                            putExtra(Intent.EXTRA_TEXT, summary)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Fit Report"))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Collision Alerts Banner (if any)
            if (state.floorPlanCollisions.isNotEmpty() || state.floorPlanWallCollisions.isNotEmpty() || state.floorPlanDoorObstructions.isNotEmpty()) {
                Surface(
                    color = TechnicalFailContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        state.floorPlanCollisions.forEach { col ->
                            Text(
                                text = "✕ ${col.message}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechnicalFail
                            )
                        }
                        state.floorPlanWallCollisions.forEach { wallCol ->
                            Text(
                                text = "✕ ${wallCol.message}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechnicalFail
                            )
                        }
                        state.floorPlanDoorObstructions.forEach { doorObs ->
                            Text(
                                text = "✕ ${doorObs.message}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechnicalFail
                            )
                        }
                    }
                }
            }

            // Interactive Canvas Area (Takes ~55% of height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE9EDF0))
                    .clipToBounds()
            ) {
                val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
                    scale = (scale * zoomChange).coerceIn(0.5f, 4f)
                    panOffset += panChange
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .transformable(state = transformableState)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                panOffset += dragAmount
                            }
                        }
                ) {
                    val canvasW = size.width
                    val canvasH = size.height

                    // Coordinate mapping: convert room mm to screen px
                    val paddingPx = 48.dp.toPx()
                    val roomMmW = state.room.lengthMm.toFloat()
                    val roomMmH = state.room.widthMm.toFloat()

                    val baseScaleX = (canvasW - paddingPx * 2) / roomMmW
                    val baseScaleY = (canvasH - paddingPx * 2) / roomMmH
                    val baseScale = min(baseScaleX, baseScaleY)
                    val currentScale = baseScale * scale

                    val roomPxW = roomMmW * currentScale
                    val roomPxH = roomMmH * currentScale

                    val originX = (canvasW - roomPxW) / 2f + panOffset.x
                    val originY = (canvasH - roomPxH) / 2f + panOffset.y

                    // 1. Draw Grid
                    if (state.settings.showGrid) {
                        val gridStepMm = state.settings.gridSpacingMm.toFloat()
                        val gridStepPx = gridStepMm * currentScale

                        var gx = originX
                        while (gx <= originX + roomPxW) {
                            drawLine(
                                color = CanvasGrid,
                                start = Offset(gx, originY),
                                end = Offset(gx, originY + roomPxH),
                                strokeWidth = 1f
                            )
                            gx += gridStepPx
                        }

                        var gy = originY
                        while (gy <= originY + roomPxH) {
                            drawLine(
                                color = CanvasGrid,
                                start = Offset(originX, gy),
                                end = Offset(originX + roomPxW, gy),
                                strokeWidth = 1f
                            )
                            gy += gridStepPx
                        }
                    }

                    // 2. Draw Room Interior and Walls
                    drawRect(
                        color = CanvasRoomFill,
                        topLeft = Offset(originX, originY),
                        size = Size(roomPxW, roomPxH)
                    )

                    val wallThicknessPx = 6.dp.toPx()
                    drawRect(
                        color = CanvasRoomWall,
                        topLeft = Offset(originX, originY),
                        size = Size(roomPxW, roomPxH),
                        style = Stroke(width = wallThicknessPx)
                    )

                    // 3. Draw Architectural Features (Door & Window)
                    val door = state.doorFeature
                    if (door.enabled) {
                        val doorWidthPx = door.widthMm.toFloat() * currentScale
                        val doorCornerPx = door.distanceFromCornerMm.toFloat() * currentScale
                        drawDoor(
                            originX = originX,
                            originY = originY,
                            roomPxW = roomPxW,
                            roomPxH = roomPxH,
                            doorWidthPx = doorWidthPx,
                            doorCornerPx = doorCornerPx,
                            wall = door.wall,
                            swing = door.swing
                        )
                    }

                    val window = state.windowFeature
                    if (window.enabled) {
                        val winWidthPx = window.widthMm.toFloat() * currentScale
                        val winCornerPx = window.distanceFromCornerMm.toFloat() * currentScale
                        drawWindow(
                            originX = originX,
                            originY = originY,
                            roomPxW = roomPxW,
                            roomPxH = roomPxH,
                            winWidthPx = winWidthPx,
                            winCornerPx = winCornerPx,
                            wall = window.wall
                        )
                    }

                    // 4. Draw Placed Furniture Items
                    state.placedFurnitureList.forEachIndexed { idx, item ->
                        val isSelected = idx == state.activeFurnitureIndex
                        val furnEffLen = item.effectiveLengthMm.toFloat()
                        val furnEffWid = item.effectiveWidthMm.toFloat()

                        val furnLeftPx = originX + item.positionXMm.toFloat() * currentScale
                        val furnTopPx = originY + item.positionYMm.toFloat() * currentScale
                        val furnWidthPx = furnEffLen * currentScale
                        val furnHeightPx = furnEffWid * currentScale

                        // Clearance Zone
                        if (state.settings.showClearance) {
                            val cLeftPx = item.clearance.leftMm.toFloat() * currentScale
                            val cRightPx = item.clearance.rightMm.toFloat() * currentScale
                            val cTopPx = item.clearance.backMm.toFloat() * currentScale
                            val cBottomPx = item.clearance.frontMm.toFloat() * currentScale

                            val clrLeft = furnLeftPx - cLeftPx
                            val clrTop = furnTopPx - cTopPx
                            val clrWidth = furnWidthPx + cLeftPx + cRightPx
                            val clrHeight = furnHeightPx + cTopPx + cBottomPx

                            drawRect(
                                color = CanvasClearanceFill,
                                topLeft = Offset(clrLeft, clrTop),
                                size = Size(clrWidth, clrHeight)
                            )
                            drawRect(
                                color = CanvasClearanceBorder.copy(alpha = 0.6f),
                                topLeft = Offset(clrLeft, clrTop),
                                size = Size(clrWidth, clrHeight),
                                style = Stroke(
                                    width = 1.5f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            )
                        }

                        // Walking Path Highlight if enabled
                        if (state.reserveWalkingClearance) {
                            val walkPx = (state.walkingWidthInput.toFloatOrNull() ?: 80f) * 10f * currentScale
                            drawRect(
                                color = CanvasWalkingPath,
                                topLeft = Offset(furnLeftPx - walkPx, furnTopPx - walkPx),
                                size = Size(furnWidthPx + walkPx * 2, furnHeightPx + walkPx * 2),
                                style = Stroke(
                                    width = 1.5f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )
                            )
                        }

                        // Furniture Body
                        val itemColor = Color(item.colorArgb)
                        drawRoundRect(
                            color = itemColor,
                            topLeft = Offset(furnLeftPx, furnTopPx),
                            size = Size(furnWidthPx, furnHeightPx),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // Selection Border
                        if (isSelected) {
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(furnLeftPx - 2f, furnTopPx - 2f),
                                size = Size(furnWidthPx + 4f, furnHeightPx + 4f),
                                cornerRadius = CornerRadius(6f, 6f),
                                style = Stroke(width = 2.5f)
                            )
                        }

                        // Furniture Label & Dimension
                        if (state.settings.showFurnitureLabels && furnWidthPx > 40 && furnHeightPx > 20) {
                            drawContext.canvas.nativeCanvas.apply {
                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = (11f * scale).coerceIn(10f, 18f)
                                    isFakeBoldText = true
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                                val textX = furnLeftPx + furnWidthPx / 2f
                                val textY = furnTopPx + furnHeightPx / 2f + 4f
                                drawText(item.name.take(12), textX, textY, paint)
                            }
                        }
                    }

                    // 5. Draw Room Dimension Lines
                    if (state.settings.showDimensions) {
                        drawDimensionLineH(
                            startX = originX,
                            endX = originX + roomPxW,
                            y = originY - 18.dp.toPx(),
                            label = "${UnitConverter.formatMm(state.room.lengthMm, state.unit, 0, false)} ${state.unit.symbol}"
                        )
                        drawDimensionLineV(
                            startY = originY,
                            endY = originY + roomPxH,
                            x = originX + roomPxW + 18.dp.toPx(),
                            label = "${UnitConverter.formatMm(state.room.widthMm, state.unit, 0, false)} ${state.unit.symbol}"
                        )
                    }
                }

                // Canvas Overlay Floating Controls (Zoom +/- and Scale tag)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Scale ${String.format(java.util.Locale.US, "%.1fx", scale)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        IconButton(
                            onClick = { scale = (scale + 0.25f).coerceAtMost(4f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In")
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        IconButton(
                            onClick = { scale = (scale - 0.25f).coerceAtLeast(0.5f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                        }
                    }
                }
            }

            // Lower Section: Furniture Positioning & Multi-Item Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Item Selector Chips (Up to 10 items)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROOM FURNITURE (${state.placedFurnitureList.size}/10)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Box {
                        OutlinedButton(
                            onClick = { showAddMenu = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Item", fontSize = 11.sp)
                        }

                        DropdownMenu(
                            expanded = showAddMenu,
                            onDismissRequest = { showAddMenu = false }
                        ) {
                            FurniturePreset.ALL.take(8).forEach { preset ->
                                DropdownMenuItem(
                                    text = { Text(preset.name) },
                                    onClick = {
                                        viewModel.addAdditionalFurniture(preset)
                                        showAddMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.placedFurnitureList.forEachIndexed { idx, item ->
                        FilterChip(
                            selected = idx == state.activeFurnitureIndex,
                            onClick = { viewModel.setActiveFurnitureIndex(idx) },
                            label = { Text(item.name, fontSize = 12.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(item.colorArgb), CircleShape)
                                )
                            }
                        )
                    }
                }

                // Active Item Position & Clearance Readout
                activeItem?.let { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.name} (${UnitConverter.formatMm(item.effectiveLengthMm, state.unit, 0, false)} × ${UnitConverter.formatMm(item.effectiveWidthMm, state.unit, 0, true)})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { viewModel.rotatePlacedFurniture(item.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.RotateRight,
                                            contentDescription = "Rotate",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (state.placedFurnitureList.size > 1) {
                                        IconButton(
                                            onClick = { viewModel.removePlacedFurniture(item.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = TechnicalFail
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sliders for Position X and Y
                            val maxX = maxOf(0.0, state.room.lengthMm - item.effectiveLengthMm)
                            val maxY = maxOf(0.0, state.room.widthMm - item.effectiveWidthMm)

                            Text(
                                text = "Position X (from left wall): ${UnitConverter.formatMm(item.positionXMm, state.unit)}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Slider(
                                value = item.positionXMm.toFloat(),
                                onValueChange = { viewModel.updatePlacedFurniturePosition(item.id, it.toDouble(), item.positionYMm) },
                                valueRange = 0f..max(maxX.toFloat(), 1f),
                                modifier = Modifier.height(24.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Position Y (from top wall): ${UnitConverter.formatMm(item.positionYMm, state.unit)}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Slider(
                                value = item.positionYMm.toFloat(),
                                onValueChange = { viewModel.updatePlacedFurniturePosition(item.id, item.positionXMm, it.toDouble()) },
                                valueRange = 0f..max(maxY.toFloat(), 1f),
                                modifier = Modifier.height(24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Live Wall Clearances for this item
                            val leftClear = item.positionXMm
                            val rightClear = state.room.lengthMm - (item.positionXMm + item.effectiveLengthMm)
                            val topClear = item.positionYMm
                            val bottomClear = state.room.widthMm - (item.positionYMm + item.effectiveWidthMm)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ClearanceIndicator("Left", leftClear, item.clearance.leftMm, state.unit)
                                ClearanceIndicator("Right", rightClear, item.clearance.rightMm, state.unit)
                                ClearanceIndicator("Front", bottomClear, item.clearance.frontMm, state.unit)
                                ClearanceIndicator("Back", topClear, item.clearance.backMm, state.unit)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClearanceIndicator(
    label: String,
    actualMm: Double,
    requiredMm: Double,
    unit: com.example.furniturefit.data.model.UnitType
) {
    val ok = actualMm >= requiredMm
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = UnitConverter.formatMm(actualMm, unit, 0, false),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (ok) TechnicalPass else TechnicalFail
        )
        Text(
            text = if (ok) "✓" else "✕",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (ok) TechnicalPass else TechnicalFail
        )
    }
}

private fun DrawScope.drawDimensionLineH(startX: Float, endX: Float, y: Float, label: String) {
    val lineCol = CanvasRoomWall
    drawLine(color = lineCol, start = Offset(startX, y), end = Offset(endX, y), strokeWidth = 1.5f)
    // Arrows
    drawLine(color = lineCol, start = Offset(startX, y), end = Offset(startX + 6f, y - 4f), strokeWidth = 1.5f)
    drawLine(color = lineCol, start = Offset(startX, y), end = Offset(startX + 6f, y + 4f), strokeWidth = 1.5f)
    drawLine(color = lineCol, start = Offset(endX, y), end = Offset(endX - 6f, y - 4f), strokeWidth = 1.5f)
    drawLine(color = lineCol, start = Offset(endX, y), end = Offset(endX - 6f, y + 4f), strokeWidth = 1.5f)

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = lineCol.toArgb()
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawText(label, (startX + endX) / 2f, y - 6f, paint)
    }
}

private fun DrawScope.drawDimensionLineV(startY: Float, endY: Float, x: Float, label: String) {
    val lineCol = CanvasRoomWall
    drawLine(color = lineCol, start = Offset(x, startY), end = Offset(x, endY), strokeWidth = 1.5f)
    // Arrows
    drawLine(color = lineCol, start = Offset(x, startY), end = Offset(x - 4f, startY + 6f), strokeWidth = 1.5f)
    drawLine(color = lineCol, start = Offset(x, startY), end = Offset(x + 4f, startY + 6f), strokeWidth = 1.5f)
    drawLine(color = lineCol, start = Offset(x, endY), end = Offset(x - 4f, endY - 6f), strokeWidth = 1.5f)
    drawLine(color = lineCol, start = Offset(x, endY), end = Offset(x + 4f, endY - 6f), strokeWidth = 1.5f)

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = lineCol.toArgb()
            textSize = 24f
            textAlign = android.graphics.Paint.Align.LEFT
        }
        drawText(label, x + 8f, (startY + endY) / 2f + 8f, paint)
    }
}

private fun DrawScope.drawDoor(
    originX: Float,
    originY: Float,
    roomPxW: Float,
    roomPxH: Float,
    doorWidthPx: Float,
    doorCornerPx: Float,
    wall: WallSide,
    swing: DoorSwing
) {
    val doorColor = Color(0xFF64748B)
    val swingColor = Color(0x6664748B)

    when (wall) {
        WallSide.LEFT -> {
            val dY = originY + doorCornerPx
            // Opening white cut
            drawLine(Color.White, Offset(originX, dY), Offset(originX, dY + doorWidthPx), 8f)
            // Door leaf
            drawLine(doorColor, Offset(originX, dY), Offset(originX + doorWidthPx, dY), 3f)
            // Swing arc
            if (swing == DoorSwing.INWARD) {
                drawArc(
                    color = swingColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(originX - doorWidthPx, dY - doorWidthPx),
                    size = Size(doorWidthPx * 2, doorWidthPx * 2),
                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                )
            }
        }
        WallSide.TOP -> {
            val dX = originX + doorCornerPx
            drawLine(Color.White, Offset(dX, originY), Offset(dX + doorWidthPx, originY), 8f)
            drawLine(doorColor, Offset(dX, originY), Offset(dX, originY + doorWidthPx), 3f)
            if (swing == DoorSwing.INWARD) {
                drawArc(
                    color = swingColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(dX - doorWidthPx, originY - doorWidthPx),
                    size = Size(doorWidthPx * 2, doorWidthPx * 2),
                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
                )
            }
        }
        WallSide.RIGHT -> {
            val dY = originY + doorCornerPx
            drawLine(Color.White, Offset(originX + roomPxW, dY), Offset(originX + roomPxW, dY + doorWidthPx), 8f)
            drawLine(doorColor, Offset(originX + roomPxW, dY), Offset(originX + roomPxW - doorWidthPx, dY), 3f)
        }
        WallSide.BOTTOM -> {
            val dX = originX + doorCornerPx
            drawLine(Color.White, Offset(dX, originY + roomPxH), Offset(dX + doorWidthPx, originY + roomPxH), 8f)
            drawLine(doorColor, Offset(dX, originY + roomPxH), Offset(dX, originY + roomPxH - doorWidthPx), 3f)
        }
    }
}

private fun DrawScope.drawWindow(
    originX: Float,
    originY: Float,
    roomPxW: Float,
    roomPxH: Float,
    winWidthPx: Float,
    winCornerPx: Float,
    wall: WallSide
) {
    val winColor = Color(0xFF0284C7)
    when (wall) {
        WallSide.TOP -> {
            val wX = originX + winCornerPx
            drawLine(Color.White, Offset(wX, originY), Offset(wX + winWidthPx, originY), 8f)
            drawLine(winColor, Offset(wX, originY), Offset(wX + winWidthPx, originY), 4f)
        }
        WallSide.BOTTOM -> {
            val wX = originX + winCornerPx
            drawLine(Color.White, Offset(wX, originY + roomPxH), Offset(wX + winWidthPx, originY + roomPxH), 8f)
            drawLine(winColor, Offset(wX, originY + roomPxH), Offset(wX + winWidthPx, originY + roomPxH), 4f)
        }
        WallSide.LEFT -> {
            val wY = originY + winCornerPx
            drawLine(Color.White, Offset(originX, wY), Offset(originX, wY + winWidthPx), 8f)
            drawLine(winColor, Offset(originX, wY), Offset(originX, wY + winWidthPx), 4f)
        }
        WallSide.RIGHT -> {
            val wY = originY + winCornerPx
            drawLine(Color.White, Offset(originX + roomPxW, wY), Offset(originX + roomPxW, wY + winWidthPx), 8f)
            drawLine(winColor, Offset(originX + roomPxW, wY), Offset(originX + roomPxW, wY + winWidthPx), 4f)
        }
    }
}
