# 🐸 Focus Frog — Product Roadmap & Milestones (v1.0 – v2.5 Complete)

> **App Pitch**: A focus timer where every completed session feeds and grows a virtual frog pet.

---

## 📌 Completed Milestones (v1.0 – v2.5) ✅

- [x] **v1.0 Basic Focus Timer**: Preset duration options (5m, 15m, 25m, 50m), countdown display, start/pause/reset controls, skip-to-break flow.
- [x] **v1.1 Frog Pet System**: Custom Compose Canvas vector renderer with 4 growth stages (*Tadpole, Froglet, Big Frog, Royal Frog*), 3 mood states (*Happy, Neutral, Hungry*), and happy-jump bounce animation.
- [x] **v1.2 Data Persistence**: Room Database (`SessionEntity`, `ShopItemEntity`, `UserStatsEntity`) & DataStore Preferences (`UserPreferencesRepository`). On-launch dynamic mood computation based on `lastSessionDate` vs current device calendar date.
- [x] **v1.3 Notifications**: Background-only session completion alerts and WorkManager daily evening reminder (18:00 local time) with smart skipping if a session was completed today.
- [x] **v1.4 Pond Shop & Navigation**: Material 3 Bottom Navigation Bar (Timer, Shop, Stats), Bug currency economy, purchasable accessories (*Hats, Sunglasses, Crown*), reactive theme switching (*Pond, Night Sky, Rainforest*), and live Canvas accessory overlays.
- [x] **v1.5 Stats & Streaks**: StatCards displaying current streak 🔥, best streak ⚡, today's sessions, lifetime sessions, and lifetime focus minutes with encouraging empty state.
- [x] **v2.1 Home-Screen Glance Widget**: `FocusFrogWidget` extending `GlanceAppWidget()` with offscreen `FrogBitmapRenderer` Canvas drawing, provider metadata, and auto-refreshing on session completion.
- [x] **v2.2 Custom Session Lengths & Break Settings**: Custom focus duration picker (1–120 minutes) with inline error validation, configurable break length slider (1–30 minutes) in Settings Dialog, and single-line `HH:MM:SS` timer formatting for 60+ minute durations.
- [x] **v2.3 Sound & Haptic Feedback**: Low-latency 16-bit 22.05kHz synthesized PCM WAV audio effects (*session complete chime, happy jump, coin purchase, thud failed, subtle equip pop*) + calm haptic feedback with DataStore toggles.
- [x] **v2.4 Frog Diary (Session History)**: Dedicated session history screen accessible from Stats, grouped by day (*"Today", "Yesterday", or formatted dates*) showing duration, timestamp, and bugs earned.
- [x] **v2.5 New Shop Content & Stage Rewards**: New accessories (*Wizard Hat 🧙, Bow Tie 🎀, Headphones 🎧, Tiny Leaf Umbrella ☂️*), new **Sunset Theme 🌅**, multi-accessory layer draw order, and stage evolution celebration dialog overlay.

---

## 🚀 v3.0 Roadmap & Future Feature Specifications

### 1. ☁️ Cloud Sync & Backup
- **Tech Stack**: Firebase Firestore / Supabase + Firebase Auth (Google Sign-In + Anonymous Auth)
- **Features**:
  - **Cross-Device Sync**: Syncs completed session history, lifetime minutes, streak records, owned shop items, and equipped theme across multiple devices.
  - **Offline-First Design**: Local Room DB remains the single source of truth. Changes queue locally and sync whenever an active internet connection is available.

---

### 2. 👥 Social Focus Rooms & Friends
- **Features**:
  - **Shared Ponds**: Create or join focus rooms with friends where everyone's frog pets sit together in a shared pond.
  - **Group Encouragement**: Send quick frog croaks / emojis to friends during active focus sessions.

---

### 3. 🛒 Google Play Store Launch Checklist

#### A. Console Setup & Compliance
- [ ] Register Google Play Developer Account.
- [ ] Create App Entry on Play Console (`com.example.focusfrog`).
- [ ] Set up Privacy Policy hosted page disclosing local storage (Room DB) and local notifications.

#### B. Store Listing Assets
- [ ] **App Icon**: High-resolution 512x512 PNG (Green Frog Face).
- [ ] **Feature Graphic**: 1024x500 PNG showcasing Frog Pet + Timer.
- [ ] **Screenshots**:
  1. Timer Screen with active Frog Pet & countdown.
  2. Pond Shop with equipped accessories & themes.
  3. Frog Diary session history log.
  4. Dark Mode / Sunset, Rainforest & Night Sky theme showcase.

#### C. Build & Signing Pipeline
- [ ] Generate Android App Bundle (`.aab`) via `./gradlew :app:bundleRelease`.
- [ ] Configure Release Signing Keystore in `app/build.gradle.kts`.
- [ ] Test on Google Play Internal Testing Track before public release.
