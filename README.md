# MIRAE — Android Attestation Practice

A complete Kotlin + Jetpack Compose starter app built around the supplied
`Mirae_Informatika_Attestatsiya_Savollar_Bazasi` SQLite database.

## Open in Android Studio

Use a current Android Studio release with JDK 17.

The project is configured for:

- Android Gradle Plugin 9.4.1
- Gradle 9.6.0
- Kotlin 2.4.10
- Jetpack Compose BOM 2026.09.00
- compileSdk 37 / targetSdk 36
- minSdk 24

The versions were selected from the current Android documentation available when this project was generated.

## Main files

- `app/src/main/java/com/mirae/app/MainActivity.kt`
- `app/src/main/java/com/mirae/app/AppViewModel.kt`
- `app/src/main/java/com/mirae/app/data/MiraeRepository.kt`
- `app/src/main/java/com/mirae/app/data/Models.kt`
- `app/src/main/java/com/mirae/app/data/StatsStore.kt`
- `app/src/main/java/com/mirae/app/ui/MiraeTheme.kt`
- `app/src/main/java/com/mirae/app/ui/screens/MiraeApp.kt`
- `app/src/main/assets/mirae_questions.sqlite3`

## First build

1. Open the `MiraeApp` folder in Android Studio.
2. Let Gradle sync.
3. Install an Android 12+ emulator or connect a device.
4. Run the `app` configuration.

No internet connection is required at runtime for the supplied question bank.

## Data behavior

The database is bundled unchanged. On first launch the app copies it to private app storage and opens it read-only. The app queries:

- subjects
- topics
- active questions
- answer options
- explanations

Quiz results are stored locally in SharedPreferences.

## Manual expansion ideas

The architecture is intentionally easy to extend with:

- bookmarks/favorites
- question search
- timed exam mode
- official-slot mock exams
- per-topic analytics
- question review history
- cloud sync
- Firebase/Play Integrity
- user accounts
- PDF/source viewer
- Uzbek/Russian/English localization
- accessibility and tablet layouts
