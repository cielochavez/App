package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.theme.NeutralMedium
import com.example.ui.theme.WarmBorder
import androidx.compose.foundation.BorderStroke

@Composable
fun PushNotificationBanner(
    notification: AppNotification?,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (notification != null) {
            Card(
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.2.dp, WarmBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onClick() }
                    .testTag("push_notification_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFFFF7F9)
                                )
                            )
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon based on type
                        val iconVector = when (notification.type) {
                            NotificationType.ORDER_UPDATE -> Icons.Default.LocalShipping
                            NotificationType.INVENTORY_ALERT -> Icons.Default.Inventory2
                            NotificationType.PROMO_DROP -> Icons.Default.Tag
                            NotificationType.VIP_EXCLUSIVE -> Icons.Default.Star
                            else -> Icons.Default.Notifications
                        }

                        val iconBg = when (notification.type) {
                            NotificationType.ORDER_UPDATE -> AthenaPinkDark
                            NotificationType.INVENTORY_ALERT -> CrimsonSale
                            NotificationType.PROMO_DROP -> AthenaPinkDark
                            else -> AthenaPinkDark
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(iconBg.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = "Push Type",
                                tint = iconBg,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ATHENA PUSH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = AthenaPinkDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${notification.timeAgo}",
                                    fontSize = 10.sp,
                                    color = NeutralMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notification.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeutralDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = notification.message,
                                fontSize = 11.sp,
                                color = NeutralMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dismiss_push_banner_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar Notificación",
                                tint = NeutralMedium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
