package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for querying and modifying the Wishlist table.
 * All write queries are suspend functions, and reads expose reactive Flows.
 */
@Dao
interface WishlistDao {

    @Query("SELECT * FROM wishlist_items ORDER BY addedTimestamp DESC")
    fun getAllWishlistItems(): Flow<List<WishlistItem>>

    @Query("SELECT productId FROM wishlist_items")
    fun getAllWishlistProductIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isProductInWishlist(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WishlistItem)

    @Delete
    suspend fun deleteItem(item: WishlistItem)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun deleteByProductId(productId: String)

    @Query("DELETE FROM wishlist_items")
    suspend fun clearAll()
}
