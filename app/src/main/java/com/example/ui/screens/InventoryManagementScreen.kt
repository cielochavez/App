package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.getDrawableResId
import com.example.ui.theme.AthenaGold
import com.example.ui.theme.AthenaGoldDark
import com.example.ui.theme.CrimsonSale
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.AthenaViewModel
import java.util.Locale

enum class InventoryFilter(val label: String) {
    ALL("Todos"),
    LOW_STOCK("Stock Crítico (<5)"),
    OUT_OF_STOCK("Agotados"),
    ACTIVE("Activos en Tienda")
}

@Composable
fun InventoryManagementScreen(
    viewModel: AthenaViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val kpis by viewModel.inventoryKPIs.collectAsState()

    var activeFilter by remember { mutableStateOf(InventoryFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val displayedProducts = products.filter { prod ->
        val matchesQuery = searchQuery.isBlank() ||
            prod.name.contains(searchQuery, ignoreCase = true) ||
            prod.sku.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (activeFilter) {
            InventoryFilter.ALL -> true
            InventoryFilter.LOW_STOCK -> prod.isLowStock
            InventoryFilter.OUT_OF_STOCK -> prod.isOutOfStock
            InventoryFilter.ACTIVE -> prod.isActive
        }
        matchesQuery && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AthenaGold,
                contentColor = ObsidianNight,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_product_inventory_fab")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Prenda")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nueva Prenda", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp)
        ) {
            // Header Title
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Control de Inventario & Almacén",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Gestión en tiempo real de existencias, SKUs y alertas de stock de Athena Style.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Executive Dashboard KPI Cards
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        title = "Total SKUs",
                        value = "${kpis.totalSkus}",
                        icon = Icons.Default.Inventory2,
                        iconTint = AthenaGoldDark,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Unidades",
                        value = "${kpis.totalUnits}",
                        icon = Icons.Default.Inventory2,
                        iconTint = StatusSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    KpiCard(
                        title = "Valor Total Stock",
                        value = "$${String.format(Locale.US, "%.0f", kpis.totalValue)}",
                        icon = Icons.Default.AttachMoney,
                        iconTint = AthenaGold,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Stock Crítico",
                        value = "${kpis.lowStockCount}",
                        icon = Icons.Default.Warning,
                        iconTint = CrimsonSale,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar en inventario por nombre o SKU...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AthenaGoldDark) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inventory_search_bar")
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Filter Chips
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    InventoryFilter.values().forEach { filter ->
                        val isSelected = activeFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeFilter = filter },
                            label = { Text(filter.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ObsidianNight,
                                selectedLabelColor = AthenaGold
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) AthenaGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Inventory List Items
            items(displayedProducts, key = { it.id }) { product ->
                InventoryItemRow(
                    product = product,
                    onStockChange = { delta -> viewModel.adjustStock(product.id, delta) },
                    onToggleActive = { viewModel.toggleProductActive(product.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }

    // Modal Dialog: Add New Product to Catalog
    if (showAddDialog) {
        var newName by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf(ProductCategory.OLYMPUS) }
        var newPrice by remember { mutableStateOf("145.0") }
        var newStock by remember { mutableStateOf("10") }
        var newFabric by remember { mutableStateOf("100% Seda de Morera Natural") }
        var newDescription by remember { mutableStateOf("Prenda confeccionada a medida en los talleres Athena con finos acabados.") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Añadir Prenda al Inventario",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nombre de la Prenda") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_product_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("Precio ($)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("new_product_price_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = newStock,
                            onValueChange = { newStock = it },
                            label = { Text("Stock Inicial") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("new_product_stock_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newFabric,
                        onValueChange = { newFabric = it },
                        label = { Text("Tejido / Composición") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Descripción") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val priceVal = newPrice.toDoubleOrNull() ?: 120.0
                            val stockVal = newStock.toIntOrNull() ?: 5
                            viewModel.addNewProduct(
                                name = newName,
                                category = newCategory,
                                price = priceVal,
                                stock = stockVal,
                                fabric = newFabric,
                                description = newDescription,
                                sizes = listOf("S", "M", "L")
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AthenaGold, contentColor = ObsidianNight),
                    modifier = Modifier.testTag("confirm_add_product_btn")
                ) {
                    Text("Guardar e Integrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f))
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun InventoryItemRow(
    product: Product,
    onStockChange: (Int) -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("inventory_item_${product.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFEDE8E0))
            ) {
                Image(
                    painter = painterResource(id = product.getDrawableResId()),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.sku,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", product.price)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AthenaGoldDark
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stock status badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusText = when {
                        product.isOutOfStock -> "AGOTADO"
                        product.isLowStock -> "CRÍTICO (${product.stock} un.)"
                        else -> "STOCK ÓPTIMO (${product.stock} un.)"
                    }
                    val statusColor = when {
                        product.isOutOfStock -> CrimsonSale
                        product.isLowStock -> StatusWarning
                        else -> StatusSuccess
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            // Quick Stock adjustments
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                IconButton(
                    onClick = { onStockChange(-1) },
                    modifier = Modifier.size(32.dp).testTag("decrease_stock_${product.id}")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Reducir Stock", modifier = Modifier.size(16.dp))
                }
                Text(
                    text = product.stock.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onStockChange(1) },
                    modifier = Modifier.size(32.dp).testTag("increase_stock_${product.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar Stock", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Toggle visibility switch
            IconButton(
                onClick = onToggleActive,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (product.isActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Estado en tienda",
                    tint = if (product.isActive) StatusSuccess else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
