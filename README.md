# FurnitureFit 🛋️📏

**FurnitureFit** is a modern, native Android application built with **Kotlin** and **Jetpack Compose** that helps users verify whether furniture will fit into their rooms. It provides precise clearance calculations, architectural feature support, doorway delivery checks, interactive 2D floor plan visualizer, multi-item placement, and history tracking.

---

## 🌟 Key Features

- 📐 **Precision Fit Calculation**:
  - Auto-checks rotation (0° vs 90° orientation) to evaluate optimal fit.
  - Supports multiple measurement units: **Centimeters (cm)**, **Meters (m)**, **Inches (in)**, and **Feet (ft)**.
  - Wall measurement reference: Inside vs Outside wall boundaries.
  - Height & vertical clearance checking.

- 🚪 **Doorway & Access Path Analysis**:
  - **Doorway Delivery Check**: Verifies if furniture can pass through entryways in standard, sideways, or flat orientation.
  - **Clearance Management**: Configurable symmetric or custom side clearances (Left, Right, Front, Back).
  - Dedicated walking path and door access clearance reservation.

- 🗺️ **Interactive 2D Floor Plan Canvas**:
  - Dynamic visual canvas with pinch-to-zoom and pan gestures.
  - Customizable grid overlays (10 cm, 25 cm, 50 cm, 100 cm) with snap-to-grid capabilities.
  - Renders architectural features (doors with swing arcs and window openings).
  - Drag-and-drop / slider positioning for up to 10 placed furniture items.
  - Real-time collision detection (item-to-item, wall overhangs, and door swing obstructions).

- 📜 **History & Report Sharing**:
  - Offline local history storage for up to 10 recent calculation reports.
  - One-tap copy and system text sharing for fit breakdown reports.
  - Reload saved calculations directly back into the 2D floor plan layout.

- 🎨 **Modern Material 3 UI & Customization**:
  - Full support for **Material You Dynamic Colors** (Android 12+) and Dark/Light/System theme toggles.
  - Edge-to-edge layout implementation.
  - Presets for popular furniture types (King Bed, L-Shaped Sofa, Dining Table, Desk, Wardrobe, etc.).

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose with Material 3 Design
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Data Layer
- **State Management**: `StateFlow` & `collectAsStateWithLifecycle`
- **Navigation**: Jetpack Navigation Compose (`NavHost`)
- **Graphics**: Custom Compose `Canvas` with 2D transformation matrix rendering
- **Testing**: JUnit 4 unit tests for domain calculator logic
- **Build System**: Gradle Version Catalogs (`libs.versions.toml`) with Android Gradle Plugin 9.4+
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 37 (Android 15+)

---

## 📁 Project Structure

```
FurnitureFit/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/com/example/furniturefit/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── calculator/
│       │   │   │   │   ├── FurnitureFitCalculator.kt   # Core geometry & fit algorithms
│       │   │   │   │   └── UnitConverter.kt            # Multi-unit conversion helper
│       │   │   │   ├── model/                          # Data models (Room, Furniture, Clearance, etc.)
│       │   │   │   └── repository/
│       │   │   │       └── PreferencesRepository.kt   # SharedPreferences persistence
│       │   │   ├── ui/
│       │   │   │   ├── components/                     # Reusable Compose UI components
│       │   │   │   ├── navigation/                     # Screen routing
│       │   │   │   ├── screens/                        # App screens (Home, FloorPlan, History, Settings, About)
│       │   │   │   ├── theme/                          # Material 3 Theme, Typography, Colors
│       │   │   │   └── viewmodel/                      # FurnitureFitViewModel & UI State
│       │   │   └── AndroidManifest.xml
│       └── test/                                       # Comprehensive Unit Tests
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Ladybug / 2024.2+ recommended)
- **JDK**: Java 11 or higher
- **Android SDK Platform 37** installed

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/FurnitureFit.git
   cd FurnitureFit
   ```
2. Open the project in Android Studio.
3. Sync Gradle assets and dependencies (`Sync Project with Gradle Files`).
4. Select an Android Emulator or physical device running Android 8.0 (API 26) or higher.
5. Click **Run** (`Shift + F10`).

---

## 🧪 Running Unit Tests

Run the domain calculation unit tests using Gradle CLI:

```bash
./gradlew test
```

---

## 📄 License

```
Copyright (c) 2026 Abdullah Ateeq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
