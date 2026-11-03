package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderStatus
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.StatusSuccess

@Composable
fun ShippingMapVisualizer(
    progress: Float, // 0.0f to 1.0f
    status: OrderStatus,
    courierName: String,
    estimatedMinutes: Int,
    destinationAddress: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarRipple")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianNight),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("shipping_map_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Live Pulse
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(StatusSuccess)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GPS EN VIVO • RADAR VIP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = AthenaGold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (status == OrderStatus.DELIVERED) "¡ENTREGADO!" else "ETA: $estimatedMinutes min",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas Map Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF191724))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw map road grid lines
                    val roadColor = Color(0xFF28253B)
                    val mainAvenueColor = Color(0xFF35304E)

                    // Secondary streets
                    drawLine(roadColor, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 3f)
                    drawLine(roadColor, Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 3f)
                    drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 3f)
                    drawLine(roadColor, Offset(w * 0.8f, 0f), Offset(w * 0.8f, h), strokeWidth = 3f)

                    // Main diagonal avenue
                    drawLine(
                        mainAvenueColor,
                        Offset(0f, h * 0.1f),
                        Offset(w, h * 0.9f),
                        strokeWidth = 14f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        mainAvenueColor,
                        Offset(0f, h * 0.85f),
                        Offset(w * 0.7f, 0f),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )

                    // Delivery Route Path (Waypoints from Atelier to Destination)
                    val p0 = Offset(w * 0.12f, h * 0.82f) // Atelier Central
                    val p1 = Offset(w * 0.35f, h * 0.65f) // Waypoint 1
                    val p2 = Offset(w * 0.60f, h * 0.38f) // Waypoint 2
                    val p3 = Offset(w * 0.86f, h * 0.22f) // Client Home

                    val fullRoutePath = Path().apply {
                        moveTo(p0.x, p0.y)
                        quadraticTo(p1.x, p1.y + 20f, (p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                        quadraticTo(p2.x, p2.y - 20f, p3.x, p3.y)
                    }

                    // Draw Planned Route (dashed gray-gold)
                    drawPath(
                        path = fullRoutePath,
                        color = Color(0xFF534C69),
                        style = Stroke(
                            width = 6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // Calculate courier live position along progress
                    val currentPos = when {
                        progress <= 0.33f -> {
                            val t = progress / 0.33f
                            Offset(
                                p0.x + (p1.x - p0.x) * t,
                                p0.y + (p1.y - p0.y) * t
                            )
                        }
                        progress <= 0.66f -> {
                            val t = (progress - 0.33f) / 0.33f
                            Offset(
                                p1.x + (p2.x - p1.x) * t,
                                p1.y + (p2.y - p1.y) * t
                            )
                        }
                        else -> {
                            val t = ((progress - 0.66f) / 0.34f).coerceIn(0f, 1f)
                            Offset(
                                p2.x + (p3.x - p2.x) * t,
                                p2.y + (p3.y - p2.y) * t
                            )
                        }
                    }

                    // Draw Completed Route (Solid glowing Athena Gold)
                    drawLine(
                        color = AthenaGold,
                        start = p0,
                        end = currentPos,
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )

                    // Origin Node: Atelier Athena
                    drawCircle(Color(0xFF2E2413), radius = 16f, center = p0)
                    drawCircle(AthenaGold, radius = 9f, center = p0)

                    // Destination Node: Client Penthouse
                    drawCircle(Color(0xFF1E3A2F), radius = 18f, center = p3)
                    drawCircle(StatusSuccess, radius = 10f, center = p3)

                    // Active Moving Courier Marker
                    // Radar Wave 1
                    drawCircle(
                        color = AthenaGold.copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = currentPos,
                        style = Stroke(width = 3f)
                    )
                    // Vehicle core circle
                    drawCircle(color = ObsidianNight, radius = 16f, center = currentPos)
                    drawCircle(color = AthenaGold, radius = 11f, center = currentPos)
                    drawCircle(color = Color.White, radius = 5f, center = currentPos)
                }

                // Overlay UI chips for Atelier and Destination
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC14121E))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Atelier",
                            tint = AthenaGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Atelier Athena", fontSize = 10.sp, color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC14121E))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Destino",
                            tint = StatusSuccess,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(destinationAddress.take(20) + "...", fontSize = 10.sp, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Courier details & Progress Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AthenaGold.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Mensajero",
                        tint = AthenaGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = courierName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = status.title,
                        fontSize = 11.sp,
                        color = AthenaGold
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = AthenaGold,
                trackColor = Color(0xFF28253B),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
