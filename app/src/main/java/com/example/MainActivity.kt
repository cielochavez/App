package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AthenaTopBar
import com.example.ui.components.PushNotificationBanner
import com.example.ui.screens.CartAndCheckoutScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.InventoryManagementScreen
import com.example.ui.screens.LiveTrackingScreen
import com.example.ui.screens.NotificationCenterScreen
import com.example.ui.screens.PortadaScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.AthenaStyleTheme
import com.example.ui.theme.ObsidianNight
import com.example.viewmodel.AthenaScreen
import com.example.viewmodel.AthenaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AthenaStyleTheme {
                AthenaApp()
            }
        }
    }
}

@Composable
fun AthenaApp(
    viewModel: AthenaViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val activePushBanner by viewModel.activePushBanner.collectAsState()

    val unreadNotifsCount = notifications.count { !it.isRead }
    val cartCount = cart.sumOf { it.quantity }
    val wishlistCount = wishlistItems.size

    // Hardware and gesture BackHandler (returns to Portada if on Catalog, or previous screen)
    BackHandler(enabled = currentScreen != AthenaScreen.PORTADA) {
        viewModel.goBack()
    }

    val topBarTitle = when (currentScreen) {
        AthenaScreen.PORTADA -> "ATHENA STYLE"
        AthenaScreen.CATALOG -> "CATÁLOGO"
        AthenaScreen.WISHLIST -> "LISTA DE DESEOS"
        AthenaScreen.PRODUCT_DETAIL -> "DETALLE DE PRENDA"
        AthenaScreen.CART_CHECKOUT -> "BOLSA & PAGO"
        AthenaScreen.LIVE_TRACKING -> "SEGUIMIENTO EN VIVO"
        AthenaScreen.INVENTORY -> "ATELIER & STOCK"
        AthenaScreen.NOTIFICATIONS -> "NOTIFICACIONES"
    }

    val topBarSubtitle = when (currentScreen) {
        AthenaScreen.PORTADA -> "Tu Estilo, Tus Reglas • Indumentaria"
        AthenaScreen.CATALOG -> "Colección Exclusiva & Tendencias"
        AthenaScreen.WISHLIST -> "Guardado en Room • Compra Futura"
        AthenaScreen.LIVE_TRACKING -> "GPS & Despacho VIP"
        AthenaScreen.INVENTORY -> "Panel de Control Administrativo"
        else -> null
    }

    Scaffold(
        contentWindowInsets = if (currentScreen == AthenaScreen.PORTADA) WindowInsets(0, 0, 0, 0) else WindowInsets.safeDrawing,
        topBar = {
            if (currentScreen != AthenaScreen.PORTADA) {
                AthenaTopBar(
                    title = topBarTitle,
                    subtitle = topBarSubtitle,
                    showBackButton = true,
                    onBackClick = { viewModel.goBack() },
                    onLogoClick = { viewModel.navigateTo(AthenaScreen.PORTADA) },
                    unreadNotificationsCount = unreadNotifsCount,
                    wishlistItemCount = wishlistCount,
                    cartItemCount = cartCount,
                    onNotificationsClick = { viewModel.navigateTo(AthenaScreen.NOTIFICATIONS) },
                    onWishlistClick = { viewModel.navigateTo(AthenaScreen.WISHLIST) },
                    onCartClick = { viewModel.navigateTo(AthenaScreen.CART_CHECKOUT) }
                )
            }
        },
        bottomBar = {
            // Show bottom bar unless in full product detail or fullscreen portada
            if (currentScreen != AthenaScreen.PRODUCT_DETAIL && currentScreen != AthenaScreen.PORTADA) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.PORTADA,
                        onClick = { viewModel.navigateTo(AthenaScreen.PORTADA) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AthenaScreen.PORTADA) Icons.Default.Home else Icons.Outlined.Home,
                                contentDescription = "Portada"
                            )
                        },
                        label = { Text("Portada", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNight,
                            selectedTextColor = AthenaPink,
                            indicatorColor = AthenaPink
                        ),
                        modifier = Modifier.testTag("nav_item_portada")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.CATALOG,
                        onClick = { viewModel.navigateTo(AthenaScreen.CATALOG) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AthenaScreen.CATALOG) Icons.Default.Storefront else Icons.Outlined.Storefront,
                                contentDescription = "Catálogo"
                            )
                        },
                        label = { Text("Catálogo", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNight,
                            selectedTextColor = AthenaGoldDark,
                            indicatorColor = AthenaGold
                        ),
                        modifier = Modifier.testTag("nav_item_catalog")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.WISHLIST,
                        onClick = { viewModel.navigateTo(AthenaScreen.WISHLIST) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (wishlistCount > 0) {
                                        Badge(containerColor = AthenaPink, contentColor = ObsidianNight) {
                                            Text(wishlistCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentScreen == AthenaScreen.WISHLIST) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Deseos"
                                )
                            }
                        },
                        label = { Text("Deseos", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNight,
                            selectedTextColor = AthenaPink,
                            indicatorColor = AthenaPink
                        ),
                        modifier = Modifier.testTag("nav_item_wishlist")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.LIVE_TRACKING,
                        onClick = { viewModel.navigateTo(AthenaScreen.LIVE_TRACKING) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AthenaScreen.LIVE_TRACKING) Icons.Default.LocalShipping else Icons.Outlined.LocalShipping,
                                contentDescription = "Envíos"
                            )
                        },
                        label = { Text("Envíos", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNight,
                            selectedTextColor = AthenaGoldDark,
                            indicatorColor = AthenaGold
                        ),
                        modifier = Modifier.testTag("nav_item_tracking")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.CART_CHECKOUT,
                        onClick = { viewModel.navigateTo(AthenaScreen.CART_CHECKOUT) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(containerColor = AthenaPink, contentColor = ObsidianNight) {
                                            Text(cartCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentScreen == AthenaScreen.CART_CHECKOUT) Icons.Default.ShoppingBag else Icons.Outlined.ShoppingBag,
                                    contentDescription = "Bolsa"
                                )
                            }
                        },
                        label = { Text("Bolsa", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianNight,
                            selectedTextColor = AthenaGoldDark,
                            indicatorColor = AthenaGold
                        ),
                        modifier = Modifier.testTag("nav_item_cart")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content Area with fluid transitions
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    AthenaScreen.PORTADA -> {
                        PortadaScreen(viewModel = viewModel)
                    }

                    AthenaScreen.CATALOG -> {
                        CatalogScreen(
                            viewModel = viewModel,
                            onProductClick = { product ->
                                viewModel.openProductDetail(product)
                            }
                        )
                    }

                    AthenaScreen.WISHLIST -> {
                        WishlistScreen(
                            viewModel = viewModel,
                            onProductClick = { product ->
                                viewModel.openProductDetail(product)
                            }
                        )
                    }

                    AthenaScreen.PRODUCT_DETAIL -> {
                        selectedProduct?.let { prod ->
                            ProductDetailScreen(
                                product = prod,
                                viewModel = viewModel,
                                onBackClick = { viewModel.goBack() }
                            )
                        } ?: run {
                            viewModel.navigateTo(AthenaScreen.CATALOG)
                        }
                    }

                    AthenaScreen.CART_CHECKOUT -> {
                        CartAndCheckoutScreen(
                            viewModel = viewModel,
                            onNavigateToTracking = {
                                viewModel.navigateTo(AthenaScreen.LIVE_TRACKING)
                            }
                        )
                    }

                    AthenaScreen.LIVE_TRACKING -> {
                        LiveTrackingScreen(
                            viewModel = viewModel
                        )
                    }

                    AthenaScreen.INVENTORY -> {
                        InventoryManagementScreen(
                            viewModel = viewModel
                        )
                    }

                    AthenaScreen.NOTIFICATIONS -> {
                        NotificationCenterScreen(
                            viewModel = viewModel,
                            onNavigateToTracking = {
                                viewModel.navigateTo(AthenaScreen.LIVE_TRACKING)
                            }
                        )
                    }
                }
            }

            // Real-Time In-App Push Notification Floating Banner
            PushNotificationBanner(
                notification = activePushBanner,
                onDismiss = { viewModel.dismissPushBanner() },
                onClick = {
                    val notif = activePushBanner
                    viewModel.dismissPushBanner()
                    if (notif != null) {
                        viewModel.markNotificationAsRead(notif.id)
                        if (notif.linkedOrderId != null) {
                            viewModel.selectActiveOrder(notif.linkedOrderId)
                            viewModel.navigateTo(AthenaScreen.LIVE_TRACKING)
                        } else {
                            viewModel.navigateTo(AthenaScreen.NOTIFICATIONS)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            )
        }
    }
}
