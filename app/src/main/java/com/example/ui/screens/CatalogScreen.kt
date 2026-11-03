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
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.text.style.TextDecoration
import com.example.model.ProductColor
import com.example.ui.components.PriceEditDialog
import com.example.ui.theme.AthenaGoldDark
import java.util.Locale
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.RebajasImageSlot
import com.example.ui.components.ProductCard
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
    val isOnlySaleFilter by viewModel.isOnlySaleFilter.collectAsState()
    val wishlistProductIds by viewModel.wishlistProductIds.collectAsState()
    val rebajasSlots by viewModel.rebajasImageSlots.collectAsState()
    val adidasSlots by viewModel.adidasImageSlots.collectAsState()
    val conjuntosSlots by viewModel.conjuntosImageSlots.collectAsState()

    var activeTargetSlotId by remember { mutableStateOf<String?>(null) }
    var activeSlotType by remember { mutableStateOf("rebajas") } // "rebajas", "adidas", or "conjuntos"

    // Dialog states for adding/editing prices
    var slotForPriceEdit by remember { mutableStateOf<RebajasImageSlot?>(null) }
    var productForPriceEdit by remember { mutableStateOf<Product?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val rebajasScrollState = rememberScrollState()
    val adidasScrollState = rememberScrollState()
    val conjuntosScrollState = rememberScrollState()

    // Android zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val targetId = activeTargetSlotId
            when (activeSlotType) {
                "adidas" -> {
                    if (targetId != null) {
                        viewModel.addAdidasImageToSlot(targetId, uri.toString())
                    } else {
                        viewModel.addAdidasNewImage("Prenda Adidas", uri.toString())
                    }
                }
                "conjuntos" -> {
                    if (targetId != null) {
                        viewModel.addConjuntosImageToSlot(targetId, uri.toString())
                    } else {
                        viewModel.addConjuntosNewImage("Set Malibú / Conjunto", uri.toString())
                    }
                }
                else -> {
                    if (targetId != null) {
                        viewModel.addRebajasImageToSlot(targetId, uri.toString())
                    } else {
                        viewModel.addRebajasNewImage("Prenda Rebajas", uri.toString())
                    }
                }
            }
        }
        activeTargetSlotId = null
    }

    // Smooth downwards scrolling column with large, prominent photo spaces and simple UI
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_grid")
    ) {
        // 1. MENÚ PRINCIPAL: REBAJAS • ADIDAS • CONJUNTOS (SIMPLE Y ELEGANTE)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14121E)),
                border = BorderStroke(1.2.dp, AthenaPink.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_main_menu_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "MENÚ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            color = AthenaPink
                        )

                        // Pequeño acceso para ver todo
                        Text(
                            text = if (selectedCategory == ProductCategory.ALL) "• Viendo Todo •" else "Ver Todo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedCategory == ProductCategory.ALL) AthenaPink else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectedCategory.value = ProductCategory.ALL
                                    viewModel.isOnlySaleFilter.value = false
                                }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. BOTÓN REBAJAS
                        val isRebajas = isOnlySaleFilter || selectedCategory == ProductCategory.REBAJAS
                        Button(
                            onClick = {
                                viewModel.openMenuSection(ProductCategory.REBAJAS)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRebajas) CrimsonSale else Color(0xFF222030),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.2.dp, if (isRebajas) CrimsonSale else Color.Transparent),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("menu_tab_rebajas")
                        ) {
                            Text(
                                text = "🔥 Rebajas",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 2. BOTÓN ADIDAS
                        val isAdidas = selectedCategory == ProductCategory.ADIDAS
                        Button(
                            onClick = {
                                viewModel.openMenuSection(ProductCategory.ADIDAS)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdidas) AthenaPink else Color(0xFF222030),
                                contentColor = if (isAdidas) Color.Black else AthenaPink
                            ),
                            border = BorderStroke(1.2.dp, if (isAdidas) AthenaPink else Color.Transparent),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("menu_tab_adidas")
                        ) {
                            Text(
                                text = "👟 Adidas",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // 3. BOTÓN CONJUNTOS
                        val isConjuntos = selectedCategory == ProductCategory.SETS
                        Button(
                            onClick = {
                                viewModel.openMenuSection(ProductCategory.SETS)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConjuntos) Color.White else Color(0xFF222030),
                                contentColor = if (isConjuntos) Color(0xFF14121E) else Color.White
                            ),
                            border = BorderStroke(1.2.dp, if (isConjuntos) Color.White else Color.Transparent),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("menu_tab_conjuntos")
                        ) {
                            Text(
                                text = "✨ Conjuntos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. CONTENIDO SEGÚN LA SECCIÓN DEL MENÚ SELECCIONADA
        val isRebajasActive = isOnlySaleFilter || selectedCategory == ProductCategory.REBAJAS
        val isAdidasActive = selectedCategory == ProductCategory.ADIDAS
        val isConjuntosActive = selectedCategory == ProductCategory.SETS

        // SECCIÓN REBAJAS: PORTADA + ESPACIOS PARA FOTOS REBAJAS
        if (isRebajasActive) {
            // Portada Rebajas
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("portada_rebajas_banner_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFF0F0E16))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_portada_rebajas),
                            contentDescription = "Portada Rebajas",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CrimsonSale)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔥 REBAJAS ATELIER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Espacios para agregar fotos de Rebajas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rebajas_image_spaces_section")
                ) {
                    // Header con flechas para correr las fotos
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "ESPACIOS PARA AGREGAR FOTOS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = AthenaPink
                            )
                            Text(
                                text = "Sube fotos de prendas de rebajas desde tu galería.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Controles de flechas para correr las fotos
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        rebajasScrollState.animateScrollBy(-600f)
                                    }
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF222030))
                                    .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                    .testTag("rebajas_header_arrow_prev")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Correr fotos izquierda",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        rebajasScrollState.animateScrollBy(600f)
                                    }
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF222030))
                                    .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                    .testTag("rebajas_header_arrow_next")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Correr fotos derecha",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Carousel con flechas laterales superpuestas
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rebajasScrollState)
                        ) {
                            // Tarjeta + Subir Foto de Prenda
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(290.dp)
                                    .border(2.dp, AthenaPink, RoundedCornerShape(20.dp))
                                    .clickable {
                                        activeSlotType = "rebajas"
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
                                        .padding(18.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(AthenaPink.copy(alpha = 0.2f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = "Subir foto",
                                            tint = AthenaPink,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "+ Subir Foto de Prenda",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AthenaPink,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Elige de tu galería",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.75f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Espacios para fotos de Rebajas
                            rebajasSlots.forEach { slot ->
                                LargePhotoSlotCard(
                                    slot = slot,
                                    onUploadClick = {
                                        activeSlotType = "rebajas"
                                        activeTargetSlotId = slot.id
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onRemoveClick = {
                                        viewModel.removeRebajasSlot(slot.id)
                                    },
                                    onEditPriceClick = {
                                        slotForPriceEdit = slot
                                    },
                                    onAddToCartClick = {
                                        viewModel.addSlotToCart(slot)
                                    }
                                )
                            }
                        }

                        // Flechas flotantes en los bordes para correr fotos fácilmente
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    rebajasScrollState.animateScrollBy(-600f)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 2.dp)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF14121E).copy(alpha = 0.88f))
                                .border(1.5.dp, AthenaPink, CircleShape)
                                .testTag("rebajas_side_arrow_prev")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Correr foto anterior",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    rebajasScrollState.animateScrollBy(600f)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 2.dp)
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF14121E).copy(alpha = 0.88f))
                                .border(1.5.dp, AthenaPink, CircleShape)
                                .testTag("rebajas_side_arrow_next")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Correr foto siguiente",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // SECCIÓN ADIDAS: ENCABEZADO + ESPACIOS PARA FOTOS ADIDAS
        if (isAdidasActive) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14131D)),
                    border = BorderStroke(1.5.dp, AthenaPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adidas_collection_header_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "👟 COLECCIÓN ADIDAS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Campera de Jean 3 Tiras & Conjunto 3 Piezas",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AthenaPink)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ORIGINALS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Header con flechas para correr fotos Adidas
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "ESPACIOS PARA IMÁGENES ADIDAS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = AthenaPink
                                )
                                Text(
                                    text = "Toca cualquier espacio para subir fotos de prendas Adidas.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            adidasScrollState.animateScrollBy(-600f)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF222030))
                                        .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                        .testTag("adidas_header_arrow_prev")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Correr fotos izquierda",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            adidasScrollState.animateScrollBy(600f)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF222030))
                                        .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                        .testTag("adidas_header_arrow_next")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Correr fotos derecha",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(adidasScrollState)
                            ) {
                                // Tarjeta + Subir Foto Adidas
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                                    modifier = Modifier
                                        .width(260.dp)
                                        .height(290.dp)
                                        .border(2.dp, AthenaPink, RoundedCornerShape(20.dp))
                                        .clickable {
                                            activeSlotType = "adidas"
                                            activeTargetSlotId = null
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .testTag("slot_add_adidas_photo_card")
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(18.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(AthenaPink.copy(alpha = 0.2f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = "Subir foto Adidas",
                                                tint = AthenaPink,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "+ Subir Foto Adidas",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AthenaPink,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Elige de tu galería",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.75f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Espacios con fotos cargadas (Campera jean, Conjunto 3 piezas, etc.)
                                adidasSlots.forEach { slot ->
                                    LargePhotoSlotCard(
                                        slot = slot,
                                        onUploadClick = {
                                            activeSlotType = "adidas"
                                            activeTargetSlotId = slot.id
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        onRemoveClick = {
                                            viewModel.removeAdidasSlot(slot.id)
                                        },
                                        onEditPriceClick = {
                                            slotForPriceEdit = slot
                                        },
                                        onAddToCartClick = {
                                            viewModel.addSlotToCart(slot)
                                        }
                                    )
                                }
                            }

                            // Flechas laterales Adidas
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        adidasScrollState.animateScrollBy(-600f)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 2.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF14121E).copy(alpha = 0.88f))
                                    .border(1.5.dp, AthenaPink, CircleShape)
                                    .testTag("adidas_side_arrow_prev")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Correr foto anterior",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        adidasScrollState.animateScrollBy(600f)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 2.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF14121E).copy(alpha = 0.88f))
                                    .border(1.5.dp, AthenaPink, CircleShape)
                                    .testTag("adidas_side_arrow_next")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Correr foto siguiente",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // SECCIÓN CONJUNTOS: ENCABEZADO SET MALIBÚ & ESPACIOS PARA FOTOS
        if (isConjuntosActive) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14121E)),
                    border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("conjuntos_header_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "✨ COLECCIÓN SET MALIBÚ & CONJUNTOS",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Top halter con cierre frontal & pantalón flare oxford en 3 tonos",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }

                            // Botón de Adidas dentro de Conjuntos
                            Button(
                                onClick = {
                                    viewModel.openMenuSection(ProductCategory.ADIDAS)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AthenaPink,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("btn_adidas_inside_conjuntos")
                            ) {
                                Text(
                                    text = "👟 Ver Adidas",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ESPACIOS PARA AGREGAR IMÁGENES SET MALIBÚ & CONJUNTOS
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ESPACIOS PARA IMÁGENES SET MALIBÚ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = AthenaPink
                                )
                                Text(
                                    text = "Fotos reales del Set Malibú en celeste, rosa y marrón chocolate.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Flechas en encabezado para correr fotos
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            conjuntosScrollState.animateScrollBy(-600f)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF222030))
                                        .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                        .testTag("conjuntos_header_arrow_prev")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Correr fotos izquierda",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            conjuntosScrollState.animateScrollBy(600f)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF222030))
                                        .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                        .testTag("conjuntos_header_arrow_next")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Correr fotos derecha",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(conjuntosScrollState)
                            ) {
                                // Tarjeta + Subir Foto Conjunto
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                                    modifier = Modifier
                                        .width(260.dp)
                                        .height(290.dp)
                                        .border(2.dp, AthenaPink, RoundedCornerShape(20.dp))
                                        .clickable {
                                            activeSlotType = "conjuntos"
                                            activeTargetSlotId = null
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .testTag("slot_add_conjuntos_photo_card")
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(18.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(AthenaPink.copy(alpha = 0.2f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = "Subir foto Conjunto",
                                                tint = AthenaPink,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "+ Subir Foto Conjunto",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AthenaPink,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Elige de tu galería",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.75f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // Espacios con fotos cargadas del Set Malibú (Celeste, Rosa, Marrón)
                                conjuntosSlots.forEach { slot ->
                                    LargePhotoSlotCard(
                                        slot = slot,
                                        onUploadClick = {
                                            activeSlotType = "conjuntos"
                                            activeTargetSlotId = slot.id
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        onRemoveClick = {
                                            viewModel.removeConjuntosSlot(slot.id)
                                        },
                                        onEditPriceClick = {
                                            slotForPriceEdit = slot
                                        },
                                        onAddToCartClick = {
                                            viewModel.addSlotToCart(slot)
                                        }
                                    )
                                }
                            }

                            // Flechas laterales Conjuntos
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        conjuntosScrollState.animateScrollBy(-600f)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 2.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF14121E).copy(alpha = 0.88f))
                                    .border(1.5.dp, AthenaPink, CircleShape)
                                    .testTag("conjuntos_side_arrow_prev")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Correr foto anterior",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        conjuntosScrollState.animateScrollBy(600f)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 2.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF14121E).copy(alpha = 0.88f))
                                    .border(1.5.dp, AthenaPink, CircleShape)
                                    .testTag("conjuntos_side_arrow_next")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Correr foto siguiente",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. BARRA DE BÚSQUEDA COMPACTA
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = {
                    Text(
                        text = "Buscar prenda...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = AthenaPink,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AthenaPink,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_bar")
            )
        }

        // TÍTULO DE PRENDAS
        item {
            val sectionTitle = when {
                isRebajasActive -> "PRENDAS EN REBAJAS 🔥"
                isAdidasActive -> "PRENDAS Y CONJUNTOS ADIDAS 👟"
                isConjuntosActive -> "CONJUNTOS & TRAJES ✨"
                else -> "CATÁLOGO DE PRENDAS"
            }
            Text(
                text = sectionTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = Color.White
            )
        }

        // 5. LISTA DE PRENDAS CON FOTOS GRANDES (UNA POR FILA, IMPACTO VISUAL MÁXIMO)
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
                        color = product.colors.firstOrNull() ?: ProductColor("Dorado", 0xFFD4AF37)
                    )
                },
                onEditPriceClick = {
                    productForPriceEdit = product
                }
            )
        }
    }

    // Diálogos para Asignar / Cambiar Precio
    slotForPriceEdit?.let { slot ->
        PriceEditDialog(
            itemName = slot.title,
            initialPrice = slot.price,
            onDismiss = { slotForPriceEdit = null },
            onSavePrice = { newPrice ->
                viewModel.updateSlotPrice(slot.id, newPrice)
            }
        )
    }

    productForPriceEdit?.let { prod ->
        PriceEditDialog(
            itemName = prod.name,
            initialPrice = prod.price,
            onDismiss = { productForPriceEdit = null },
            onSavePrice = { newPrice ->
                viewModel.updateProductPrice(prod.id, newPrice)
            }
        )
    }
}

/**
 * Tarjeta de Foto Grande para Rebajas y Galería de Usuario
 */
@Composable
private fun LargePhotoSlotCard(
    slot: RebajasImageSlot,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    onEditPriceClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .width(280.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                if (slot.isPlaceholder) {
                    onUploadClick()
                }
            }
            .testTag("slot_card_${slot.id}")
    ) {
        if (slot.isPlaceholder) {
            // Espacio vacío para agregar foto
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color(0xFF14131D))
                    .border(
                        1.5.dp,
                        AthenaPink.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AthenaPink.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar Foto",
                            tint = AthenaPink,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = slot.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = slot.subtitle ?: "Toca para agregar foto",
                        fontSize = 12.sp,
                        color = AthenaPink,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Espacio con Foto Grande
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
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

                    // Botón simple de eliminar foto en la esquina superior
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("remove_slot_${slot.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Etiqueta Rebaja
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CrimsonSale)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PRENDA REBAJA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Título, precio editable y botón de comprar/agregar al carrito
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = slot.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    slot.subtitle?.let {
                        Text(
                            text = it,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // LUGAR PARA PRECIO DEBAJO DE LA IMAGEN
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val displayPrice = slot.price ?: 35.0
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", displayPrice)}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AthenaGoldDark
                            )
                            if (slot.originalPrice != null && slot.originalPrice > displayPrice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", slot.originalPrice)}",
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Botón de Asignar / Cambiar Precio
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AthenaPink.copy(alpha = 0.15f))
                                .clickable { onEditPriceClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("slot_edit_price_${slot.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Precio",
                                tint = AthenaPink,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (slot.price != null) "Editar" else "Precio",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AthenaPink
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // OPCIÓN DE AGREGAR AL CARRITO PARA COMPRAR
                    Button(
                        onClick = onAddToCartClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AthenaPink,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("slot_buy_btn_${slot.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Agregar al Carrito • Comprar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
