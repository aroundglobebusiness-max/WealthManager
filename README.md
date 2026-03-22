# Wealth Manager 💰
### Developed by Soorya × Claude

> "Your money. Your rules. Your Wealth."

---

## Features
- 🏠 Premium dark + gold glassmorphism UI
- 💸 Income & Expense tracking
- 📊 Wealth Score (0-100 financial health)
- 📈 Net Worth tracker (Assets & Liabilities)
- 🎯 Financial Goals with progress
- 🔄 Notion sync (manual token)
- 🧠 Smart suggestions from past transactions
- 📱 Home screen widget
- 📤 Offline sync queue
- 🎮 Haptic feedback
- ⚡ 120Hz optimized animations

---

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Database:** Room
- **Networking:** Retrofit + OkHttp
- **DI:** Hilt
- **Widget:** Jetpack Glance
- **Architecture:** MVVM + Clean Architecture

---

## Setup

### Step 1 — Android Studio
1. Install Android Studio from developer.android.com/studio
2. Open this project folder
3. Let it sync Gradle

### Step 2 — Run
1. Enable USB Debugging on Realme GT 6T
2. Connect via USB
3. Click Run ▶️

### Step 3 — Build APK
```bash
./gradlew assembleRelease
```
APK → `app/build/outputs/apk/release/app-release.apk`

---

## Notion Setup (In App)
1. Go to notion.so/my-integrations → Create integration
2. Open Settings in app → Paste token + DB ID
3. Tap "Save & Test Connection"

### Notion DB Columns needed:
| Column | Type |
|--------|------|
| Name | Title |
| Amount | Number |
| Type | Select |
| Category | Select |
| Currency | Text |
| Note | Text |
| Date | Date |

---

## Developer
**Soorya × Claude**
Built with ❤️ using Kotlin + Jetpack Compose
