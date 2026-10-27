package com.example.furniturefit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furniturefit.data.calculator.UnitConverter
import com.example.furniturefit.data.model.Clearance
import com.example.furniturefit.data.model.FitResult
import com.example.furniturefit.data.model.FurnitureDimensions
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.RoomDimensions
import com.example.furniturefit.data.model.UnitType
import com.example.furniturefit.ui.theme.TechnicalFail
import com.example.furniturefit.ui.theme.TechnicalFailContainer
import com.example.furniturefit.ui.theme.TechnicalPass
import com.example.furniturefit.ui.theme.TechnicalPassContainer
import com.example.furniturefit.ui.theme.TechnicalWarning
import com.example.furniturefit.ui.theme.TechnicalWarningContainer
import java.util.Locale

@Composable
fun FitResultCard(
    result: FitResult,
    room: RoomDimensions,
    furniture: FurnitureDimensions,
    clearance: Clearance,
    unit: UnitType,
    onSaveCheck: () -> Unit,
    onOpenFloorPlan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fits = result.fits
    val statusBg = if (fits) TechnicalPassContainer else TechnicalFailContainer
    val statusColor = if (fits) TechnicalPass else TechnicalFail
    val statusIcon = if (fits) Icons.Default.CheckCircle else Icons.Default.Close

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(statusColor.copy(alpha = 0.6f)),
            width = 1.5.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column {
                    Text(
                        text = if (result.fitsBothWays) "✓ FITS BOTH WAYS"
                        else if (fits) "✓ FITS"
                        else "✕ DOES NOT FIT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = result.message,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tight fit warning if applicable
            if (result.isTightFit) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TechnicalWarningContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = TechnicalWarning,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = "⚠ TIGHT FIT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechnicalWarning
                        )
                        Text(
                            text = "Only ${UnitConverter.formatMm(result.fitMarginMm, unit)} remains on the narrowest side. Consider allowing more clearance.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Comparison Metrics Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricColumn(
                    label = "ROOM",
                    primary = "${UnitConverter.formatMm(room.lengthMm, unit, 1, false)} × ${UnitConverter.formatMm(room.widthMm, unit, 1, true)}"
                )
                MetricColumn(
                    label = "FURNITURE",
                    primary = "${UnitConverter.formatMm(furniture.lengthMm, unit, 1, false)} × ${UnitConverter.formatMm(furniture.widthMm, unit, 1, true)}"
                )
                MetricColumn(
                    label = "REQ FOOTPRINT",
                    primary = "${UnitConverter.formatMm(result.requiredLengthMm, unit, 1, false)} × ${UnitConverter.formatMm(result.requiredWidthMm, unit, 1, true)}"
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Orientation Breakdown Card
            Text(
                text = "ORIENTATION ANALYSIS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 0° Box
                result.zeroDegreesDetail?.let { detail ->
                    OrientationMiniCard(
                        title = "0° (Lengthwise)",
                        fits = detail.fits,
                        remainingOrShortage = if (detail.fits) {
                            "Remaining: ${UnitConverter.formatMm(detail.remainingLengthMm, unit, 0, false)} × ${UnitConverter.formatMm(detail.remainingWidthMm, unit, 0, true)}"
                        } else {
                            "Short by: ${UnitConverter.formatMm(maxOf(detail.shortageLengthMm, detail.shortageWidthMm), unit, 1, true)}"
                        },
                        isRecommended = result.orientation == Orientation.ZERO_DEGREES,
                        modifier = Modifier.weight(1f)
                    )
                }
                // 90° Box
                result.ninetyDegreesDetail?.let { detail ->
                    OrientationMiniCard(
                        title = "90° (Rotated)",
                        fits = detail.fits,
                        remainingOrShortage = if (detail.fits) {
                            "Remaining: ${UnitConverter.formatMm(detail.remainingLengthMm, unit, 0, false)} × ${UnitConverter.formatMm(detail.remainingWidthMm, unit, 0, true)}"
                        } else {
                            "Short by: ${UnitConverter.formatMm(maxOf(detail.shortageLengthMm, detail.shortageWidthMm), unit, 1, true)}"
                        },
                        isRecommended = result.orientation == Orientation.NINETY_DEGREES,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fit Margin & Remaining Space Row
            if (fits) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fit Margin (Narrowest Remaining):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = UnitConverter.formatMm(result.fitMarginMm, unit),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Height Check if present
            result.heightCheck?.let { hc ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (hc.fits) TechnicalPassContainer.copy(alpha = 0.5f) else TechnicalFailContainer.copy(alpha = 0.5f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (hc.fits) "Vertical Clearance:" else "✕ Height shortage:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (hc.fits) TechnicalPass else TechnicalFail
                    )
                    Text(
                        text = if (hc.fits) UnitConverter.formatMm(hc.remainingHeightMm, unit) else "Short by ${UnitConverter.formatMm(hc.shortageHeightMm, unit)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hc.fits) TechnicalPass else TechnicalFail
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Area Utilization
            val roomAreaM2 = room.areaMm2 / 1_000_000.0
            val furnAreaM2 = furniture.areaMm2 / 1_000_000.0
            val reqAreaM2 = result.requiredAreaMm2 / 1_000_000.0
            val furnUtilPct = if (roomAreaM2 > 0) (furnAreaM2 / roomAreaM2) * 100.0 else 0.0
            val reqUtilPct = if (roomAreaM2 > 0) (reqAreaM2 / roomAreaM2) * 100.0 else 0.0

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = "ROOM AREA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = UnitConverter.formatArea(room.areaMm2, unit),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column {
                    Text(
                        text = "FURNITURE FOOTPRINT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${UnitConverter.formatArea(furniture.areaMm2, unit)} (${String.format(Locale.US, "%.1f", furnUtilPct)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column {
                    Text(
                        text = "WITH CLEARANCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${UnitConverter.formatArea(result.requiredAreaMm2, unit)} (${String.format(Locale.US, "%.1f", reqUtilPct)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onSaveCheck,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "SAVE CHECK", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenFloorPlan,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "VISUALIZE FIT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(label: String, primary: String) {
    Column {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        Text(
            text = primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun OrientationMiniCard(
    title: String,
    fits: Boolean,
    remainingOrShortage: String,
    isRecommended: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (isRecommended) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (isRecommended) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        else null
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (fits) "✓ Fits" else "✕ Fails",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (fits) TechnicalPass else TechnicalFail
                )
            }
            Text(
                text = remainingOrShortage,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (isRecommended) {
                Text(
                    text = "Recommended",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
