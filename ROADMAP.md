# 🐸 Focus Frog — Product Roadmap & Future Architecture (v2.0+)

> **App Pitch**: A focus timer where every completed session feeds and grows a virtual frog pet.

---

## 📌 Current Status: v1.0 – v1.5 Complete ✅

- **v1.0 Basic Focus Timer**: 5m, 15m, 25m, 50m duration options, countdown display, start/pause/reset controls, skip to break flow.
- **v1.1 Frog Pet System**: Custom Compose Canvas vector renderer with 4 growth stages (Tadpole, Froglet, Big Frog, Royal Frog), 3 mood states (Happy, Neutral, Hungry), and happy-jump bounce animation.
- **v1.2 Data Persistence**: Room Database (`SessionEntity`, `ShopItemEntity`, `UserStatsEntity`) & DataStore Preferences (`UserPreferencesRepository`). On-launch dynamic mood computation based on `lastSessionDate` vs current device calendar date.
- **v1.3 Notifications**: Background-only session completion alerts and WorkManager daily evening reminder (18:00 local time) with smart skipping if a session was completed today.
- **v1.4 Pond Shop & Navigation**: Material 3 Bottom Navigation Bar (Timer, Shop, Stats), Bug currency economy, purchasable accessories (Hats, Sunglasses, Crown), reactive theme switching (Pond, Night Sky, Rainforest), and live Canvas accessory overlays.
- **v1.5 Stats & Streaks**: StatCards displaying current streak 🔥, best streak ⚡, today's sessions, lifetime sessions, and lifetime focus minutes with encouraging empty state.

---

## 🚀 v2.0 Roadmap & Future Feature Specifications

### 1. 📱 Home-Screen Widget (Jetpack Glance)
- **Tech Stack**: `androidx.glance:glance-appwidget`
- **Features**:
  - **Live Frog Display**: Renders current frog growth stage, mood expression, and equipped accessories.
  - **Quick Action**: "Start 25m Focus" button right from the Android home screen.
  - **Status Bar**: Displays current streak 🔥 and bug balance 🪰.
- **Implementation Strategy**:
  - `FocusFrogWidgetReceiver` and `GlanceAppWidget`.
  - Re-renders on `userStatsFlow` state updates or timer completion broadcast.

---

### 2. ☁️ Cloud Sync & Backup
- **Tech Stack**: Firebase Firestore / Supabase + Firebase Auth (Google Sign-In + Anonymous Auth)
- **Features**:
  - **Cross-Device Sync**: Syncs completed session history, lifetime minutes, streak records, owned shop items, and equipped theme across multiple devices.
  - **Offline-First Design**: Local Room DB remains the single source of truth. Changes queue locally and sync whenever an active internet connection is available.
  - **Conflict Resolution**:
    - `totalSessions` & `totalMinutes`: Sum / Max merging.
    - `bugsBalance`: Max value merging.
    - `ownedItems`: Union set of owned item IDs across local and remote databases.

---

### 3. 🎩 Cosmetic Packs & Exclusive Frog Skins
- **Monetization & Expansion**:
  - **Frog Skins**:
    - Golden Frog (Glow effect & golden particle trails)
    - Rainbow Frog (Cycling hue gradient)
    - Ninja Frog (Headband & shuriken accessory)
  - **Seasonal Accessory Packs**:
    - Winter Pack (Santa Hat, Earmuffs, Snow Theme)
    - Autumn Pack (Scarf, Falling Leaves Theme)
    - Party Pack (Party Hat, Party Blower)
  - **Rule**: All gameplay mechanics remain 100% free; cosmetics are purely optional.

---

### 4. 🛒 Google Play Store Launch Checklist

#### A. Console Setup & Compliance
- [ ] Register Google Play Developer Account ($25 one-time fee).
- [ ] Create App Entry on Play Console (`com.example.focusfrog` or production package ID).
- [ ] Set up Privacy Policy hosted page disclosing local storage (Room DB) and local notifications.

#### B. Store Listing Assets
- [ ] **App Icon**: High-resolution 512x512 PNG (Green Frog Face).
- [ ] **Feature Graphic**: 1024x500 PNG showcasing Frog Pet + Timer.
- [ ] **Screenshots** (minimum 4 for Phone & Tablet):
  1. Timer Screen with active Frog Pet & countdown.
  2. Pond Shop with equipped accessories & themes.
  3. Stats & Streaks dashboard.
  4. Dark Mode / Rainforest & Night Sky theme showcase.

#### C. Build & Signing Pipeline
- [ ] Generate Android App Bundle (`.aab`) via `./gradlew :app:bundleRelease`.
- [ ] Configure Release Signing Keystore in `app/build.gradle.kts` / `local.properties`.
- [ ] Verify ProGuard / R8 keep rules in `rules.keep` for Room and DataStore.
- [ ] Test on Google Play Internal Testing Track before public release.
