package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import com.example.ui.components.PriceEditDialog
import com.example.model.getGalleryDrawables
import com.example.ui.theme.AthenaPink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Product
import com.example.model.getDrawableResId
import com.example.ui.theme.AlabasterCream
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.GoldenSun
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RoseBronze
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.AthenaViewModel
import java.util.Locale

@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: AthenaViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: com.example.model.ProductColor("Dorado", 0xFFD4AF37)) }
    var quantity by remember { mutableIntStateOf(1) }
    var showSizeGuide by remember { mutableStateOf(false) }
    var showPriceEditDialog by remember { mutableStateOf(false) }

    val gallery = remember(product.id) { product.getGalleryDrawables() }
    var currentImageIndex by remember(product.id) { mutableIntStateOf(0) }

    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = favorites.contains(product.id)

    Scaffold(
        bottomBar = {
            // Sticky Add-to-Bag Bottom Bar
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Column {
                        Text(
                            text = "Total a pagar",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", product.price * quantity)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AthenaGoldDark
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.addToCart(
                                product = product,
                                size = selectedSize,
                                color = selectedColor,
                                quantity = quantity
                            )
                        },
                        enabled = !product.isOutOfStock,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthenaGold,
                            contentColor = ObsidianNight
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("add_to_cart_detail_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (product.isOutOfStock) "Agotado" else "Añadir a la Bolsa",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Header with Action Buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(Color(0xFFEDE8E0))
            ) {
                // High-resolution Garment Image with sliding gallery
                Image(
                    painter = painterResource(id = gallery.getOrElse(currentImageIndex) { product.getDrawableResId() }),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Flechas para correr las fotos (Izquierda y Derecha)
                if (gallery.size > 1) {
                    IconButton(
                        onClick = {
                            currentImageIndex = if (currentImageIndex > 0) currentImageIndex - 1 else gallery.size - 1
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .testTag("detail_arrow_prev")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Foto Anterior",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            currentImageIndex = (currentImageIndex + 1) % gallery.size
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .testTag("detail_arrow_next")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Foto Siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Contador de fotos en la prenda
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Foto ${currentImageIndex + 1} de ${gallery.size}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Top navigation bar overlay
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("product_detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleFavorite(product.id) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("product_detail_fav_btn")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) RoseBronze else Color.White
                        )
                    }
                }

                // Badges bottom-left
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    if (product.discountPercent != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CrimsonSale)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "-${product.discountPercent}% OFF",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianNight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.category.displayName.uppercase(),
                            color = AthenaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Product Details Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title and SKU
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "SKU: ${product.sku}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldenSun,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", product.rating),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${product.reviewsCount})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price display with option to edit/assign price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", product.price)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AthenaGoldDark
                        )
                        if (product.originalPrice != null) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", product.originalPrice)}",
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Botón para Asignar / Cambiar Precio
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AthenaPink.copy(alpha = 0.15f))
                            .clickable { showPriceEditDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("detail_edit_price_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Precio",
                            tint = AthenaPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cambiar Precio",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AthenaPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stock Alert
                if (product.isOutOfStock) {
                    Text(
                        text = "Actualmente Agotado en Almacén",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonSale
                    )
                } else if (product.isLowStock) {
                    Text(
                        text = "¡Alerta de Inventario: Solo quedan ${product.stock} unidades en Atelier!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                } else {
                    Text(
                        text = "✓ Stock Disponible (${product.stock} unidades listas para despacho inmediato)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StatusSuccess
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // Size Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Seleccionar Talla",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { showSizeGuide = true },
                        modifier = Modifier.testTag("size_guide_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Rule,
                            contentDescription = null,
                            tint = AthenaGoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Guía de Medidas",
                            fontSize = 12.sp,
                            color = AthenaGoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    product.sizes.forEach { size ->
                        val isSelected = selectedSize == size
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ObsidianNight else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.5.dp,
                                    if (isSelected) AthenaGold else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedSize = size }
                                .testTag("select_size_$size")
                        ) {
                            Text(
                                text = size,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AthenaGold else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Color Swatches
                Text(
                    text = "Color Seleccionado: ${selectedColor.name}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    product.colors.forEach { color ->
                        val isSelected = selectedColor.name == color.name
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(color.hexValue))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) AthenaGoldDark else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                                .testTag("select_color_${color.name}")
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (color.hexValue == 0xFFFFFFF0L || color.hexValue == 0xFFF5EBE0L) Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quantity selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cantidad de prendas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
                        }

                        Text(
                            text = quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        IconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Descripción del Atelier",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fabric & Composition Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DryCleaning,
                                contentDescription = null,
                                tint = AthenaGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Composición & Cuidados",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Tejido: ${product.fabric}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Cuidados: ${product.careInstructions}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = StatusSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Garantía de Autenticidad Athena 100% de Lujo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = StatusSuccess
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(110.dp))
        }
    }

    // Size Guide Modal Dialog
    if (showSizeGuide) {
        AlertDialog(
            onDismissRequest = { showSizeGuide = false },
            title = {
                Text(
                    text = "Guía de Medidas Athena Style",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Medidas en centímetros (cm):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "XS" to "Busto: 82-86 cm | Cintura: 62-66 cm | Cadera: 88-92 cm",
                        "S" to "Busto: 86-90 cm | Cintura: 66-70 cm | Cadera: 92-96 cm",
                        "M" to "Busto: 90-95 cm | Cintura: 70-75 cm | Cadera: 96-101 cm",
                        "L" to "Busto: 95-101 cm | Cintura: 75-81 cm | Cadera: 101-107 cm",
                        "XL" to "Busto: 101-107 cm | Cintura: 81-87 cm | Cadera: 107-113 cm"
                    ).forEach { (size, measure) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AthenaGold)
                            ) {
                                Text(
                                    text = size,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianNight
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = measure,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSizeGuide = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    // Diálogo para Cambiar o Asignar Precio en Detalle
    if (showPriceEditDialog) {
        PriceEditDialog(
            itemName = product.name,
            initialPrice = product.price,
            onDismiss = { showPriceEditDialog = false },
            onSavePrice = { newPrice ->
                viewModel.updateProductPrice(product.id, newPrice)
            }
        )
    }
}
