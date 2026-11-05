package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.ObsidianNight

import androidx.compose.foundation.border
import com.example.ui.theme.WarmBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthenaTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onLogoClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    wishlistItemCount: Int = 0,
    cartItemCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            } else {
                // Monogram Icon (tappable to open Portada)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF7F9))
                        .border(1.dp, WarmBorder, CircleShape)
                        .clickable { onLogoClick() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.athena_app_icon),
                        contentDescription = "Athena Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        },
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(AthenaGold)
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // Wishlist (Room Database) Icon
            IconButton(
                onClick = onWishlistClick,
                modifier = Modifier.testTag("top_bar_wishlist_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (wishlistItemCount > 0) {
                            Badge(
                                containerColor = AthenaPink,
                                contentColor = ObsidianNight
                            ) {
                                Text(
                                    text = if (wishlistItemCount > 9) "9+" else wishlistItemCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Lista de Deseos",
                        tint = if (wishlistItemCount > 0) AthenaPink else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Notifications Icon
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("top_bar_notif_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationsCount > 0) {
                            Badge(
                                containerColor = CrimsonSale,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones"
                    )
                }
            }

            // Cart Icon
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.testTag("top_bar_cart_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = AthenaGold,
                                contentColor = ObsidianNight
                            ) {
                                Text(
                                    text = if (cartItemCount > 9) "9+" else cartItemCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Carrito de Compras"
                    )
                }
            }
        }
    )
}
