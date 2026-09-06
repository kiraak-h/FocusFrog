package com.example.focusfrog.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "HAT", "SUNGLASSES", "CROWN", "THEME"
    val price: Int,
    val isOwned: Boolean,
    val isEquipped: Boolean,
    val requiredStage: String // "TADPOLE", "FROGLET", "BIG_FROG", "ROYAL_FROG"
)
