package com.example.viewmodel

import android.app.Application
import java.util.Locale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AthenaRepository
import com.example.data.local.AthenaDatabase
import com.example.data.local.WishlistItem
import com.example.data.local.WishlistRepository
import com.example.model.AppNotification
import com.example.model.CartItem
import com.example.model.NotificationType
import com.example.model.Order
import com.example.model.Product
import com.example.model.ProductCategory
import com.example.model.ProductColor
import com.example.model.RebajasImageSlot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AthenaScreen(val title: String) {
    PORTADA("Portada"),
    CATALOG("Catálogo"),
    WISHLIST("Lista de Deseos"),
    PRODUCT_DETAIL("Detalle"),
    CART_CHECKOUT("Carrito & Pago"),
    LIVE_TRACKING("Seguimiento"),
    INVENTORY("Inventario"),
    NOTIFICATIONS("Notificaciones")
}

enum class PaymentType(val displayName: String) {
    CREDIT_CARD("Tarjeta de Crédito / Débito"),
    DIGITAL_WALLET("Billetera Digital (Nequi / Daviplata / G-Pay)"),
    PSE("PSE / Transferencia Bancaria")
}

data class InventoryKPIs(
    val totalSkus: Int,
    val totalUnits: Int,
    val totalValue: Double,
    val lowStockCount: Int,
    val outOfStockCount: Int
)

class AthenaViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: AthenaRepository = AthenaRepository(),
    private val wishlistRepository: WishlistRepository = WishlistRepository(
        AthenaDatabase.getInstance(application).wishlistDao()
    )
) : AndroidViewModel(application) {

    // Navigation State - starts at Portada
    private val _currentScreen = MutableStateFlow(AthenaScreen.PORTADA)
    val currentScreen: StateFlow<AthenaScreen> = _currentScreen.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Filter & Search State
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(ProductCategory.ALL)
    val selectedSizeFilter = MutableStateFlow<String?>(null)
    val isOnlySaleFilter = MutableStateFlow(false)
    val isOnlyInStockFilter = MutableStateFlow(false)

    // Data streams from repository
    val products = repository.products
    val cart = repository.cart
    val orders = repository.orders
    val activeTrackedOrderId = repository.activeTrackedOrderId
    val notifications = repository.notifications
    val activePushBanner = repository.activePushBanner
    val appliedCoupon = repository.appliedCoupon

    // ---------------- ROOM DATABASE WISHLIST ---------------- //
    val wishlistItems: StateFlow<List<WishlistItem>> = wishlistRepository.allWishlistItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wishlistProductIds: StateFlow<Set<String>> = wishlistRepository.wishlistProductIds
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Legacy/convenience alias keeping backward compatibility
    val favorites: StateFlow<Set<String>> = wishlistProductIds

    // ---------------- REBAJAS IMAGE SPACES (SLOTS & UPLOADS) ---------------- //
    private val _rebajasImageSlots = MutableStateFlow<List<RebajasImageSlot>>(
        listOf(
            RebajasImageSlot(
                id = "reb-slot-corset-encaje",
                title = "Corset Encaje Floral (Trio)",
                subtitle = "Blanco Nácar, Azul Cobalto & Rosa Palo",
                drawableResId = R.drawable.rebajas_corset_encaje_trio,
                price = 38.0,
                originalPrice = 55.0,
                associatedProductId = "reb-corset-1"
            ),
            RebajasImageSlot(
                id = "reb-slot-corset-leopardo",
                title = "Corset Bustier Animal Print & Black",
                subtitle = "Copa Armada, Tul & Varillas",
                drawableResId = R.drawable.rebajas_corset_leopardo_negro,
                price = 42.0,
                originalPrice = 60.0,
                associatedProductId = "reb-corset-2"
            ),
            RebajasImageSlot(
                id = "reb-slot-campera-piel",
                title = "Campera Térmica con Piel Sherpa",
                subtitle = "Marrón Caramelo & Beige Nieve",
                drawableResId = R.drawable.rebajas_campera_piel_duo,
                price = 78.0,
                originalPrice = 110.0,
                associatedProductId = "reb-campera-4"
            ),
            RebajasImageSlot(
                id = "reb-slot-conjunto-canale",
                title = "Conjunto Ribbed con Cierre",
                subtitle = "Burdeos Vino & Marrón Oxford Flare",
                drawableResId = R.drawable.rebajas_conjunto_canale_duo,
                price = 65.0,
                originalPrice = 89.0,
                associatedProductId = "reb-conjunto-3"
            ),
            RebajasImageSlot(
                id = "reb-slot-buzo-la",
                title = "Buzo Los Angeles 91",
                subtitle = "Oversize Streetwear • $13.000",
                drawableResId = R.drawable.rebajas_buzo_losangeles_91,
                price = 35.0,
                originalPrice = 55.0,
                associatedProductId = "reb-buzo-9"
            ),
            RebajasImageSlot(
                id = "slot-empty-1",
                title = "Espacio Lookbook #6",
                subtitle = "Toca para agregar foto",
                isPlaceholder = true,
                price = 45.0
            )
        )
    )
    val rebajasImageSlots: StateFlow<List<RebajasImageSlot>> = _rebajasImageSlots.asStateFlow()

    // ---------------- ADIDAS IMAGE SPACES (SLOTS & UPLOADS) ---------------- //
    private val _adidasImageSlots = MutableStateFlow<List<RebajasImageSlot>>(
        listOf(
            RebajasImageSlot(
                id = "adidas-slot-1",
                title = "Campera de Jean Adidas",
                subtitle = "Denim Jacket 3 Tiras Vintage",
                drawableResId = R.drawable.prod_campera_jean_adidas,
                price = 98.0,
                originalPrice = 135.0,
                associatedProductId = "adi-campera-jean-1"
            ),
            RebajasImageSlot(
                id = "adidas-slot-2",
                title = "Conjunto 3 Piezas Adidas",
                subtitle = "Campera Crop, Top & Short",
                drawableResId = R.drawable.prod_conjunto_3piezas_adidas,
                price = 88.0,
                originalPrice = 120.0,
                associatedProductId = "adi-conjunto-3p-2"
            ),
            RebajasImageSlot(
                id = "adidas-slot-empty-1",
                title = "Espacio Adidas #3",
                subtitle = "Toca para subir foto de prenda",
                isPlaceholder = true,
                price = 55.0
            ),
            RebajasImageSlot(
                id = "adidas-slot-empty-2",
                title = "Espacio Adidas #4",
                subtitle = "Toca para subir foto de prenda",
                isPlaceholder = true,
                price = 60.0
            )
        )
    )
    val adidasImageSlots: StateFlow<List<RebajasImageSlot>> = _adidasImageSlots.asStateFlow()

    // ---------------- CONJUNTOS IMAGE SPACES (SET MALIBÚ & SETS) ---------------- //
    private val _conjuntosImageSlots = MutableStateFlow<List<RebajasImageSlot>>(
        listOf(
            RebajasImageSlot(
                id = "conj-malibu-celeste",
                title = "Set Malibú Celeste",
                subtitle = "Top Halter Cierre & Oxford Flare",
                drawableResId = R.drawable.prod_set_malibu_celeste,
                price = 68.0,
                originalPrice = 95.0,
                associatedProductId = "set-malibu-celeste"
            ),
            RebajasImageSlot(
                id = "conj-malibu-rosa",
                title = "Set Malibú Rosa Pastel",
                subtitle = "Silueta Estilizada & Tiro Alto",
                drawableResId = R.drawable.prod_set_malibu_rosa,
                price = 68.0,
                originalPrice = 95.0,
                associatedProductId = "set-malibu-rosa"
            ),
            RebajasImageSlot(
                id = "conj-malibu-marron",
                title = "Set Malibú Marrón Chocolate",
                subtitle = "Canalé Premium Stretch",
                drawableResId = R.drawable.prod_set_malibu_marron,
                price = 68.0,
                originalPrice = 95.0,
                associatedProductId = "set-malibu-marron"
            ),
            RebajasImageSlot(
                id = "conj-slot-empty-1",
                title = "Espacio Conjuntos #4",
                subtitle = "Toca para subir foto de prenda",
                isPlaceholder = true,
                price = 65.0
            )
        )
    )
    val conjuntosImageSlots: StateFlow<List<RebajasImageSlot>> = _conjuntosImageSlots.asStateFlow()

    // Notification filter
    val notificationFilter = MutableStateFlow(NotificationType.ALL)

    private data class FilterCriteria(
        val query: String,
        val cat: ProductCategory,
        val size: String?,
        val onlySale: Boolean,
        val onlyInStock: Boolean
    )

    private val filterCriteria = combine(
        combine(searchQuery, selectedCategory) { q, c -> Pair(q, c) },
        combine(selectedSizeFilter, isOnlySaleFilter, isOnlyInStockFilter) { s, sale, stock -> Triple(s, sale, stock) }
    ) { (q, c), (s, sale, stock) ->
        FilterCriteria(q, c, s, sale, stock)
    }

    // Filtered Products
    val filteredProducts: StateFlow<List<Product>> = products.combine(filterCriteria) { prods, criteria ->
        prods.filter { p ->
            val matchesQuery = criteria.query.isBlank() ||
                p.name.contains(criteria.query, ignoreCase = true) ||
                p.description.contains(criteria.query, ignoreCase = true) ||
                p.sku.contains(criteria.query, ignoreCase = true) ||
                p.fabric.contains(criteria.query, ignoreCase = true)
            val matchesCat = when (criteria.cat) {
                ProductCategory.ALL -> true
                ProductCategory.REBAJAS -> p.discountPercent != null && p.discountPercent!! > 0
                ProductCategory.SETS -> p.category == ProductCategory.SETS || p.category == ProductCategory.ADIDAS
                ProductCategory.ADIDAS -> p.category == ProductCategory.ADIDAS || (p.category == ProductCategory.SETS && p.name.contains("Adidas", ignoreCase = true))
                else -> p.category == criteria.cat
            }
            val matchesSize = criteria.size == null || p.sizes.contains(criteria.size)
            val matchesSale = !criteria.onlySale || (p.discountPercent != null && p.discountPercent!! > 0)
            val matchesStock = !criteria.onlyInStock || p.stock > 0
            matchesQuery && matchesCat && matchesSize && matchesSale && matchesStock
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Tracked Order
    val activeTrackedOrder: StateFlow<Order?> = combine(
        orders,
        activeTrackedOrderId
    ) { orderList, activeId ->
        orderList.find { it.orderId == activeId } ?: orderList.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Inventory KPIs
    val inventoryKPIs: StateFlow<InventoryKPIs> = products.combine(orders) { prods, _ ->
        val totalSkus = prods.size
        val totalUnits = prods.sumOf { it.stock }
        val totalVal = prods.sumOf { it.stock * it.price }
        val lowStock = prods.count { it.isLowStock }
        val outOfStock = prods.count { it.isOutOfStock }
        InventoryKPIs(totalSkus, totalUnits, totalVal, lowStock, outOfStock)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InventoryKPIs(0, 0, 0.0, 0, 0))

    // Checkout Form State
    val checkoutStep = MutableStateFlow(1) // 1: Cart, 2: Address & Shipping, 3: Payment Gateway, 4: Success
    val customerName = MutableStateFlow("Camila Restrepo")
    val deliveryAddress = MutableStateFlow("Calle 85 #11-53, Apto 701")
    val deliveryCity = MutableStateFlow("Bogotá")
    val deliveryNotes = MutableStateFlow("Entregar en caja Athena Signature con lacre")
    val selectedPaymentType = MutableStateFlow(PaymentType.CREDIT_CARD)

    // Credit Card Fields
    val cardNumber = MutableStateFlow("4532 8920 1192 4242")
    val cardHolder = MutableStateFlow("CAMILA RESTREPO")
    val cardExpiry = MutableStateFlow("11/28")
    val cardCvv = MutableStateFlow("882")
    val isCardFlipped = MutableStateFlow(false)

    // Payment Processing State
    val isProcessingPayment = MutableStateFlow(false)
    val lastCompletedOrder = MutableStateFlow<Order?>(null)

    init {
        // Pre-populate Room Wishlist with luxury starting favorites if initial table is empty
        viewModelScope.launch {
            val existing = wishlistRepository.allWishlistItems
            // Pre-seed sample items so user immediately sees saved wishlist in first run
            val sampleP1 = products.value.find { it.id == "prod-1" }
            val sampleP3 = products.value.find { it.id == "prod-3" }
            sampleP1?.let { wishlistRepository.saveToWishlist(it) }
            sampleP3?.let { wishlistRepository.saveToWishlist(it) }
        }
    }

    // Navigation methods
    fun navigateTo(screen: AthenaScreen) {
        _currentScreen.value = screen
    }

    fun openMenuSection(category: ProductCategory) {
        selectedCategory.value = category
        if (category == ProductCategory.REBAJAS) {
            isOnlySaleFilter.value = true
        } else {
            isOnlySaleFilter.value = false
        }
        _currentScreen.value = AthenaScreen.CATALOG
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        _currentScreen.value = AthenaScreen.PRODUCT_DETAIL
    }

    fun goBack() {
        when (_currentScreen.value) {
            AthenaScreen.PORTADA -> { /* at root */ }
            AthenaScreen.PRODUCT_DETAIL -> _currentScreen.value = AthenaScreen.CATALOG
            AthenaScreen.WISHLIST -> _currentScreen.value = AthenaScreen.CATALOG
            AthenaScreen.CART_CHECKOUT -> {
                if (checkoutStep.value > 1 && checkoutStep.value < 4) {
                    checkoutStep.value -= 1
                } else {
                    _currentScreen.value = AthenaScreen.CATALOG
                }
            }
            AthenaScreen.CATALOG -> _currentScreen.value = AthenaScreen.PORTADA
            else -> _currentScreen.value = AthenaScreen.CATALOG
        }
    }

    // ---------------- ROOM DATABASE WISHLIST ACTIONS ---------------- //
    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isWishlisted = wishlistProductIds.value.contains(product.id)
            if (isWishlisted) {
                wishlistRepository.removeFromWishlist(product.id)
                repository.sendPushNotification(
                    title = "Eliminado de Lista de Deseos",
                    message = "'${product.name}' fue removido de tus guardados.",
                    type = NotificationType.VIP_EXCLUSIVE
                )
            } else {
                wishlistRepository.saveToWishlist(product)
                repository.sendPushNotification(
                    title = "¡Guardado en tu Wishlist!",
                    message = "'${product.name}' guardado en la base de datos Room para compra futura.",
                    type = NotificationType.VIP_EXCLUSIVE
                )
            }
        }
    }

    fun toggleFavorite(productId: String) {
        val prod = products.value.find { it.id == productId }
        if (prod != null) {
            toggleWishlist(prod)
        } else {
            repository.toggleFavorite(productId)
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(productId)
        }
    }

    fun clearWishlist() {
        viewModelScope.launch {
            wishlistRepository.clearWishlist()
        }
    }

    // ---------------- REBAJAS IMAGE SPACES MANAGEMENT ---------------- //
    fun addRebajasImageToSlot(slotId: String, imageUri: String, title: String? = null) {
        _rebajasImageSlots.update { current ->
            current.map { slot ->
                if (slot.id == slotId) {
                    slot.copy(
                        title = title ?: slot.title.replace("Espacio", "Look"),
                        subtitle = "Imagen añadida a Rebajas",
                        imageUri = imageUri,
                        drawableResId = null,
                        isPlaceholder = false
                    )
                } else slot
            }
        }
        repository.sendPushNotification(
            title = "Nueva Imagen en Rebajas",
            message = "Se ha actualizado el espacio visual de Rebajas exitosamente.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun addRebajasNewImage(title: String, imageUri: String) {
        val newSlot = RebajasImageSlot(
            id = "slot-custom-" + System.currentTimeMillis(),
            title = title.ifBlank { "Nuevo Look Rebajas" },
            subtitle = "Foto agregada por el usuario",
            imageUri = imageUri,
            drawableResId = null,
            isPlaceholder = false
        )
        _rebajasImageSlots.update { current ->
            // Insert before the empty placeholders
            val filled = current.filter { !it.isPlaceholder }
            val placeholders = current.filter { it.isPlaceholder }
            filled + newSlot + placeholders
        }
        repository.sendPushNotification(
            title = "Espacio de Imagen Añadido",
            message = "Se añadió '$title' al catálogo visual de Rebajas.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun addEmptyRebajasSlot() {
        val count = _rebajasImageSlots.value.size + 1
        val emptySlot = RebajasImageSlot(
            id = "slot-empty-" + System.currentTimeMillis(),
            title = "Espacio para Look #$count",
            subtitle = "Toca para agregar foto",
            isPlaceholder = true
        )
        _rebajasImageSlots.update { it + emptySlot }
    }

    fun removeRebajasSlot(slotId: String) {
        _rebajasImageSlots.update { current ->
            current.filterNot { it.id == slotId }
        }
    }

    // ---------------- ADIDAS IMAGE SPACES MANAGEMENT ---------------- //
    fun addAdidasImageToSlot(slotId: String, imageUri: String, title: String? = null) {
        _adidasImageSlots.update { current ->
            current.map { slot ->
                if (slot.id == slotId) {
                    slot.copy(
                        title = title ?: slot.title.replace("Espacio", "Look"),
                        subtitle = "Imagen añadida a Adidas",
                        imageUri = imageUri,
                        drawableResId = null,
                        isPlaceholder = false
                    )
                } else slot
            }
        }
        repository.sendPushNotification(
            title = "Nueva Foto en Colección Adidas",
            message = "Se actualizó la foto en el espacio de Conjuntos Adidas.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun addAdidasNewImage(title: String, imageUri: String) {
        val newSlot = RebajasImageSlot(
            id = "adidas-slot-custom-" + System.currentTimeMillis(),
            title = title.ifBlank { "Nuevo Look Adidas" },
            subtitle = "Foto agregada por el usuario",
            imageUri = imageUri,
            drawableResId = null,
            isPlaceholder = false
        )
        _adidasImageSlots.update { current ->
            val filled = current.filter { !it.isPlaceholder }
            val placeholders = current.filter { it.isPlaceholder }
            filled + newSlot + placeholders
        }
        repository.sendPushNotification(
            title = "Foto Adidas Añadida",
            message = "Se añadió '$title' a la colección Adidas.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun removeAdidasSlot(slotId: String) {
        _adidasImageSlots.update { current ->
            current.filterNot { it.id == slotId }
        }
    }

    // ---------------- CONJUNTOS IMAGE SPACES MANAGEMENT ---------------- //
    fun addConjuntosImageToSlot(slotId: String, imageUri: String, title: String? = null) {
        _conjuntosImageSlots.update { current ->
            current.map { slot ->
                if (slot.id == slotId) {
                    slot.copy(
                        title = title ?: slot.title.replace("Espacio", "Look"),
                        subtitle = "Imagen añadida a Conjuntos",
                        imageUri = imageUri,
                        drawableResId = null,
                        isPlaceholder = false
                    )
                } else slot
            }
        }
        repository.sendPushNotification(
            title = "Nueva Foto en Conjuntos",
            message = "Se actualizó la foto en el espacio de Conjuntos & Set Malibú.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun addConjuntosNewImage(title: String, imageUri: String) {
        val newSlot = RebajasImageSlot(
            id = "conj-slot-custom-" + System.currentTimeMillis(),
            title = title.ifBlank { "Nuevo Set Malibú" },
            subtitle = "Foto agregada por el usuario",
            imageUri = imageUri,
            drawableResId = null,
            isPlaceholder = false
        )
        _conjuntosImageSlots.update { current ->
            val filled = current.filter { !it.isPlaceholder }
            val placeholders = current.filter { it.isPlaceholder }
            filled + newSlot + placeholders
        }
        repository.sendPushNotification(
            title = "Foto de Conjunto Añadida",
            message = "Se añadió '$title' a la colección de Conjuntos.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun removeConjuntosSlot(slotId: String) {
        _conjuntosImageSlots.update { current ->
            current.filterNot { it.id == slotId }
        }
    }

    // ---------------- PRECIOS & COMPRA DIRECTA DESDE IMÁGENES ---------------- //
    fun updateSlotPrice(slotId: String, newPrice: Double) {
        _rebajasImageSlots.update { list ->
            list.map { if (it.id == slotId) it.copy(price = newPrice) else it }
        }
        _adidasImageSlots.update { list ->
            list.map { if (it.id == slotId) it.copy(price = newPrice) else it }
        }
        _conjuntosImageSlots.update { list ->
            list.map { if (it.id == slotId) it.copy(price = newPrice) else it }
        }
        repository.sendPushNotification(
            title = "Precio Actualizado",
            message = "Se actualizó el precio a $${String.format(Locale.US, "%.2f", newPrice)} correctamente.",
            type = NotificationType.PROMO_DROP
        )
    }

    fun updateProductPrice(productId: String, newPrice: Double) {
        repository.updateProductPrice(productId, newPrice)
        // Also keep any connected slot in sync
        _rebajasImageSlots.update { list ->
            list.map { if (it.associatedProductId == productId) it.copy(price = newPrice) else it }
        }
        _adidasImageSlots.update { list ->
            list.map { if (it.associatedProductId == productId) it.copy(price = newPrice) else it }
        }
        _conjuntosImageSlots.update { list ->
            list.map { if (it.associatedProductId == productId) it.copy(price = newPrice) else it }
        }
        repository.sendPushNotification(
            title = "Precio de Prenda Actualizado",
            message = "Nuevo precio: $${String.format(Locale.US, "%.2f", newPrice)}.",
            type = NotificationType.INVENTORY_ALERT
        )
    }

    fun addSlotToCart(slot: RebajasImageSlot) {
        val prod = slot.associatedProductId?.let { id -> products.value.find { it.id == id } }
            ?: products.value.find { it.name.contains(slot.title, ignoreCase = true) || slot.title.contains(it.name, ignoreCase = true) }
            ?: Product(
                id = "slot-prod-" + slot.id,
                name = slot.title,
                category = ProductCategory.REBAJAS,
                price = slot.price ?: 35.0,
                originalPrice = slot.originalPrice ?: 50.0,
                sizes = listOf("S", "M", "L"),
                colors = listOf(ProductColor("Tono Prenda", 0xFFE0A96D)),
                description = slot.subtitle ?: "Prenda exclusiva de colección",
                fabric = "Algodón peinado y tejido premium",
                stock = 10,
                sku = "ATH-SLOT-" + slot.id.takeLast(4).uppercase(),
                rating = 4.9f,
                reviewsCount = 15,
                imageResName = if (slot.drawableResId != null) "rebajas_corset_encaje_trio" else "athena_cover"
            )

        val finalProd = if (slot.price != null && slot.price > 0.0) {
            prod.copy(price = slot.price)
        } else {
            prod
        }

        addToCart(
            product = finalProd,
            size = finalProd.sizes.firstOrNull() ?: "M",
            color = finalProd.colors.firstOrNull() ?: ProductColor("Único", 0xFFFFFFFF)
        )
    }

    // ---------------- CART ACTIONS ---------------- //
    fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int = 1) {
        repository.addToCart(product, size, color, quantity)
        repository.sendPushNotification(
            title = "Añadido al Carrito",
            message = "${product.name} (Talla $size) agregado exitosamente.",
            type = NotificationType.VIP_EXCLUSIVE
        )
    }

    fun updateCartQuantity(cartItemId: String, delta: Int) {
        repository.updateCartQuantity(cartItemId, delta)
    }

    fun removeFromCart(cartItemId: String) {
        repository.removeFromCart(cartItemId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun applyCoupon(code: String): Boolean {
        return repository.applyCoupon(code)
    }

    fun removeCoupon() {
        repository.removeCoupon()
    }

    // Checkout processing simulation
    fun startCheckout() {
        checkoutStep.value = 2 // Go to Address
    }

    fun proceedToPayment() {
        checkoutStep.value = 3 // Go to Payment Gateway
    }

    fun processPayment() {
        viewModelScope.launch {
            isProcessingPayment.value = true
            delay(1800) // Realistic banking simulation delay
            isProcessingPayment.value = false

            val paymentName = when (selectedPaymentType.value) {
                PaymentType.CREDIT_CARD -> "Tarjeta terminada en ${cardNumber.value.takeLast(4)}"
                PaymentType.DIGITAL_WALLET -> "Billetera Digital Nequi / GPay"
                PaymentType.PSE -> "PSE Débito Bancario"
            }

            val newOrder = repository.placeOrder(
                customerName = customerName.value.ifBlank { "Cliente Athena VIP" },
                deliveryAddress = deliveryAddress.value.ifBlank { "Dirección Principal" },
                deliveryCity = deliveryCity.value.ifBlank { "Bogotá" },
                deliveryNotes = deliveryNotes.value,
                paymentMethodName = paymentName,
                shippingFee = 0.0 // Free Express VIP
            )

            lastCompletedOrder.value = newOrder
            checkoutStep.value = 4 // Success screen
        }
    }

    fun resetCheckout() {
        checkoutStep.value = 1
        lastCompletedOrder.value = null
    }

    // Live Tracking actions
    fun selectActiveOrder(orderId: String) {
        repository.selectActiveOrder(orderId)
    }

    fun advanceOrderTrackingStage(orderId: String) {
        repository.advanceOrderTrackingStage(orderId)
    }

    // Inventory actions
    fun adjustStock(productId: String, delta: Int) {
        repository.adjustStock(productId, delta)
    }

    fun updateProductStockDirectly(productId: String, stock: Int) {
        repository.updateProductStockDirectly(productId, stock)
    }

    fun toggleProductActive(productId: String) {
        repository.toggleProductActive(productId)
    }

    fun addNewProduct(
        name: String,
        category: ProductCategory,
        price: Double,
        stock: Int,
        fabric: String,
        description: String,
        sizes: List<String>
    ) {
        val sku = "ATH-" + category.name.take(3) + "-" + (100..999).random()
        val newProd = Product(
            id = "prod-" + System.currentTimeMillis(),
            name = name,
            category = category,
            price = price,
            originalPrice = null,
            sizes = sizes.ifEmpty { listOf("S", "M", "L") },
            description = description,
            fabric = fabric,
            stock = stock,
            sku = sku,
            isNew = true,
            isTrending = false
        )
        repository.addNewProduct(newProd)
    }

    // Push notification actions
    fun dismissPushBanner() {
        repository.dismissPushBanner()
    }

    fun markNotificationAsRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllNotificationsAsRead() {
        repository.markAllNotificationsAsRead()
    }

    fun clearAllNotifications() {
        repository.clearAllNotifications()
    }

    fun simulateCustomPushNotification(presetIndex: Int) {
        when (presetIndex) {
            0 -> repository.sendPushNotification(
                title = "¡Tu Mensajero Express está a 500m!",
                message = "El courier VIP ha llegado a tu cuadra con tu pedido #ATH-9284.",
                type = NotificationType.ORDER_UPDATE,
                linkedOrderId = "ATH-9284"
            )
            1 -> repository.sendPushNotification(
                title = "Alerta Flash: Colección Olimpo 25% OFF",
                message = "Usa el código GOLDEN20 en el checkout en las próximas 2 horas.",
                type = NotificationType.PROMO_DROP
            )
            2 -> repository.sendPushNotification(
                title = "Alerta de Inventario: Stock Bajo",
                message = "El 'Vestido Noche Artemisa' tiene solo 2 unidades restantes en almacén.",
                type = NotificationType.INVENTORY_ALERT
            )
            3 -> repository.sendPushNotification(
                title = "Invitación al Desfile de Temporada Athena",
                message = "Presentación exclusiva en vivo de la temporada Primavera/Verano para clientes VIP.",
                type = NotificationType.VIP_EXCLUSIVE
            )
        }
    }
}
