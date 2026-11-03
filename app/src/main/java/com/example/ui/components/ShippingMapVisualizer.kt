package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OrderStatus
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RosaFluor
import com.example.ui.theme.StatusSuccess
import java.util.Locale

@Composable
fun ShippingMapVisualizer(
    progress: Float, // 0.0f to 1.0f
    status: OrderStatus,
    courierName: String,
    estimatedMinutes: Int,
    destinationAddress: String,
    remainingDistanceKm: Double = 3.2,
    latitude: Double = -38.3752,
    longitude: Double = -60.2783,
    isAccepted: Boolean = true,
    courierStatusText: String? = null,
    onAcceptOrder: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "RadarRipple")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 44f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarAlpha"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianNight),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, AthenaGold.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("shipping_map_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con Google Maps Tres Arroyos y botón para abrir
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isAccepted) StatusSuccess else AthenaGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🗺️ GOOGLE MAPS EN VIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = AthenaGold
                        )
                        Text(
                            text = "Tres Arroyos, Bs. As. (CP 7500)",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                // Botón abrir en Google Maps app / web
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF222032))
                        .border(1.dp, AthenaGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .clickable {
                            try {
                                val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(mapIntent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Abriendo Google Maps...", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("open_google_maps_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Abrir Google Maps",
                        tint = AthenaGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ver Mapa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cartel de Tiempo Restante Dinámico en Tiempo Real
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isAccepted) {
                            if (progress >= 1.0f) StatusSuccess.copy(alpha = 0.2f) else RosaFluor.copy(alpha = 0.16f)
                        } else {
                            AthenaGold.copy(alpha = 0.15f)
                        }
                    )
                    .border(
                        1.dp,
                        if (isAccepted) {
                            if (progress >= 1.0f) StatusSuccess else RosaFluor
                        } else {
                            AthenaGold
                        },
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (!isAccepted) "⏳" else if (progress >= 1.0f) "🎉" else "⏱️",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            val etaText = when {
                                !isAccepted -> "Esperando aceptación del Atelier"
                                progress >= 1.0f -> "¡Llegó a destino en Tres Arroyos!"
                                estimatedMinutes <= 2 -> "¡Llegando a tu puerta!"
                                else -> "Faltan $estimatedMinutes min (${String.format(Locale.US, "%.1f", remainingDistanceKm)} km) para llegar"
                            }
                            Text(
                                text = etaText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = courierStatusText ?: "GPS activo en Tres Arroyos (CP 7500)",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    if (isAccepted && progress < 1.0f) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RosaFluor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "EN VIVO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Botón interactivo para que el Atelier / Vendedor acepte el pedido
            if (!isAccepted && onAcceptOrder != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAcceptOrder,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("accept_order_btn")
                ) {
                    Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aceptar Pedido e Iniciar Reparto (Tres Arroyos)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Map Box representativo de Tres Arroyos (CP 7500)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF141320))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Calles y avenidas de Tres Arroyos
                    val roadColor = Color(0xFF252236)
                    val mainAvenueColor = Color(0xFF38334E)

                    // Cuadrícula típica de Tres Arroyos (Avenidas diagonales y ortogonales)
                    drawLine(roadColor, Offset(0f, h * 0.20f), Offset(w, h * 0.20f), strokeWidth = 3f)
                    drawLine(roadColor, Offset(0f, h * 0.50f), Offset(w, h * 0.50f), strokeWidth = 3f)
                    drawLine(roadColor, Offset(0f, h * 0.80f), Offset(w, h * 0.80f), strokeWidth = 3f)
                    drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 3f)
                    drawLine(roadColor, Offset(w * 0.55f, 0f), Offset(w * 0.55f, h), strokeWidth = 3f)
                    drawLine(roadColor, Offset(w * 0.85f, 0f), Offset(w * 0.85f, h), strokeWidth = 3f)

                    // Av. Moreno y Av. Belgrano (Avenidas principales con mayor grosor)
                    drawLine(
                        mainAvenueColor,
                        Offset(0f, h * 0.35f),
                        Offset(w, h * 0.35f),
                        strokeWidth = 14f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        mainAvenueColor,
                        Offset(w * 0.40f, 0f),
                        Offset(w * 0.40f, h),
                        strokeWidth = 12f,
                        cap = StrokeCap.Round
                    )
                    // Diagonal Av. Güemes
                    drawLine(
                        mainAvenueColor,
                        Offset(0f, h * 0.85f),
                        Offset(w * 0.85f, 0f),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )

                    // Plaza San Martín (centro de Tres Arroyos)
                    drawCircle(Color(0xFF1F382B), radius = 22f, center = Offset(w * 0.40f, h * 0.35f))
                    drawCircle(Color(0xFF2E6348), radius = 10f, center = Offset(w * 0.40f, h * 0.35f))

                    // Puntos de la ruta:
                    // P0: Atelier Athena Style (Tres Arroyos)
                    // P3: Domicilio de la compradora (Tres Arroyos CP 7500)
                    val p0 = Offset(w * 0.15f, h * 0.78f) // Atelier Athena
                    val p1 = Offset(w * 0.38f, h * 0.60f) // Av. Belgrano / Av. Moreno
                    val p2 = Offset(w * 0.62f, h * 0.35f) // Zona Centro Tres Arroyos
                    val p3 = Offset(w * 0.84f, h * 0.22f) // Destino compradora

                    val fullRoutePath = Path().apply {
                        moveTo(p0.x, p0.y)
                        quadraticTo(p1.x, p1.y + 15f, (p1.x + p2.x) / 2f, (p1.y + p2.y) / 2f)
                        quadraticTo(p2.x, p2.y - 15f, p3.x, p3.y)
                    }

                    // Trazado de ruta proyectada en Google Maps (gris punteado)
                    drawPath(
                        path = fullRoutePath,
                        color = Color(0xFF5D5477),
                        style = Stroke(
                            width = 6f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // Posición en tiempo real del repartidor según progress
                    val clampedProg = progress.coerceIn(0.0f, 1.0f)
                    val currentPos = when {
                        clampedProg <= 0.33f -> {
                            val t = clampedProg / 0.33f
                            Offset(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t)
                        }
                        clampedProg <= 0.66f -> {
                            val t = (clampedProg - 0.33f) / 0.33f
                            Offset(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t)
                        }
                        else -> {
                            val t = (clampedProg - 0.66f) / 0.34f
                            Offset(p2.x + (p3.x - p2.x) * t, p2.y + (p3.y - p2.y) * t)
                        }
                    }

                    // Recorrido completado en color Oro Athena brillante
                    drawLine(
                        color = if (isAccepted) RosaFluor else AthenaGold,
                        start = p0,
                        end = currentPos,
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )

                    // Marcador Origen: Atelier Athena
                    drawCircle(Color(0xFF2E2413), radius = 16f, center = p0)
                    drawCircle(AthenaGold, radius = 9f, center = p0)

                    // Marcador Destino: Compradora (Tres Arroyos CP 7500)
                    drawCircle(Color(0xFF1E3A2F), radius = 18f, center = p3)
                    drawCircle(StatusSuccess, radius = 10f, center = p3)

                    // Vehículo Repartidor en Tiempo Real con onda radar
                    if (isAccepted) {
                        drawCircle(
                            color = RosaFluor.copy(alpha = pulseAlpha),
                            radius = pulseRadius,
                            center = currentPos,
                            style = Stroke(width = 3f)
                        )
                    }
                    drawCircle(color = ObsidianNight, radius = 16f, center = currentPos)
                    drawCircle(color = if (isAccepted) RosaFluor else AthenaGold, radius = 11f, center = currentPos)
                    drawCircle(color = Color.White, radius = 5f, center = currentPos)
                }

                // Chips superpuestos sobre el mapa
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xEE14121E))
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
                        Text("Atelier Athena (Tres Arroyos)", fontSize = 10.sp, color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xEE14121E))
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
                        Text(
                            text = destinationAddress.take(18) + "... (CP 7500)",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Datos del mensajero y barra de avance de entrega
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isAccepted) RosaFluor.copy(alpha = 0.2f) else AthenaGold.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = "Repartidor",
                        tint = if (isAccepted) RosaFluor else AthenaGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$courierName • Reparto Tres Arroyos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = if (!isAccepted) "Esperando confirmación para iniciar viaje" else "En ruta hacia tu domicilio (CP 7500)",
                        fontSize = 11.sp,
                        color = if (isAccepted) RosaFluor else AthenaGold
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0.0f, 1.0f) },
                color = if (isAccepted) RosaFluor else AthenaGold,
                trackColor = Color(0xFF28253B),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
