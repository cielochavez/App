package com.example.data

import com.example.model.AppNotification
import com.example.model.CartItem
import com.example.model.NotificationType
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.ProductColor
import com.example.model.TrackingTimelineEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AthenaRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _products = MutableStateFlow(AthenaSampleData.sampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(setOf("prod-1", "prod-3", "prod-7"))
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(listOf(AthenaSampleData.createInitialOrder()))
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _activeTrackedOrderId = MutableStateFlow("ATH-9284")
    val activeTrackedOrderId: StateFlow<String> = _activeTrackedOrderId.asStateFlow()

    private val _notifications = MutableStateFlow(AthenaSampleData.sampleNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Real-time Push Notification Floating Banner
    private val _activePushBanner = MutableStateFlow<AppNotification?>(null)
    val activePushBanner: StateFlow<AppNotification?> = _activePushBanner.asStateFlow()

    // Coupon discount logic
    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    // ---------------- CART MANAGEMENT ---------------- //
    fun addToCart(product: Product, size: String, color: ProductColor, quantity: Int = 1) {
        _cart.update { currentList ->
            val existingIndex = currentList.indexOfFirst {
                it.product.id == product.id && it.selectedSize == size && it.selectedColor.name == color.name
            }
            if (existingIndex >= 0) {
                currentList.toMutableList().also { list ->
                    val existing = list[existingIndex]
                    list[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
                }
            } else {
                currentList + CartItem(
                    product = product,
                    selectedSize = size,
                    selectedColor = color,
                    quantity = quantity
                )
            }
        }
    }

    fun updateCartQuantity(cartItemId: String, delta: Int) {
        _cart.update { currentList ->
            currentList.mapNotNull { item ->
                if (item.id == cartItemId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else item
            }
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cart.update { currentList -> currentList.filterNot { it.id == cartItemId } }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // ---------------- FAVORITES / WISHLIST ---------------- //
    fun toggleFavorite(productId: String) {
        _favorites.update { currentFavs ->
            if (currentFavs.contains(productId)) {
                currentFavs - productId
            } else {
                currentFavs + productId
            }
        }
    }

    // ---------------- COUPONS ---------------- //
    fun applyCoupon(code: String): Boolean {
        val cleanCode = code.trim().uppercase()
        return if (cleanCode == "ATHENA10" || cleanCode == "GOLDEN20" || cleanCode == "DIOSA15") {
            _appliedCoupon.value = cleanCode
            sendPushNotification(
                title = "¡Cupón Aplicado!",
                message = "Descuento del código $cleanCode aplicado exitosamente a tu orden.",
                type = NotificationType.PROMO_DROP
            )
            true
        } else {
            false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // ---------------- CHECKOUT & ORDER CREATION ---------------- //
    fun placeOrder(
        customerName: String,
        deliveryAddress: String,
        deliveryCity: String,
        deliveryNotes: String,
        paymentMethodName: String,
        shippingFee: Double
    ): Order {
        val currentCart = _cart.value
        val subtotal = currentCart.sumOf { it.totalPrice }
        val discountRate = when (_appliedCoupon.value) {
            "ATHENA10" -> 0.10
            "GOLDEN20" -> 0.20
            "DIOSA15" -> 0.15
            else -> 0.0
        }
        val discount = subtotal * discountRate
        val total = (subtotal - discount + shippingFee).coerceAtLeast(0.0)

        val newOrderId = "ATH-" + (1000..9999).random()
        val trackingCode = "ATH-EXP-" + (10000..99999).random() + "-CO"

        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())

        val timeline = listOf(
            TrackingTimelineEvent(
                title = "Pedido Confirmado",
                description = "Tu pago ha sido procesado mediante $paymentMethodName con cifrado 256-bit.",
                time = formattedDate,
                isCompleted = true,
                isCurrent = true
            ),
            TrackingTimelineEvent(
                title = "Control de Calidad & Empaque de Lujo",
                description = "Revisión artesanal de costuras, planchado a vapor y empaque en estuche Athena.",
                time = "En preparación",
                isCompleted = false,
                isCurrent = false
            ),
            TrackingTimelineEvent(
                title = "En Tránsito a Hub Express",
                description = "Despacho con sellos inviolables de seguridad.",
                time = "Próximo paso",
                isCompleted = false,
                isCurrent = false
            ),
            TrackingTimelineEvent(
                title = "En Reparto en tu Zona",
                description = "Asignación de mensajero VIP en ruta.",
                time = "Pendiente",
                isCompleted = false,
                isCurrent = false
            ),
            TrackingTimelineEvent(
                title = "Entregado con Éxito",
                description = "Entrega personal y confirmación de satisfacción.",
                time = "Pendiente",
                isCompleted = false,
                isCurrent = false
            )
        )

        val newOrder = Order(
            orderId = newOrderId,
            dateFormatted = formattedDate,
            items = currentCart,
            subtotal = subtotal,
            discount = discount,
            shippingFee = shippingFee,
            total = total,
            status = OrderStatus.CONFIRMED,
            trackingNumber = trackingCode,
            customerName = customerName,
            deliveryAddress = deliveryAddress,
            deliveryCity = deliveryCity,
            deliveryNotes = deliveryNotes,
            paymentMethodName = paymentMethodName,
            courierProgress = 0.15f,
            estimatedMinutes = 35,
            timeline = timeline
        )

        // Reduce stock in products inventory
        currentCart.forEach { item ->
            adjustStock(item.product.id, -item.quantity)
        }

        _orders.update { listOf(newOrder) + it }
        _activeTrackedOrderId.value = newOrderId
        _cart.value = emptyList()
        _appliedCoupon.value = null

        // Trigger Push notification
        sendPushNotification(
            title = "¡Orden #$newOrderId Confirmada!",
            message = "Hemos recibido tu compra por $${String.format(Locale.US, "%.2f", total)}. Tu seguimiento en tiempo real ya está activo.",
            type = NotificationType.ORDER_UPDATE,
            linkedOrderId = newOrderId
        )

        return newOrder
    }

    // ---------------- REAL-TIME SHIPPING ADVANCEMENT ---------------- //
    fun selectActiveOrder(orderId: String) {
        _activeTrackedOrderId.value = orderId
    }

    fun advanceOrderTrackingStage(orderId: String) {
        _orders.update { currentOrders ->
            currentOrders.map { order ->
                if (order.orderId == orderId) {
                    val nextStatus = when (order.status) {
                        OrderStatus.CONFIRMED -> OrderStatus.PREPARING
                        OrderStatus.PREPARING -> OrderStatus.IN_TRANSIT
                        OrderStatus.IN_TRANSIT -> OrderStatus.OUT_FOR_DELIVERY
                        OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                        OrderStatus.DELIVERED -> OrderStatus.CONFIRMED // wrap around for simulation
                    }

                    val nextProgress = when (nextStatus) {
                        OrderStatus.CONFIRMED -> 0.15f
                        OrderStatus.PREPARING -> 0.35f
                        OrderStatus.IN_TRANSIT -> 0.55f
                        OrderStatus.OUT_FOR_DELIVERY -> 0.85f
                        OrderStatus.DELIVERED -> 1.0f
                    }

                    val nextEta = when (nextStatus) {
                        OrderStatus.CONFIRMED -> 40
                        OrderStatus.PREPARING -> 32
                        OrderStatus.IN_TRANSIT -> 20
                        OrderStatus.OUT_FOR_DELIVERY -> 8
                        OrderStatus.DELIVERED -> 0
                    }

                    val updatedTimeline = order.timeline.mapIndexed { idx, evt ->
                        when {
                            idx < nextStatus.stepIndex -> evt.copy(isCompleted = true, isCurrent = false)
                            idx == nextStatus.stepIndex -> evt.copy(isCompleted = (nextStatus == OrderStatus.DELIVERED), isCurrent = true)
                            else -> evt.copy(isCompleted = false, isCurrent = false)
                        }
                    }

                    // Push notification alert
                    val notifTitle = when (nextStatus) {
                        OrderStatus.PREPARING -> "Pedido #${order.orderId}: Empaque de Lujo"
                        OrderStatus.IN_TRANSIT -> "Pedido #${order.orderId}: En Tránsito Express"
                        OrderStatus.OUT_FOR_DELIVERY -> "¡El mensajero está en tu zona! (#${order.orderId})"
                        OrderStatus.DELIVERED -> "¡Pedido #${order.orderId} Entregado!"
                        OrderStatus.CONFIRMED -> "Pedido #${order.orderId}: Confirmado"
                    }

                    sendPushNotification(
                        title = notifTitle,
                        message = nextStatus.subtitle,
                        type = NotificationType.ORDER_UPDATE,
                        linkedOrderId = order.orderId
                    )

                    order.copy(
                        status = nextStatus,
                        courierProgress = nextProgress,
                        estimatedMinutes = nextEta,
                        timeline = updatedTimeline
                    )
                } else order
            }
        }
    }

    // ---------------- INVENTORY MANAGEMENT ---------------- //
    fun adjustStock(productId: String, delta: Int) {
        _products.update { currentProducts ->
            currentProducts.map { prod ->
                if (prod.id == productId) {
                    val updatedStock = (prod.stock + delta).coerceAtLeast(0)
                    if (updatedStock <= 3 && prod.stock > 3) {
                        sendPushNotification(
                            title = "Alerta de Stock Crítico",
                            message = "Quedan solo $updatedStock unidades disponibles de '${prod.name}'. Revisa el inventario de Athena.",
                            type = NotificationType.INVENTORY_ALERT
                        )
                    }
                    prod.copy(stock = updatedStock)
                } else prod
            }
        }
    }

    fun updateProductStockDirectly(productId: String, newStock: Int) {
        _products.update { currentProducts ->
            currentProducts.map { prod ->
                if (prod.id == productId) {
                    val validStock = newStock.coerceAtLeast(0)
                    prod.copy(stock = validStock)
                } else prod
            }
        }
    }

    fun updateProductPrice(productId: String, newPrice: Double) {
        _products.update { currentProducts ->
            currentProducts.map { prod ->
                if (prod.id == productId) {
                    prod.copy(price = newPrice.coerceAtLeast(0.0))
                } else prod
            }
        }
    }

    fun toggleProductActive(productId: String) {
        _products.update { currentProducts ->
            currentProducts.map { prod ->
                if (prod.id == productId) prod.copy(isActive = !prod.isActive) else prod
            }
        }
    }

    fun addNewProduct(product: Product) {
        _products.update { listOf(product) + it }
        sendPushNotification(
            title = "Nuevo Producto Añadido al Catálogo",
            message = "Se ha integrado '${product.name}' con SKU ${product.sku} y stock de ${product.stock} unidades.",
            type = NotificationType.INVENTORY_ALERT
        )
    }

    // ---------------- PUSH NOTIFICATIONS ---------------- //
    fun sendPushNotification(
        title: String,
        message: String,
        type: NotificationType,
        linkedOrderId: String? = null
    ) {
        val notif = AppNotification(
            title = title,
            message = message,
            timeAgo = "Ahora",
            type = type,
            isRead = false,
            linkedOrderId = linkedOrderId
        )
        _notifications.update { listOf(notif) + it }

        // Trigger floating in-app banner
        _activePushBanner.value = notif
        scope.launch {
            delay(5000)
            if (_activePushBanner.value?.id == notif.id) {
                _activePushBanner.value = null
            }
        }
    }

    fun dismissPushBanner() {
        _activePushBanner.value = null
    }

    fun markNotificationAsRead(id: String) {
        _notifications.update { current ->
            current.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.update { current ->
            current.map { it.copy(isRead = true) }
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }
}
