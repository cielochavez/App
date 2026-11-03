package com.example.model

enum class OrderStatus(val title: String, val subtitle: String, val stepIndex: Int) {
    CONFIRMED("Pedido Confirmado", "Atelier Central Athena ha recibido tu orden", 0),
    PREPARING("Control de Calidad & Empaque", "Prendas planchadas al vapor y empacadas en caja de lujo", 1),
    IN_TRANSIT("En Tránsito", "En camino al centro logístico express", 2),
    OUT_FOR_DELIVERY("En Reparto en tu Zona", "El mensajero VIP está a pocos minutos de tu dirección", 3),
    DELIVERED("Entregado con Éxito", "Disfruta de tu exclusiva prenda Athena Style", 4)
}

data class TrackingTimelineEvent(
    val title: String,
    val description: String,
    val time: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class Order(
    val orderId: String,
    val dateFormatted: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val shippingFee: Double,
    val total: Double,
    val status: OrderStatus,
    val trackingNumber: String,
    val customerName: String,
    val deliveryAddress: String,
    val deliveryCity: String,
    val deliveryNotes: String = "Entregar en portería o recepción principal",
    val paymentMethodName: String,
    val courierName: String = "Alejandro Valdés - VIP Courier",
    val courierPhone: String = "+57 (310) 849-2144",
    val courierVehicle: String = "Van Climatizada Athena VIP #12",
    val courierProgress: Float = 0.65f, // 0.0f to 1.0f on route map
    val estimatedMinutes: Int = 22,
    val timeline: List<TrackingTimelineEvent> = emptyList()
)
