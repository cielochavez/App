package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a saved clothing item in the user's Wishlist.
 * Allows users to persist their favorite items locally for future purchase.
 */
@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey
    val productId: String,
    val name: String,
    val price: Double,
    val originalPrice: Double? = null,
    val category: String,
    val fabric: String,
    val sku: String,
    val discountPercent: Int? = null,
    val isLowStock: Boolean = false,
    val stock: Int = 1,
    val addedTimestamp: Long = System.currentTimeMillis()
)
