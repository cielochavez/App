package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.AthenaPinkDark
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.NeutralDark
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.WarmBorder
import com.example.viewmodel.AthenaScreen
import com.example.viewmodel.AthenaViewModel

@Composable
fun NotificationCenterScreen(
    viewModel: AthenaViewModel,
    onNavigateToTracking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val activeFilter by viewModel.notificationFilter.collectAsState()

    var prefShipping by remember { mutableStateOf(true) }
    var prefPromos by remember { mutableStateOf(true) }
    var prefStock by remember { mutableStateOf(true) }

    val filteredNotifications = notifications.filter { notif ->
        when (activeFilter) {
            NotificationType.ALL -> true
            else -> notif.type == activeFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Title Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Centro de Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Alertas en tiempo real, seguimiento y lanzamientos.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (notifications.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.markAllNotificationsAsRead() },
                        modifier = Modifier.testTag("mark_all_read_btn")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = AthenaGoldDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Leídas", fontSize = 12.sp, color = AthenaGoldDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Live Push Simulator Interactive Test Panel
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, WarmBorder),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth().testTag("push_simulator_panel")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AthenaPinkDark)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Disparador de Notificaciones Push",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Haz clic en cualquier botón para disparar una notificación push instantánea con banner flotante animado:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.simulateCustomPushNotification(0) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2ECE8), contentColor = NeutralDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("sim_push_shipping_btn")
                        ) {
                            Text("En Reparto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.simulateCustomPushNotification(1) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2ECE8), contentColor = NeutralDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("sim_push_promo_btn")
                        ) {
                            Text("Oferta 25%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.simulateCustomPushNotification(2) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2ECE8), contentColor = NeutralDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("sim_push_stock_btn")
                        ) {
                            Text("Stock Bajo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.simulateCustomPushNotification(3) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2ECE8), contentColor = NeutralDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("sim_push_vip_btn")
                        ) {
                            Text("Desfile VIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Notification Filter Chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                NotificationType.values().forEach { type ->
                    val isSelected = activeFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.notificationFilter.value = type },
                        label = { Text(type.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AthenaPink,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) AthenaPink else WarmBorder
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Notifications List
        if (filteredNotifications.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No hay notificaciones en esta categoría.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(filteredNotifications, key = { it.id }) { notif ->
                NotificationItemCard(
                    notification = notif,
                    onClick = {
                        viewModel.markNotificationAsRead(notif.id)
                        if (notif.linkedOrderId != null) {
                            viewModel.selectActiveOrder(notif.linkedOrderId)
                            onNavigateToTracking(notif.linkedOrderId)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Preferences Settings Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Preferencias de Notificaciones Push",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    PreferenceRow(
                        title = "Seguimiento y Estado de Envíos",
                        subtitle = "Alertas cuando tu paquete sale a reparto o cambia de etapa",
                        checked = prefShipping,
                        onCheckedChange = { prefShipping = it }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                    PreferenceRow(
                        title = "Promociones y Descuentos Exclusivos",
                        subtitle = "Códigos de descuento anticipados para miembros VIP",
                        checked = prefPromos,
                        onCheckedChange = { prefPromos = it }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                    PreferenceRow(
                        title = "Alertas de Stock y Colecciones",
                        subtitle = "Avisar cuando prendas agotadas vuelvan a tener existencias",
                        checked = prefStock,
                        onCheckedChange = { prefStock = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
        ),
        border = if (!notification.isRead) androidx.compose.foundation.BorderStroke(1.dp, AthenaGold.copy(alpha = 0.5f)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_card_${notification.id}")
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(14.dp)
        ) {
            val iconVector = when (notification.type) {
                NotificationType.ORDER_UPDATE -> Icons.Default.LocalShipping
                NotificationType.INVENTORY_ALERT -> Icons.Default.Inventory2
                NotificationType.PROMO_DROP -> Icons.Default.Tag
                NotificationType.VIP_EXCLUSIVE -> Icons.Default.Star
                else -> Icons.Default.Notifications
            }

            val iconColor = when (notification.type) {
                NotificationType.ORDER_UPDATE -> AthenaGoldDark
                NotificationType.INVENTORY_ALERT -> CrimsonSale
                NotificationType.PROMO_DROP -> AthenaGold
                else -> AthenaGold
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f))
            ) {
                Icon(imageVector = iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AthenaGold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.timeAgo,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    if (notification.linkedOrderId != null) {
                        Text(
                            text = "Rastrear Envío →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AthenaGoldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AthenaPink
            )
        )
    }
}
