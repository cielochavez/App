package com.example.data.local

import com.example.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting data access to the local Room Wishlist.
 * Exposes observable Flows and suspend functions for the ViewModel.
 */
class WishlistRepository(
    private val wishlistDao: WishlistDao
) {
    val allWishlistItems: Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()
    val wishlistProductIds: Flow<List<String>> = wishlistDao.getAllWishlistProductIds()

    fun isWishlisted(productId: String): Flow<Boolean> = wishlistDao.isProductInWishlist(productId)

    suspend fun saveToWishlist(product: Product) {
        val item = WishlistItem(
            productId = product.id,
            name = product.name,
            price = product.price,
            originalPrice = product.originalPrice,
            category = product.category.displayName,
            fabric = product.fabric,
            sku = product.sku,
            discountPercent = product.discountPercent,
            isLowStock = product.isLowStock,
            stock = product.stock,
            addedTimestamp = System.currentTimeMillis()
        )
        wishlistDao.insertItem(item)
    }

    suspend fun removeFromWishlist(productId: String) {
        wishlistDao.deleteByProductId(productId)
    }

    suspend fun clearWishlist() {
        wishlistDao.clearAll()
    }
}
