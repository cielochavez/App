package com.example.model

enum class NotificationType(val label: String) {
    ALL("Todas"),
    ORDER_UPDATE("Envíos"),
    PROMO_DROP("Promociones"),
    INVENTORY_ALERT("Inventario"),
    VIP_EXCLUSIVE("Exclusivo")
}

data class AppNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val linkedOrderId: String? = null
)
