package com.example.model

data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val product: Product,
    val selectedSize: String,
    val selectedColor: ProductColor,
    val quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}
