package com.example.focusfrog.data.repository

import com.example.focusfrog.data.local.db.ShopDao
import com.example.focusfrog.data.local.db.ShopItemEntity
import com.example.focusfrog.data.local.db.UserStatsDao
import kotlinx.coroutines.flow.Flow

class ShopRepository(
    private val shopDao: ShopDao,
    private val userStatsDao: UserStatsDao
) {
    val allShopItems: Flow<List<ShopItemEntity>> = shopDao.getAllShopItems()

    suspend fun buyItem(item: ShopItemEntity): Boolean {
        val stats = userStatsDao.getUserStatsOnce() ?: return false
        if (stats.bugsBalance < item.price) return false

        val updatedStats = stats.copy(bugsBalance = stats.bugsBalance - item.price)
        userStatsDao.insertOrUpdateUserStats(updatedStats)

        val updatedItem = item.copy(isOwned = true)
        shopDao.updateShopItem(updatedItem)
        return true
    }

    suspend fun equipItem(item: ShopItemEntity) {
        if (!item.isOwned) return
        shopDao.unequipAllOfType(item.type)
        shopDao.equipItem(item.id)
    }

    suspend fun unequipItem(item: ShopItemEntity) {
        val updated = item.copy(isEquipped = false)
        shopDao.updateShopItem(updated)

        // If unequipping a theme, restore default Pond Theme as equipped in DB
        if (item.type == "THEME") {
            shopDao.equipItem("theme_pond")
        }
    }
}
