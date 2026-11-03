package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Local Room Database holder for Athena Style boutique.
 * Houses the Wishlist table and provides access to WishlistDao.
 */
@Database(
    entities = [WishlistItem::class],
    version = 1,
    exportSchema = false
)
abstract class AthenaDatabase : RoomDatabase() {

    abstract fun wishlistDao(): WishlistDao

    companion object {
        @Volatile
        private var INSTANCE: AthenaDatabase? = null

        fun getInstance(context: Context): AthenaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AthenaDatabase::class.java,
                    "athena_style_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
