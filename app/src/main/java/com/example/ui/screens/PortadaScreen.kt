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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.ObsidianNight
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
            .background(Color(0xFF9E9E9E)) // Seamless matching background hex
            .testTag("portada_screen")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // TOP BAR: STATUS BADGE & INSTAGRAM PILL
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Live Boutique Open Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
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

                // Instagram Handle Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AthenaPink, AthenaPinkDark)
                            )
                        )
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Instagram", "@Athena_Style_")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "@Athena_Style_ copiado al portapapeles", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("instagram_handle_pill")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Instagram",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // FULLSCREEN HERO LOGO / COVER ARTWORK
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .scale(heroScale)
                    .testTag("portada_artwork_container")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.athena_cover),
                    contentDescription = "Portada Athena Style - TU ESTILO, TUS REGLAS",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SLOGAN & BRAND IDENTITY
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF14121E))
                        .border(1.5.dp, AthenaPink, RoundedCornerShape(10.dp))
                        .padding(horizontal = 22.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AthenaPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TU ESTILO, TUS REGLAS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.2.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "INDUMENTARIA • ALTA MODA & ESTILO URBANO",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = AthenaPinkDark
                )
            }

            // FOTO DE PORTADA EN REBAJAS (Banner directo Boutique Athena)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0E16))
                    .border(1.2.dp, AthenaPink, RoundedCornerShape(16.dp))
                    .clickable {
                        viewModel.isOnlySaleFilter.value = true
                        viewModel.selectedCategory.value = ProductCategory.REBAJAS
                        viewModel.navigateTo(AthenaScreen.CATALOG)
                    }
                    .padding(8.dp)
                    .testTag("portada_rebajas_shortcut_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_portada_rebajas),
                        contentDescription = "Foto de portada en rebajas",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .width(135.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔥 REBAJAS BOUTIQUE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AthenaPink
                        )
                        Text(
                            text = "10 prendas reales hasta 40% OFF",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AthenaPink,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MENÚ PRINCIPAL: REBAJAS • ADIDAS • CONJUNTOS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF14121E))
                    .border(1.2.dp, AthenaPink.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .padding(16.dp)
                    .testTag("portada_main_menu_card")
            ) {
                Text(
                    text = "MENÚ PRINCIPAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = AthenaPink
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Colecciones directas:",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. REBAJAS
                    Button(
                        onClick = { viewModel.openMenuSection(ProductCategory.REBAJAS) },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonSale, contentColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
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

                    // 2. ADIDAS
                    Button(
                        onClick = { viewModel.openMenuSection(ProductCategory.ADIDAS) },
                        colors = ButtonDefaults.buttonColors(containerColor = AthenaPink, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("menu_btn_adidas")
                    ) {
                        Text(
                            text = "👟 Adidas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // 3. CONJUNTOS
                    Button(
                        onClick = { viewModel.openMenuSection(ProductCategory.SETS) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF14121E)),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
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
                    Column {
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

                    Text(
                        text = "Ver Catálogo →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AthenaPink,
                        modifier = Modifier
                            .clickable {
                                viewModel.selectedCategory.value = ProductCategory.ALL
                                viewModel.isOnlySaleFilter.value = false
                                viewModel.navigateTo(AthenaScreen.CATALOG)
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    products.take(6).forEach { product ->
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
                                        .height(170.dp)
                                        .background(Color(0xFFEDE8E0))
                                ) {
                                    Image(
                                        painter = painterResource(id = product.getDrawableResId()),
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
                                                .clickable { productForPriceEdit = product }
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
                                        onClick = {
                                            viewModel.addToCart(
                                                product = product,
                                                size = product.sizes.firstOrNull() ?: "M",
                                                color = product.colors.firstOrNull() ?: ProductColor("Dorado", 0xFFD4AF37)
                                            )
                                        },
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

            Spacer(modifier = Modifier.height(8.dp))
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
