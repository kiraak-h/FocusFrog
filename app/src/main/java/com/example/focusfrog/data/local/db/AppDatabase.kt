package com.example.focusfrog.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SessionEntity::class, ShopItemEntity::class, UserStatsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sessionDao(): SessionDao
    abstract fun shopDao(): ShopDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focus_frog.db"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                // Insert initial default user stats
                database.userStatsDao().insertOrUpdateUserStats(
                    UserStatsEntity(
                        id = 1,
                        bugsBalance = 0,
                        totalSessions = 0,
                        totalMinutes = 0,
                        currentStreak = 0,
                        bestStreak = 0,
                        lastSessionDate = null,
                        frogMood = "NEUTRAL"
                    )
                )

                // Insert initial default shop items
                database.shopDao().insertInitialShopItems(
                    listOf(
                        ShopItemEntity(
                            id = "hat_frog",
                            name = "Frog Hat",
                            type = "HAT",
                            price = 30,
                            isOwned = false,
                            isEquipped = false,
                            requiredStage = "TADPOLE"
                        ),
                        ShopItemEntity(
                            id = "sunglasses",
                            name = "Cool Shades",
                            type = "SUNGLASSES",
                            price = 40,
                            isOwned = false,
                            isEquipped = false,
                            requiredStage = "TADPOLE"
                        ),
                        ShopItemEntity(
                            id = "crown_royal",
                            name = "Royal Crown",
                            type = "CROWN",
                            price = 100,
                            isOwned = false,
                            isEquipped = false,
                            requiredStage = "ROYAL_FROG"
                        ),
                        ShopItemEntity(
                            id = "theme_pond",
                            name = "Pond Theme",
                            type = "THEME",
                            price = 0,
                            isOwned = true,
                            isEquipped = true,
                            requiredStage = "TADPOLE"
                        ),
                        ShopItemEntity(
                            id = "theme_night_sky",
                            name = "Night Sky Theme",
                            type = "THEME",
                            price = 50,
                            isOwned = false,
                            isEquipped = false,
                            requiredStage = "TADPOLE"
                        ),
                        ShopItemEntity(
                            id = "theme_rainforest",
                            name = "Rainforest Theme",
                            type = "THEME",
                            price = 80,
                            isOwned = false,
                            isEquipped = false,
                            requiredStage = "TADPOLE"
                        )
                    )
                )
            }
        }
    }
}
