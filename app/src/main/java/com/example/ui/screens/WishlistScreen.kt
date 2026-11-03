package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.PriceEditDialog
import com.example.R
import com.example.data.local.WishlistItem
import com.example.model.Product
import com.example.model.ProductColor
import com.example.model.getDrawableResId
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RoseBronze
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.AthenaScreen
import com.example.viewmodel.AthenaViewModel
import java.util.Locale

@Composable
fun WishlistScreen(
    viewModel: AthenaViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    var itemForPriceEdit by remember { mutableStateOf<WishlistItem?>(null) }

    val totalValue = wishlistItems.sumOf { it.price }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("wishlist_screen")
    ) {
        // Header Summary Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wishlist_header_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AthenaPink.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = AthenaPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "LISTA DE DESEOS",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "Guardado local en Room para compras futuras",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (wishlistItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${wishlistItems.size} prendas",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "• Total estimado: $${String.format(Locale.US, "%.2f", totalValue)}",
                                    fontSize = 12.sp,
                                    color = AthenaGoldDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (wishlistItems.isNotEmpty()) {
                        Button(
                            onClick = { viewModel.clearWishlist() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = CrimsonSale
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("clear_wishlist_btn")
                        ) {
                            Text(
                                text = "Vaciar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Empty State
        if (wishlistItems.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .testTag("wishlist_empty_card")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(AthenaPink.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = AthenaPink,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Tu Lista de Deseos está vacía",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Guarda tus prendas y outfits favoritos de Athena Style tocando el corazón para comprarlos cuando desees.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.navigateTo(AthenaScreen.CATALOG) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AthenaGold,
                                contentColor = ObsidianNight
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("explore_catalog_from_wishlist_btn")
                        ) {
                            Text(
                                text = "Explorar Catálogo & Rebajas",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Wishlist items list
        items(wishlistItems, key = { it.productId }) { item ->
            val matchingProduct = allProducts.find { it.id == item.productId }
            WishlistItemRow(
                item = item,
                matchingProduct = matchingProduct,
                onItemClick = {
                    matchingProduct?.let { onProductClick(it) }
                },
                onAddToCart = {
                    if (matchingProduct != null) {
                        viewModel.addToCart(
                            product = matchingProduct,
                            size = matchingProduct.sizes.firstOrNull() ?: "M",
                            color = matchingProduct.colors.firstOrNull() ?: ProductColor("Dorado", 0xFFD4AF37)
                        )
                    }
                },
                onEditPrice = {
                    itemForPriceEdit = item
                },
                onRemove = {
                    viewModel.removeFromWishlist(item.productId)
                }
            )
        }

        // Bottom spacer for scrollability
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Diálogo para cambiar o asignar precio en Deseos
    itemForPriceEdit?.let { wishItem ->
        PriceEditDialog(
            itemName = wishItem.name,
            initialPrice = wishItem.price,
            onDismiss = { itemForPriceEdit = null },
            onSavePrice = { newPrice ->
                viewModel.updateProductPrice(wishItem.productId, newPrice)
            }
        )
    }
}

@Composable
private fun WishlistItemRow(
    item: WishlistItem,
    matchingProduct: Product?,
    onItemClick: () -> Unit,
    onAddToCart: () -> Unit,
    onEditPrice: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
            .testTag("wishlist_item_${item.productId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEDE8E0))
            ) {
                Image(
                    painter = painterResource(
                        id = matchingProduct?.getDrawableResId() ?: R.drawable.athena_app_icon
                    ),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.discountPercent != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CrimsonSale)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-${item.discountPercent}%",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Tejido: ${item.fabric} • SKU: ${item.sku}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", item.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AthenaGoldDark
                    )

                    if (item.originalPrice != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", item.originalPrice)}",
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onEditPrice,
                        modifier = Modifier
                            .size(26.dp)
                            .testTag("wishlist_edit_price_${item.productId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Precio",
                            tint = AthenaPink,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthenaGold,
                            contentColor = ObsidianNight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("wishlist_add_to_cart_${item.productId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Añadir a Bolsa",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("wishlist_remove_${item.productId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar de Wishlist",
                            tint = CrimsonSale,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
