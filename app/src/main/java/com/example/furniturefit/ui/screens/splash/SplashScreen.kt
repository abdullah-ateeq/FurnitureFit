package com.example.furniturefit.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furniturefit.ui.theme.SlatePrimary
import com.example.furniturefit.ui.theme.TechnicalPass
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        delay(250)
        onSplashFinished()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FurnitureFit",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Check whether furniture dimensions fit inside a room.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Technical blueprint preview box
            Box(
                modifier = Modifier
                    .size(260.dp, 190.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val outlineColor = MaterialTheme.colorScheme.outline
                val passColor = TechnicalPass

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val p = progress.value
                    val w = size.width
                    val h = size.height

                    // Outer room outline (drawn animated)
                    val inset = 16.dp.toPx()
                    val roomW = (w - inset * 2) * p
                    val roomH = (h - inset * 2) * p

                    val roomLeft = inset
                    val roomTop = inset

                    // Room border
                    drawRoundRect(
                        color = outlineColor,
                        topLeft = Offset(roomLeft, roomTop),
                        size = Size(roomW, roomH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    if (p > 0.4f) {
                        val furnProgress = ((p - 0.4f) / 0.6f).coerceIn(0f, 1f)
                        val furnW = 120.dp.toPx() * furnProgress
                        val furnH = 65.dp.toPx() * furnProgress
                        val furnLeft = (w - furnW) / 2
                        val furnTop = (h - furnH) / 2

                        // Clearance zone dashed
                        val clrInset = 12.dp.toPx()
                        drawRoundRect(
                            color = primaryColor.copy(alpha = 0.4f),
                            topLeft = Offset(furnLeft - clrInset, furnTop - clrInset),
                            size = Size(furnW + clrInset * 2, furnH + clrInset * 2),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                        )

                        // Furniture solid box
                        drawRoundRect(
                            color = primaryColor.copy(alpha = 0.85f),
                            topLeft = Offset(furnLeft, furnTop),
                            size = Size(furnW, furnH),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )

                        // Measuring line
                        val lineY = furnTop - 4.dp.toPx()
                        drawLine(
                            color = primaryColor,
                            start = Offset(furnLeft, lineY),
                            end = Offset(furnLeft + furnW, lineY),
                            strokeWidth = 1.5.dp.toPx()
                        )
                    }
                }

                if (progress.value > 0.65f) {
                    Text(
                        text = "SOFA",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "120 × 80 cm",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "✓ FITS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TechnicalPass,
                letterSpacing = 0.5.sp
            )
        }
    }
}
