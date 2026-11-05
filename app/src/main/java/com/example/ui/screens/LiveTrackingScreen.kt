package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.TrackingTimelineEvent
import com.example.ui.components.ShippingMapVisualizer
import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.AthenaPinkDark
import com.example.ui.theme.NeutralDark
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RosaFluor
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.WarmBorder
import com.example.viewmodel.AthenaViewModel
import java.util.Locale

@Composable
fun LiveTrackingScreen(
    viewModel: AthenaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val activeTrackedOrderId by viewModel.activeTrackedOrderId.collectAsState()
    val activeOrder by viewModel.activeTrackedOrder.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Order Selection Chips if more than 1 order exists
        if (orders.size > 1) {
            Text(
                text = "Tus Envíos Activos",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                orders.forEach { ord ->
                    val isSelected = ord.orderId == activeTrackedOrderId
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AthenaPink else Color(0xFFF2ECE8))
                            .border(
                                1.dp,
                                if (isSelected) AthenaPinkDark else WarmBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectActiveOrder(ord.orderId) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("select_order_${ord.orderId}")
                    ) {
                        Text(
                            text = "Orden #${ord.orderId}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else NeutralDark
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (activeOrder == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp)
            ) {
                Text("No hay órdenes activas para rastrear.")
            }
        } else {
            val order = activeOrder!!

            // Live GPS Canvas Visualizer
            ShippingMapVisualizer(
                progress = order.courierProgress,
                status = order.status,
                courierName = order.courierName,
                estimatedMinutes = order.estimatedMinutes,
                destinationAddress = order.deliveryAddress,
                remainingDistanceKm = order.remainingDistanceKm,
                latitude = order.latitude,
                longitude = order.longitude,
                isAccepted = order.isOrderAccepted,
                courierStatusText = order.courierStatusText,
                onAcceptOrder = { viewModel.acceptOrderAndStartDelivery(order.orderId) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Simulation Control Panel
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Simulación en Vivo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AthenaGoldDark
                            )
                            Text(
                                text = "Avanza la ruta para probar alertas push y mapa en tiempo real.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.advanceOrderTrackingStage(order.orderId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthenaPink,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("advance_tracking_stage_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Avanzar Etapa de Envío en Tiempo Real",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Courier Contact Card
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, WarmBorder),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AthenaPink.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto Mensajero",
                            tint = AthenaPinkDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vendedor: Athena_style",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RosaFluor
                        )
                        Text(
                            text = order.courierName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Celular: +54 9 2983 52-9883",
                            fontSize = 11.sp,
                            color = AthenaGoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:+5492983529883")
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                viewModel.simulateCustomPushNotification(0)
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("call_courier_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Llamar a Athena_style", tint = StatusSuccess, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline Stepper Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Progreso del Envío",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Rastreo certificado #${order.trackingNumber}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timeline items
                    order.timeline.forEachIndexed { index, event ->
                        TimelineRow(
                            event = event,
                            isLast = index == order.timeline.lastIndex
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Package Items Summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Prendas en el Paquete",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "${order.items.size} artículos",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    order.items.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = AthenaGoldDark, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text("Talla: ${item.selectedSize} • Cantidad: ${item.quantity}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", item.totalPrice)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AthenaGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Total Pagado con ${order.paymentMethodName.take(24)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format(Locale.US, "%.2f", order.total)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun TimelineRow(
    event: TrackingTimelineEvent,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        if (event.isCompleted) StatusSuccess
                        else if (event.isCurrent) AthenaGold
                        else Color.LightGray.copy(alpha = 0.4f)
                    )
            ) {
                if (event.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                } else if (event.isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AthenaPink)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(
                            if (event.isCompleted) StatusSuccess else Color.LightGray.copy(alpha = 0.5f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = event.title,
                    fontSize = 13.sp,
                    fontWeight = if (event.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (event.isCurrent) AthenaGoldDark else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = event.time,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }
    }
}
