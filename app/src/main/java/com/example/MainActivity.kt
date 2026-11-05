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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.example.model.ProductCategory
import com.example.ui.theme.CrimsonSale
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
import com.example.ui.theme.AthenaPinkDark
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
        containerColor = Color.White,
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
            // Show bottom bar with simplified menu (Inicio, Rebajas, Adidas, Conjuntos, Deseos)
            if (currentScreen != AthenaScreen.PRODUCT_DETAIL) {
                val selectedCategory by viewModel.selectedCategory.collectAsState()
                val isCatalog = currentScreen == AthenaScreen.CATALOG

                NavigationBar(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.PORTADA,
                        onClick = { viewModel.navigateTo(AthenaScreen.PORTADA) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AthenaScreen.PORTADA) Icons.Default.Home else Icons.Outlined.Home,
                                contentDescription = "Inicio"
                            )
                        },
                        label = { Text("Inicio", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AthenaPinkDark,
                            indicatorColor = AthenaPink,
                            unselectedIconColor = Color(0xFF6E6A78),
                            unselectedTextColor = Color(0xFF6E6A78)
                        ),
                        modifier = Modifier.testTag("nav_item_portada")
                    )

                    val isRebajasSelected = isCatalog && selectedCategory == ProductCategory.REBAJAS
                    NavigationBarItem(
                        selected = isRebajasSelected,
                        onClick = { viewModel.openMenuSection(ProductCategory.REBAJAS) },
                        icon = {
                            Icon(
                                imageVector = if (isRebajasSelected) Icons.Default.LocalOffer else Icons.Outlined.LocalOffer,
                                contentDescription = "Rebajas"
                            )
                        },
                        label = { Text("Rebajas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CrimsonSale,
                            indicatorColor = CrimsonSale
                        ),
                        modifier = Modifier.testTag("nav_item_rebajas")
                    )

                    val isAdidasSelected = isCatalog && selectedCategory == ProductCategory.ADIDAS
                    NavigationBarItem(
                        selected = isAdidasSelected,
                        onClick = { viewModel.openMenuSection(ProductCategory.ADIDAS) },
                        icon = {
                            Icon(
                                imageVector = if (isAdidasSelected) Icons.Default.Checkroom else Icons.Outlined.Checkroom,
                                contentDescription = "Adidas"
                            )
                        },
                        label = { Text("Adidas", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AthenaPinkDark,
                            indicatorColor = AthenaPink,
                            unselectedIconColor = Color(0xFF6E6A78),
                            unselectedTextColor = Color(0xFF6E6A78)
                        ),
                        modifier = Modifier.testTag("nav_item_adidas")
                    )

                    val isConjuntosSelected = isCatalog && selectedCategory == ProductCategory.SETS
                    NavigationBarItem(
                        selected = isConjuntosSelected,
                        onClick = { viewModel.openMenuSection(ProductCategory.SETS) },
                        icon = {
                            Icon(
                                imageVector = if (isConjuntosSelected) Icons.Default.AutoAwesome else Icons.Outlined.AutoAwesome,
                                contentDescription = "Conjuntos"
                            )
                        },
                        label = { Text("Conjuntos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AthenaPinkDark,
                            indicatorColor = AthenaPink,
                            unselectedIconColor = Color(0xFF6E6A78),
                            unselectedTextColor = Color(0xFF6E6A78)
                        ),
                        modifier = Modifier.testTag("nav_item_conjuntos")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AthenaScreen.WISHLIST,
                        onClick = { viewModel.navigateTo(AthenaScreen.WISHLIST) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (wishlistCount > 0) {
                                        Badge(containerColor = AthenaPink, contentColor = Color.White) {
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
                        label = { Text("Deseos", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AthenaPinkDark,
                            indicatorColor = AthenaPink,
                            unselectedIconColor = Color(0xFF6E6A78),
                            unselectedTextColor = Color(0xFF6E6A78)
                        ),
                        modifier = Modifier.testTag("nav_item_wishlist")
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
