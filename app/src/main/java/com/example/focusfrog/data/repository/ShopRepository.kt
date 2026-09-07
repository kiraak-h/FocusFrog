package com.example.focusfrog.data.repository

import com.example.focusfrog.data.local.db.ShopDao
import com.example.focusfrog.data.local.db.ShopItemEntity
import com.example.focusfrog.data.local.db.UserStatsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShopRepository(
    private val shopDao: ShopDao,
    private val userStatsDao: UserStatsDao
) {
    val allShopItems: Flow<List<ShopItemEntity>> = shopDao.getAllShopItems()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            ensureDefaultShopItemsExist()
        }
    }

    private suspend fun ensureDefaultShopItemsExist() {
        val defaultItems = listOf(
            ShopItemEntity("hat_frog", "Frog Hat", "HAT", 30, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("wizard_hat", "Wizard Hat", "HAT", 120, isOwned = false, isEquipped = false, "FROGLET"),
            ShopItemEntity("sunglasses", "Cool Shades", "SUNGLASSES", 40, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("bow_tie", "Bow Tie", "NECK", 35, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("headphones", "Headphones", "HEAD", 60, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("leaf_umbrella", "Tiny Leaf Umbrella", "HAND", 45, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("crown_royal", "Royal Crown", "CROWN", 100, isOwned = false, isEquipped = false, "ROYAL_FROG"),
            ShopItemEntity("theme_pond", "Pond Theme", "THEME", 0, isOwned = true, isEquipped = true, "TADPOLE"),
            ShopItemEntity("theme_night_sky", "Night Sky Theme", "THEME", 50, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("theme_rainforest", "Rainforest Theme", "THEME", 80, isOwned = false, isEquipped = false, "TADPOLE"),
            ShopItemEntity("theme_sunset", "Sunset Theme", "THEME", 150, isOwned = false, isEquipped = false, "TADPOLE")
        )
        shopDao.insertInitialShopItems(defaultItems)
    }

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
