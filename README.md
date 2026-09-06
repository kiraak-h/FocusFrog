# 🐸 Focus Frog

> **A focus timer where every completed session feeds and grows a virtual frog pet.**

Focus Frog is an Android application designed to help you stay focused and productive. By completing focus sessions, you earn bugs (currency) and help your virtual frog pet grow through various stages!

## ✨ Features (v1.5)

*   ⏱️ **Focus Timer**: Customizable duration options (5m, 15m, 25m, 50m) with start/pause/reset controls and skip-to-break flow.
*   🐸 **Frog Pet System**: A dynamic, custom Compose Canvas vector renderer featuring 4 growth stages (Tadpole, Froglet, Big Frog, Royal Frog) and 3 mood states (Happy, Neutral, Hungry) with bounce animations.
*   💾 **Data Persistence**: Offline-first architecture using Room Database and DataStore Preferences. Your frog's mood dynamically computes based on your last session date!
*   🔔 **Notifications**: Background-only session completion alerts and smart daily evening reminders via WorkManager.
*   🛒 **Pond Shop & Customization**: Earn bugs by focusing and spend them on accessories (Hats, Sunglasses, Crown) or reactive themes (Pond, Night Sky, Rainforest).
*   🔥 **Stats & Streaks**: Track your current streak, best streak, today's sessions, lifetime sessions, and total lifetime focus minutes.

## 🛠️ Tech Stack

*   **Language**: Kotlin
*   **UI Toolkit**: Jetpack Compose (Material 3)
*   **Local Database**: Room Database
*   **Preferences**: Jetpack DataStore
*   **Background Work**: WorkManager

## 🚀 Upcoming Features (v2.0+)

*   📱 **Home-Screen Widget**: Live frog display and quick-action focus timer directly from your home screen (via Jetpack Glance).
*   ☁️ **Cloud Sync**: Firebase integration for cross-device syncing of your frog, stats, and shop items.
*   🎩 **More Cosmetics**: Exclusive frog skins and seasonal accessory packs!