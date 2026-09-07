package com.example.focusfrog.util

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.focusfrog.FocusFrogApplication
import com.example.focusfrog.MainActivity
import com.example.focusfrog.R
import com.example.focusfrog.ui.components.FrogMood
import com.example.focusfrog.ui.components.FrogStage
import kotlinx.coroutines.flow.first

class FocusFrogWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val app = context.applicationContext as? FocusFrogApplication
        val focusRepo = app?.focusRepository
        val shopRepo = app?.shopRepository

        val stats = focusRepo?.getInitialUserStats()
        val completedToday = focusRepo?.getTodaySessionsCountFlow()?.first() ?: 0

        val totalSessions = stats?.totalSessions ?: 0
        val bugsBalance = stats?.bugsBalance ?: 0
        val currentStreak = stats?.currentStreak ?: 0
        val moodStr = stats?.frogMood ?: "NEUTRAL"

        val frogStage = when {
            totalSessions >= 100 -> FrogStage.ROYAL_FROG
            totalSessions >= 30 -> FrogStage.BIG_FROG
            totalSessions >= 10 -> FrogStage.FROGLET
            else -> FrogStage.TADPOLE
        }

        val frogMood = try {
            FrogMood.valueOf(moodStr)
        } catch (_: Exception) {
            FrogMood.NEUTRAL
        }

        val allItems = shopRepo?.allShopItems?.first() ?: emptyList()
        val hasHat = allItems.any { it.type == "HAT" && it.isEquipped }
        val hasSunglasses = allItems.any { it.type == "SUNGLASSES" && it.isEquipped }
        val hasCrown = allItems.any { it.type == "CROWN" && it.isEquipped }

        val frogBitmap: Bitmap = FrogBitmapRenderer.renderFrogBitmap(
            stage = frogStage,
            mood = frogMood,
            hasHat = hasHat,
            hasSunglasses = hasSunglasses,
            hasCrown = hasCrown,
            widthPx = 180,
            heightPx = 180
        )

        val stageText = when (frogStage) {
            FrogStage.TADPOLE -> context.getString(R.string.stage_tadpole)
            FrogStage.FROGLET -> context.getString(R.string.stage_froglet)
            FrogStage.BIG_FROG -> context.getString(R.string.stage_big_frog)
            FrogStage.ROYAL_FROG -> context.getString(R.string.stage_royal_frog)
        }

        val moodText = when (frogMood) {
            FrogMood.HAPPY -> context.getString(R.string.mood_happy)
            FrogMood.NEUTRAL -> context.getString(R.string.mood_neutral)
            FrogMood.HUNGRY -> context.getString(R.string.mood_hungry)
        }

        provideContent {
            WidgetContent(
                frogBitmap = frogBitmap,
                stageText = stageText,
                moodText = moodText,
                completedToday = completedToday,
                streakDays = currentStreak,
                bugs = bugsBalance
            )
        }
    }

    @Composable
    private fun WidgetContent(
        frogBitmap: Bitmap,
        stageText: String,
        moodText: String,
        completedToday: Int,
        streakDays: Int,
        bugs: Int
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = Color(0xFFFFF8E1), night = Color(0xFF1B2B20)))
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(frogBitmap),
                    contentDescription = "Focus Frog Pet",
                    modifier = GlanceModifier.size(80.dp)
                )

                Spacer(modifier = GlanceModifier.width(12.dp))

                Column {
                    Text(
                        text = stageText,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(day = Color(0xFF33691E), night = Color(0xFFDCF6BD))
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(2.dp))

                    Text(
                        text = moodText,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = ColorProvider(day = Color(0xFF1C1D1B), night = Color(0xFFE2E3DE))
                        )
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Text(
                        text = "Today: $completedToday | 🔥 $streakDays d | 🪰 $bugs",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(day = Color(0xFF558B2F), night = Color(0xFF7CB342))
                        )
                    )
                }
            }
        }
    }
}

class FocusFrogWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FocusFrogWidget()
}
