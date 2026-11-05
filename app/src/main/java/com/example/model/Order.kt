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
    val sellerName: String = "Athena_style",
    val sellerPhone: String = "+54 9 2983 52-9883",
    val courierName: String = "Alejandro Valdés - Repartidor Athena_style",
    val courierPhone: String = "+54 9 2983 52-9883",
    val courierVehicle: String = "Moto Reparto Express Athena #04",
    val courierProgress: Float = 0.0f, // 0.0f to 1.0f on route map
    val estimatedMinutes: Int = 18,
    val remainingDistanceKm: Double = 3.6,
    val postalCode: String = "7500",
    val latitude: Double = -38.3752,
    val longitude: Double = -60.2783,
    val isOrderAccepted: Boolean = false,
    val courierStatusText: String = "Esperando que el Atelier acepte el pedido",
    val timeline: List<TrackingTimelineEvent> = emptyList()
)
