package com.example.focusfrog.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialShopItems(items: List<ShopItemEntity>)

    @Query("SELECT * FROM shop_items")
    fun getAllShopItems(): Flow<List<ShopItemEntity>>

    @Query("SELECT COUNT(*) FROM shop_items")
    suspend fun getShopItemsCount(): Int

    @Update
    suspend fun updateShopItem(item: ShopItemEntity)

    @Query("UPDATE shop_items SET isEquipped = 0 WHERE type = :type")
    suspend fun unequipAllOfType(type: String): Int

    @Query("UPDATE shop_items SET isEquipped = 1 WHERE id = :itemId")
    suspend fun equipItem(itemId: String): Int
}
