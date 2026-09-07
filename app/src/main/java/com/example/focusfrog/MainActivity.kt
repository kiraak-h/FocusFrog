package com.example.focusfrog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.focusfrog.ui.diary.DiaryViewModel
import com.example.focusfrog.ui.diary.DiaryViewModelFactory
import com.example.focusfrog.ui.main.MainScreen
import com.example.focusfrog.ui.shop.ShopViewModel
import com.example.focusfrog.ui.shop.ShopViewModelFactory
import com.example.focusfrog.ui.stats.StatsViewModel
import com.example.focusfrog.ui.stats.StatsViewModelFactory
import com.example.focusfrog.ui.theme.FocusFrogTheme
import com.example.focusfrog.ui.timer.TimerViewModel
import com.example.focusfrog.ui.timer.TimerViewModelFactory
import com.example.focusfrog.util.DailyReminderScheduler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val timerViewModel: TimerViewModel by viewModels {
        val app = application as FocusFrogApplication
        TimerViewModelFactory(app.focusRepository, app.shopRepository, app.userPreferencesRepository)
    }

    private val shopViewModel: ShopViewModel by viewModels {
        val app = application as FocusFrogApplication
        ShopViewModelFactory(app.shopRepository, app.focusRepository, app.userPreferencesRepository)
    }

    private val statsViewModel: StatsViewModel by viewModels {
        val app = application as FocusFrogApplication
        StatsViewModelFactory(app.focusRepository)
    }

    private val diaryViewModel: DiaryViewModel by viewModels {
        val app = application as FocusFrogApplication
        DiaryViewModelFactory(app.focusRepository)
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled gracefully
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS runtime permission on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val app = application as FocusFrogApplication

        // Manage Daily Reminder scheduling according to notificationsEnabled setting
        lifecycleScope.launch {
            app.userPreferencesRepository.notificationsEnabled.collectLatest { isEnabled ->
                if (isEnabled) {
                    DailyReminderScheduler.scheduleDailyReminder(this@MainActivity)
                } else {
                    DailyReminderScheduler.cancelDailyReminder(this@MainActivity)
                }
            }
        }

        setContent {
            val equippedTheme by app.userPreferencesRepository.equippedTheme.collectAsState(initial = "Pond")

            FocusFrogTheme(equippedTheme = equippedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen(
                        timerViewModel = timerViewModel,
                        shopViewModel = shopViewModel,
                        statsViewModel = statsViewModel,
                        diaryViewModel = diaryViewModel
                    )
                }
            }
        }
    }
}
