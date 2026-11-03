package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AthenaDatabase
import com.example.data.local.WishlistDao
import com.example.data.local.WishlistItem
import com.example.data.local.WishlistRepository
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WishlistDatabaseTest {

    private lateinit var database: AthenaDatabase
    private lateinit var wishlistDao: WishlistDao
    private lateinit var repository: WishlistRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AthenaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        wishlistDao = database.wishlistDao()
        repository = WishlistRepository(wishlistDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveWishlistItem() = runTest {
        val item = WishlistItem(
            productId = "prod-test-1",
            name = "Vestido Noche Artemisa",
            price = 320.0,
            originalPrice = 450.0,
            category = "Vestidos",
            fabric = "Seda Natural",
            sku = "ATH-VES-001",
            discountPercent = 29
        )

        wishlistDao.insertItem(item)

        val items = wishlistDao.getAllWishlistItems().first()
        assertEquals(1, items.size)
        assertEquals("Vestido Noche Artemisa", items[0].name)
        assertEquals(320.0, items[0].price, 0.001)

        val isWishlisted = wishlistDao.isProductInWishlist("prod-test-1").first()
        assertTrue(isWishlisted)
    }

    @Test
    fun deleteWishlistItemById() = runTest {
        val item = WishlistItem(
            productId = "prod-test-2",
            name = "Corset Ribbed",
            price = 110.0,
            category = "Rebajas",
            fabric = "Algodón & Encaje",
            sku = "ATH-REB-002"
        )
        wishlistDao.insertItem(item)

        var items = wishlistDao.getAllWishlistItems().first()
        assertEquals(1, items.size)

        wishlistDao.deleteByProductId("prod-test-2")
        items = wishlistDao.getAllWishlistItems().first()
        assertEquals(0, items.size)

        val isWishlisted = wishlistDao.isProductInWishlist("prod-test-2").first()
        assertFalse(isWishlisted)
    }

    @Test
    fun repositorySaveAndRemoveProduct() = runTest {
        val product = Product(
            id = "prod-test-3",
            name = "Campera con Piel Térmica",
            category = ProductCategory.REBAJAS,
            price = 195.0,
            originalPrice = 280.0,
            sizes = listOf("S", "M", "L"),
            description = "Campera acolchada con interior en borrego térmico.",
            fabric = "Algodón & Piel Sintética",
            stock = 4,
            sku = "ATH-REB-003"
        )

        repository.saveToWishlist(product)

        val wishlist = repository.allWishlistItems.first()
        assertEquals(1, wishlist.size)
        assertEquals("Campera con Piel Térmica", wishlist[0].name)
        assertEquals(195.0, wishlist[0].price, 0.001)

        val productIds = repository.wishlistProductIds.first()
        assertTrue(productIds.contains("prod-test-3"))

        repository.removeFromWishlist("prod-test-3")
        val emptyWishlist = repository.allWishlistItems.first()
        assertTrue(emptyWishlist.isEmpty())
    }
}
