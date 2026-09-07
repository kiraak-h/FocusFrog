# 🐸 Focus Frog

> **A focus timer where every completed session feeds and grows a virtual frog pet.**

Focus Frog is a feature-rich, offline-first Android productivity application built with Jetpack Compose (Material 3), Room Database, DataStore Preferences, Jetpack Glance, WorkManager, and SoundPool. Stay focused, earn bugs (currency), grow your virtual frog pet (`Lily`), customize your pond, and track your focus journey!

---

## ✨ Features (v1.0 – v2.5 Complete)

* ⏱️ **Focus Timer & Custom Durations (v1.0 & v2.2)**: Quick preset duration chips (5m, 15m, 25m, 50m) plus a **Custom Duration Picker (1–120 minutes)** with inline validation and persistent DataStore storage.
* 🐸 **Frog Pet System (v1.1)**: Custom Jetpack Compose Canvas vector renderer featuring 4 growth stages (*Tadpole, Froglet, Big Frog, Royal Frog*), 3 mood states (*Happy, Neutral, Hungry*), and reactive happy-jump bounce animations.
* 🎉 **Stage Evolution Celebration (v2.5)**: Interactive celebration dialog overlay, happy jump animation, and chime when your frog evolves to a new stage (*Tadpole $\rightarrow$ Froglet $\rightarrow$ Big Frog $\rightarrow$ Royal Frog*).
* 💾 **Data Persistence & Dynamic Mood (v1.2)**: Offline-first Room Database (`sessions`, `shop_items`, `user_stats`) & DataStore Preferences (`user_settings`). Your frog's mood dynamically computes on launch based on your last session date!
* 🔔 **Notifications & Reminders (v1.3)**: Background-only session completion alerts and smart daily evening reminders via WorkManager (18:00 local time) with Android 13+ permission support.
* 🛒 **Pond Shop & Accessory Layers (v1.4 & v2.5)**: Earn bugs by focusing (+10 bugs per completed session) and spend them on accessories (*Frog Hat, Wizard Hat 🧙, Cool Shades 🕶️, Bow Tie 🎀, Headphones 🎧, Tiny Leaf Umbrella ☂️, Royal Crown 👑*). Layered Canvas rendering supports equipping one item per slot category simultaneously!
* 🎨 **Custom Themes (v1.4 & v2.5)**: 4 palette-strict color schemes — **Pond Theme** (Cream/Dark), **Night Sky 🌃**, **Rainforest 🌿**, and **Sunset 🌅**.
* 🔥 **Stats & Streaks (v1.5)**: Live streak calculations (*current streak 🔥 & best streak ⚡*), today's sessions counter, total completed sessions, and lifetime focus minutes.
* 📖 **Frog Diary Session History (v2.4)**: Dedicated session history screen accessible from Stats, grouped by day (*"Today", "Yesterday", or formatted dates*) showing duration, timestamp, and bugs earned.
* 📱 **Home-Screen Widget (v2.1)**: Jetpack Glance AppWidget with offscreen bitmap Canvas rendering displaying live pet status, stage, mood, streak, and daily stats.
* 🔊 **Sound & Haptics (v2.3)**: Low-latency 16-bit PCM `SoundPool` audio effects (*session complete chime, happy jump, coin purchase, thud failed, subtle equip pop*) + calm haptic feedback with DataStore toggles.

---

## 🛠️ Architecture & Tech Stack

- **Language**: Kotlin 2.0.21
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Repository Pattern (`FocusRepository`, `ShopRepository`, `UserPreferencesRepository`)
- **Local Database**: Room Database 2.8.4 (`SessionDao`, `ShopDao`, `UserStatsDao`)
- **Preferences**: DataStore Preferences 1.1.2
- **Home-Screen Widget**: Jetpack Glance 1.1.1 (`FocusFrogWidget`, `FrogBitmapRenderer`)
- **Background Work**: WorkManager 2.10.0 (`DailyReminderWorker`, `DailyReminderScheduler`)
- **Audio & Haptics**: `SoundPool` (Synthesized 16-bit PCM WAV) + `VibratorManager`
- **Target SDK**: 37 | **Min SDK**: 28 | **AGP**: 9.3.2

---

## 🚀 Future Roadmap (v3.0+)

- ☁️ **Cloud Backup & Sync**: Cross-device syncing via Firebase Firestore and Google Sign-In.
- 👥 **Social Focus Rooms**: Focus together with friends in real-time virtual ponds.
- 🎮 **Frog Mini-Games**: Catch bugs in the pond for bonus rewards.
- 📱 **Google Play Store Launch**: Production release on Google Play.
