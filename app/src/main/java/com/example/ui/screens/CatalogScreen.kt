package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.RebajasImageSlot
import com.example.ui.components.ProductCard
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.ObsidianNight
import com.example.viewmodel.AthenaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: AthenaViewModel,
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedSizeFilter by viewModel.selectedSizeFilter.collectAsState()
    val isOnlySaleFilter by viewModel.isOnlySaleFilter.collectAsState()
    val isOnlyInStockFilter by viewModel.isOnlyInStockFilter.collectAsState()
    val wishlistProductIds by viewModel.wishlistProductIds.collectAsState()
    val rebajasSlots by viewModel.rebajasImageSlots.collectAsState()

    // State for image slot assignment via photo picker
    var activeTargetSlotId by remember { mutableStateOf<String?>(null) }
    var showUrlDialog by remember { mutableStateOf(false) }
    var customUrlInput by remember { mutableStateOf("") }
    var customTitleInput by remember { mutableStateOf("") }

    // Android zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val targetId = activeTargetSlotId
            if (targetId != null) {
                viewModel.addRebajasImageToSlot(targetId, uri.toString())
            } else {
                viewModel.addRebajasNewImage("Look Exclusivo", uri.toString())
            }
        }
        activeTargetSlotId = null
    }

    // LazyVerticalGrid that is fully and smoothly scrollable downwards
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_grid")
    ) {
        // Hero Editorial Banner / Foto de Portada en Rebajas
        item(span = { GridItemSpan(2) }) {
            val isRebajasActive = isOnlySaleFilter || selectedCategory == ProductCategory.REBAJAS

            if (isRebajasActive) {
                // FOTO DE PORTADA EN REBAJAS (BANNER OFICIAL BOUTIQUE ATHENA STYLE)
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .testTag("portada_rebajas_banner_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0E16))
                    ) {
                        // Hero Portada Image (Glowing Neon BOUTIQUE ATHENA_STYLE_)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(Color(0xFF0B0A10))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner_portada_rebajas),
                                contentDescription = "Foto de Portada en Rebajas - Boutique Athena Style",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            // Status Tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CrimsonSale)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🔥 PORTADA EN REBAJAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = Color.White
                                )
                            }
                        }

                        // Bottom Actions & Details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "BOUTIQUE • REBAJAS DE TEMPORADA",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                    color = AthenaPink
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Prendas oficiales reales con hasta 40% OFF",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.isOnlySaleFilter.value = false
                                    viewModel.selectedCategory.value = ProductCategory.ALL
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.15f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Ver Todo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .testTag("editorial_hero_banner")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.athena_cover),
                            contentDescription = "Athena Style Portada Oficial",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradient Scrim
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.25f),
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AthenaGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NUEVA TEMPORADA 2026",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = AthenaGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Colección Olimpo",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color.White
                            )
                            Text(
                                text = "Siluetas esculturales en seda y lino fino.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.isOnlySaleFilter.value = true
                                    viewModel.selectedCategory.value = ProductCategory.REBAJAS
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CrimsonSale,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("explore_olympus_btn")
                            ) {
                                Text(
                                    text = "🔥 Ver Rebajas Exclusivas",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // ESPACIOS PARA AGREGAR IMÁGENES EN REBAJAS (Mandatory User Feature)
        item(span = { GridItemSpan(2) }) {
            val isRebajasActive = isOnlySaleFilter || selectedCategory == ProductCategory.REBAJAS

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isRebajasActive) Color(0xFF13121D) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("rebajas_image_spaces_section")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Header with title and Add Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = AthenaPink,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ESPACIOS PARA AGREGAR IMÁGENES",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = AthenaPink
                                )
                            }
                            Text(
                                text = "Sube looks, fotos de prendas y promociones para Rebajas",
                                fontSize = 11.sp,
                                color = if (isRebajasActive) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Quick Add Space Button
                            IconButton(
                                onClick = {
                                    viewModel.addEmptyRebajasSlot()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AthenaPink.copy(alpha = 0.15f))
                                    .testTag("add_empty_slot_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Añadir Espacio",
                                    tint = AthenaPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Carousel of Image Slots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        rebajasSlots.forEach { slot ->
                            RebajasImageSlotCard(
                                slot = slot,
                                onUploadClick = {
                                    activeTargetSlotId = slot.id
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onRemoveClick = {
                                    viewModel.removeRebajasSlot(slot.id)
                                }
                            )
                        }

                        // Add new slot button card at end of carousel
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                            modifier = Modifier
                                .width(150.dp)
                                .height(160.dp)
                                .border(1.5.dp, AthenaPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable {
                                    activeTargetSlotId = null
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("slot_add_new_picker_card")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(AthenaPink.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Subir foto",
                                        tint = AthenaPink,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "+ Subir Imagen",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AthenaPink,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "De tu Galería",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        item(span = { GridItemSpan(2) }) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = {
                    Text(
                        text = "Buscar vestidos, sedas, blazer, SKU...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AthenaGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_bar")
            )
        }

        // Category Filter Chips Carousel
        item(span = { GridItemSpan(2) }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp)
            ) {
                ProductCategory.values().forEach { category ->
                    val isSelected = selectedCategory == category
                    val isRebajas = category == ProductCategory.REBAJAS
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedCategory.value = category
                            if (isRebajas) {
                                viewModel.isOnlySaleFilter.value = true
                            }
                        },
                        label = {
                            Text(
                                text = category.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected || isRebajas) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isRebajas) CrimsonSale else ObsidianNight,
                            selectedLabelColor = Color.White,
                            containerColor = if (isRebajas) CrimsonSale.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isRebajas) CrimsonSale else if (isSelected) AthenaGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("category_chip_${category.name}")
                    )
                }
            }
        }

        // Secondary Filters: Size, Sale, Stock
        item(span = { GridItemSpan(2) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                // Size filter chips
                listOf("XS", "S", "M", "L", "XL").forEach { size ->
                    val isSizeSelected = selectedSizeFilter == size
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSizeSelected) AthenaGold else MaterialTheme.colorScheme.surface
                            )
                            .clickable {
                                viewModel.selectedSizeFilter.value = if (isSizeSelected) null else size
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("size_filter_$size")
                    ) {
                        Text(
                            text = "Talla $size",
                            fontSize = 11.sp,
                            fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSizeSelected) ObsidianNight else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // In Stock toggle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isOnlyInStockFilter) ObsidianNight else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            viewModel.isOnlyInStockFilter.value = !isOnlyInStockFilter
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("in_stock_toggle")
                ) {
                    Text(
                        text = "En Stock",
                        fontSize = 11.sp,
                        fontWeight = if (isOnlyInStockFilter) FontWeight.Bold else FontWeight.Medium,
                        color = if (isOnlyInStockFilter) AthenaGold else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Sale toggle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isOnlySaleFilter) CrimsonSale else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            viewModel.isOnlySaleFilter.value = !isOnlySaleFilter
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("sale_toggle")
                ) {
                    Text(
                        text = "% En Descuento",
                        fontSize = 11.sp,
                        fontWeight = if (isOnlySaleFilter) FontWeight.Bold else FontWeight.Medium,
                        color = if (isOnlySaleFilter) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Header Results Count
        item(span = { GridItemSpan(2) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "${filteredProducts.size} prendas exclusivas",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (searchQuery.isNotBlank() || selectedCategory != ProductCategory.ALL || selectedSizeFilter != null || isOnlySaleFilter || isOnlyInStockFilter) {
                    Text(
                        text = "Restablecer filtros",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AthenaGoldDark,
                        modifier = Modifier
                            .clickable {
                                viewModel.searchQuery.value = ""
                                viewModel.selectedCategory.value = ProductCategory.ALL
                                viewModel.selectedSizeFilter.value = null
                                viewModel.isOnlySaleFilter.value = false
                                viewModel.isOnlyInStockFilter.value = false
                            }
                            .testTag("reset_filters_btn")
                    )
                }
            }
        }

        // Empty State
        if (filteredProducts.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No se encontraron prendas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Intenta ajustar tus filtros de búsqueda o categoría.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Products Grid with Room Wishlist connection
        items(filteredProducts, key = { it.id }) { product ->
            ProductCard(
                product = product,
                isFavorite = wishlistProductIds.contains(product.id),
                onCardClick = { onProductClick(product) },
                onFavoriteClick = { viewModel.toggleWishlist(product) },
                onQuickAddClick = {
                    viewModel.addToCart(
                        product = product,
                        size = product.sizes.firstOrNull() ?: "M",
                        color = product.colors.firstOrNull() ?: com.example.model.ProductColor("Dorado", 0xFFD4AF37)
                    )
                }
            )
        }
    }
}

@Composable
private fun RebajasImageSlotCard(
    slot: RebajasImageSlot,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(220.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if (slot.isPlaceholder) {
                    onUploadClick()
                }
            }
            .testTag("slot_card_${slot.id}")
    ) {
        if (slot.isPlaceholder) {
            // Empty / Available slot placeholder
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF14131D))
                    .border(
                        1.5.dp,
                        AthenaPink.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AthenaPink.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar Imagen",
                            tint = AthenaPink,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = slot.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = slot.subtitle ?: "Toca para agregar imagen",
                        fontSize = 10.sp,
                        color = AthenaPink,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Filled slot with image preview
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFF0F0E16))
                ) {
                    if (slot.imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = slot.imageUri,
                                error = painterResource(id = R.drawable.athena_cover),
                                placeholder = painterResource(id = R.drawable.athena_app_icon)
                            ),
                            contentDescription = slot.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (slot.drawableResId != null) {
                        Image(
                            painter = painterResource(id = slot.drawableResId),
                            contentDescription = slot.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Delete / Replace Button
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("remove_slot_${slot.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar espacio",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CrimsonSale)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "REBAJA",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Title info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = slot.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        slot.subtitle?.let {
                            Text(
                                text = it,
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
