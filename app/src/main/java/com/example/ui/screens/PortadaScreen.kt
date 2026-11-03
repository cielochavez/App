package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.model.getGalleryDrawables
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.example.model.Product
import com.example.model.ProductColor
import com.example.model.getDrawableResId
import com.example.ui.components.PriceEditDialog
import com.example.ui.theme.AthenaGoldDark
import java.util.Locale
import com.example.R
import com.example.model.ProductCategory
import com.example.ui.theme.AthenaPink
import com.example.ui.theme.AthenaPinkDark
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.DancingScriptFontFamily
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RosaFluor
import com.example.ui.theme.RosaFluorBright
import com.example.ui.theme.RosaFluorGlow
import com.example.ui.theme.StatusSuccess
import com.example.viewmodel.AthenaScreen
import com.example.viewmodel.AthenaViewModel

@Composable
fun PortadaScreen(
    viewModel: AthenaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    var productForPriceEdit by remember { mutableStateOf<Product?>(null) }
    val featuredScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Subtle ambient scale pulse for high-fashion appeal
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val heroScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.018f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF14121E),
                        Color(0xFF1C182E),
                        Color(0xFF12101B)
                    )
                )
            )
            .testTag("portada_screen")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. BARRA SUPERIOR: ESTADO ATELIER & INSTAGRAM
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                // Indicador Boutique Abierta
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ATELIER OFICIAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF1E1E1E)
                    )
                }

                // Botón Instagram Oficial
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(RosaFluor, AthenaPinkDark)
                            )
                        )
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Instagram", "@Athena_Style_")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "@Athena_Style_ copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("instagram_handle_pill")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Instagram",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@Athena_Style_",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. MENÚ EN LA PARTE SUPERIOR DE LA PANTALLA: REBAJAS • ADIDAS • CONJUNTOS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF1B172C))
                    .border(1.6.dp, RosaFluor.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp)
                    .testTag("portada_main_menu_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(RosaFluor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MENÚ PRINCIPAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.8.sp,
                            color = RosaFluor
                        )
                    }
                    Text(
                        text = "Colecciones directas",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. BOTÓN REBAJAS
                    Button(
                        onClick = { viewModel.openMenuSection(ProductCategory.REBAJAS) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonSale, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("menu_btn_rebajas")
                    ) {
                        Text(
                            text = "🔥 Rebajas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 2. BOTÓN ADIDAS
                    Button(
                        onClick = { viewModel.openMenuSection(ProductCategory.ADIDAS) },
                        colors = ButtonDefaults.buttonColors(containerColor = RosaFluor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("menu_btn_adidas")
                    ) {
                        Text(
                            text = "👟 Adidas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 3. BOTÓN CONJUNTOS
                    Button(
                        onClick = { viewModel.openMenuSection(ProductCategory.SETS) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF14121E)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("menu_btn_conjuntos")
                    ) {
                        Text(
                            text = "✨ Conjuntos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. EN EL CENTRO: NOMBRE DE LA APP Athena_Style EN LETRA DELICADA COLOR ROSA FLÚOR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                RosaFluor.copy(alpha = 0.16f),
                                Color(0xFF1D182E).copy(alpha = 0.96f),
                                Color(0xFF131122)
                            )
                        )
                    )
                    .border(2.dp, RosaFluor.copy(alpha = 0.85f), RoundedCornerShape(26.dp))
                    .padding(horizontal = 20.dp, vertical = 26.dp)
                    .testTag("portada_center_branding_card")
            ) {
                // Corona sutil / detalle decorativo
                Text(
                    text = "✧ ATELIER BOUTIQUE EXCLUSIVA ✧",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // NOMBRE DE LA APP EN LETRA DELICADA COLOR ROSA FLÚOR
                Text(
                    text = "Athena_Style",
                    fontFamily = DancingScriptFontFamily,
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Normal,
                    color = RosaFluor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .scale(heroScale)
                        .testTag("app_name_delicate_fluor_pink")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Líneas decorativas con destello en rosa flúor
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, RosaFluor)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = RosaFluor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(RosaFluor, Color.Transparent)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "TU ESTILO, TUS REGLAS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "INDUMENTARIA • ALTA MODA & ESTILO URBANO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = RosaFluorBright
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BANNER DIRECTO REBAJAS BOUTIQUE (Sin imagen gigante)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1628))
                    .border(1.2.dp, RosaFluor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable {
                        viewModel.isOnlySaleFilter.value = true
                        viewModel.selectedCategory.value = ProductCategory.REBAJAS
                        viewModel.navigateTo(AthenaScreen.CATALOG)
                    }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("portada_rebajas_shortcut_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CrimsonSale.copy(alpha = 0.25f))
                        ) {
                            Text(text = "🔥", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "REBAJAS EXCLUSIVAS",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RosaFluor
                            )
                            Text(
                                text = "Prendas seleccionadas con hasta 40% OFF",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ir a rebajas",
                        tint = RosaFluor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PRENDAS DESTACADAS CON PRECIO Y COMPRA DIRECTA AL CARRITO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF14121E))
                    .border(1.2.dp, AthenaPink.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .padding(16.dp)
                    .testTag("portada_featured_products_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PRENDAS DESTACADAS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            color = AthenaPink
                        )
                        Text(
                            text = "Precios y compra directa al carrito",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Flechas encabezado para correr fotos
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    featuredScrollState.animateScrollBy(-500f)
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF222030))
                                .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                .testTag("portada_header_arrow_prev")
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
                                    featuredScrollState.animateScrollBy(500f)
                                }
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF222030))
                                .border(1.dp, AthenaPink.copy(alpha = 0.5f), CircleShape)
                                .testTag("portada_header_arrow_next")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Correr fotos derecha",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "Ver Todo →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AthenaPink,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectedCategory.value = ProductCategory.ALL
                                    viewModel.isOnlySaleFilter.value = false
                                    viewModel.navigateTo(AthenaScreen.CATALOG)
                                }
                                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(featuredScrollState)
                    ) {
                        products.take(6).forEach { product ->
                            PortadaFeaturedProductItem(
                                product = product,
                                onEditPrice = { productForPriceEdit = product },
                                onAddToCart = {
                                    viewModel.addToCart(
                                        product = product,
                                        size = product.sizes.firstOrNull() ?: "M",
                                        color = product.colors.firstOrNull() ?: ProductColor("Dorado", 0xFFD4AF37)
                                    )
                                }
                            )
                        }
                    }

                    // Flechas laterales del carrusel para correr fotos cómodamente
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                featuredScrollState.animateScrollBy(-500f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 2.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14121E).copy(alpha = 0.88f))
                            .border(1.5.dp, AthenaPink, CircleShape)
                            .testTag("portada_side_arrow_prev")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Correr prendas izquierda",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                featuredScrollState.animateScrollBy(500f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14121E).copy(alpha = 0.88f))
                            .border(1.5.dp, AthenaPink, CircleShape)
                            .testTag("portada_side_arrow_next")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Correr prendas derecha",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // MAIN CALL TO ACTION: ENTRAR A LA TIENDA
            Button(
                onClick = {
                    viewModel.selectedCategory.value = ProductCategory.ALL
                    viewModel.isOnlySaleFilter.value = false
                    viewModel.selectedSizeFilter.value = null
                    viewModel.navigateTo(AthenaScreen.CATALOG)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF14121E),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AthenaPink),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("enter_store_btn")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AthenaPink)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "ENTRAR A LA TIENDA",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AthenaPink,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTTOM FROSTED QUICK SERVICE SHORTCUTS
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(vertical = 10.dp, horizontal = 8.dp)
            ) {
                QuickNavIcon(
                    icon = Icons.Default.Storefront,
                    label = "Catálogo",
                    onClick = { viewModel.navigateTo(AthenaScreen.CATALOG) }
                )
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFDCDCDC)))
                QuickNavIcon(
                    icon = Icons.Default.LocalShipping,
                    label = "Envíos GPS",
                    onClick = { viewModel.navigateTo(AthenaScreen.LIVE_TRACKING) }
                )
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFDCDCDC)))
                QuickNavIcon(
                    icon = Icons.Default.ShoppingBag,
                    label = "Bolsa",
                    onClick = { viewModel.navigateTo(AthenaScreen.CART_CHECKOUT) }
                )
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFDCDCDC)))
                QuickNavIcon(
                    icon = Icons.Default.Inventory2,
                    label = "Inventario",
                    onClick = { viewModel.navigateTo(AthenaScreen.INVENTORY) }
                )
            }

            Spacer(modifier = Modifier.height(110.dp))
        }

        // Diálogo para Asignar / Cambiar Precio desde la Portada
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
}

@Composable
private fun QuickActionChip(
    title: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun QuickNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF14121E),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF14121E)
        )
    }
}

@Composable
private fun PortadaFeaturedProductItem(
    product: Product,
    onEditPrice: () -> Unit,
    onAddToCart: () -> Unit
) {
    val gallery = remember(product.id) { product.getGalleryDrawables() }
    var currentImageIndex by remember(product.id) { mutableIntStateOf(0) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .testTag("portada_item_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .background(Color(0xFFEDE8E0))
            ) {
                val activeDrawable = if (gallery.isNotEmpty()) {
                    gallery[currentImageIndex.coerceIn(0, gallery.lastIndex)]
                } else {
                    product.getDrawableResId()
                }

                Image(
                    painter = painterResource(id = activeDrawable),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.discountPercent != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CrimsonSale)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Flechas para correr fotos individuales de la prenda si tiene varias fotos
                if (gallery.size > 1) {
                    // Contador de fotos
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${currentImageIndex + 1}/${gallery.size}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Flecha izquierda correr foto
                    IconButton(
                        onClick = {
                            currentImageIndex = if (currentImageIndex > 0) {
                                currentImageIndex - 1
                            } else {
                                gallery.lastIndex
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("portada_card_arrow_prev_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Foto anterior",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Flecha derecha correr foto
                    IconButton(
                        onClick = {
                            currentImageIndex = if (currentImageIndex < gallery.lastIndex) {
                                currentImageIndex + 1
                            } else {
                                0
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("portada_card_arrow_next_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Foto siguiente",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // LUGAR PARA PRECIO DEBAJO DE CADA IMAGEN
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", product.price)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AthenaGoldDark
                        )
                        if (product.originalPrice != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", product.originalPrice)}",
                                fontSize = 11.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AthenaPink.copy(alpha = 0.15f))
                            .clickable { onEditPrice() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("portada_edit_price_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Precio",
                            tint = AthenaPink,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Precio",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AthenaPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // OPCIÓN DE AGREGAR AL CARRITO PARA COMPRAR
                Button(
                    onClick = { onAddToCart() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AthenaPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("portada_add_cart_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Comprar • Carrito",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
