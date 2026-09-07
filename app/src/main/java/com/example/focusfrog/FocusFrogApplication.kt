package com.example.focusfrog

import android.app.Application
import com.example.focusfrog.data.local.datastore.UserPreferencesRepository
import com.example.focusfrog.data.local.db.AppDatabase
import com.example.focusfrog.data.repository.FocusRepository
import com.example.focusfrog.data.repository.ShopRepository
import com.example.focusfrog.util.NotificationHelper
import com.example.focusfrog.util.SoundManager

class FocusFrogApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val userPreferencesRepository by lazy { UserPreferencesRepository(this) }
    val focusRepository by lazy {
        FocusRepository(database.sessionDao(), database.userStatsDao())
    }
    val shopRepository by lazy {
        ShopRepository(database.shopDao(), database.userStatsDao())
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        SoundManager.initialize(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        SoundManager.release()
    }
}
