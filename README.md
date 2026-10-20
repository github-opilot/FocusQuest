# FocusQuest

FocusQuest is an Android learning app that turns correct answers into available screen time. The app interface is in French and is built with Kotlin, Jetpack Compose, and Material 3.

## Features

### Learning path

After the onboarding shown on first launch, students can follow this path:

**Home → grade → subject → chapter → activity → quiz → results**

- The catalog is loaded from the app's JSON files and covers 6e, 5e, 4e, 3e, Seconde, Première générale, and Terminale générale.
- Only subjects available for the selected grade are shown. In Première and Terminale, subjects are grouped into common courses, specializations, and electives.
- Chapters are grouped by theme. Each chapter shows its available quiz count; chapters without questions remain visible but cannot be started.
- The only activity currently available is a **quiz with up to five questions**. A quiz is restricted to the selected grade, subject, and chapter. It uses fewer than five questions when that is all the chapter contains, and it does not repeat questions within a mission.
- The quiz shows progress, score, and explanations. A submitted answer cannot award its reward more than once. Each correct answer currently adds **30 seconds** to earned time, up to 2 minutes 30 seconds for a perfect mission. The results screen summarizes the mission.
- Back buttons return to previous steps; leaving an in-progress mission requires confirmation.

### Available time and app usage tracking

- The home screen shows available time, earned time, and time used today.
- The default daily base is **30 minutes**. In Settings, it can be changed to 15, 30, 45, or 60 minutes.
- The calculation is: **base time + earned time − used time**, with a minimum of zero.
- FocusQuest can list launchable apps on the phone, including their names and system icons. Users choose which apps to track, and their selection is saved.
- With Android's “Usage access” permission, FocusQuest reads the foreground time of selected apps through `UsageStatsManager` and deducts it from the available balance. When an app is selected for the first time that day, usage reported by Android since midnight is included.
- Usage is refreshed when FocusQuest launches, returns to the foreground, or when the user taps **Refresh used time**. Tracking is not continuous: apps remain launchable and FocusQuest **does not block them**.
- Earned and used time reset when the day changes; the base time and selected apps are stored locally with Preferences DataStore.

### Onboarding and navigation

Onboarding appears on first launch, and its completion state is saved. Later launches open the home screen directly. From home, students can start a mission, change their daily base time, manage tracked apps, or refresh usage data.

## Learning content

The JSON catalog currently contains:

| Grade | Grade-specific subject entries | Chapters | Chapters with quizzes | Questions |
|---|---:|---:|---:|---:|
| 6e | 12 | 66 | 16 | 80 |
| 5e | 15 | 82 | 22 | 110 |
| 4e | 22 | 91 | 34 | 170 |
| 3e | 22 | 98 | 35 | 175 |
| Seconde | 35 | 104 | 8 | 40 |
| Première générale | 35 | 132 | 8 | 40 |
| Terminale générale | 37 | 159 | 8 | 40 |
| **Total** | **178** | **732** | **131** | **655** |

Subject counts represent grade-specific entries, not the number of distinct subjects across the entire catalog. Not every chapter has quiz questions yet. Instrumented tests cover JSON structure, subject-to-chapter references, answer choices, and answer keys. This does not amount to pedagogical validation; teachers still need to review the content.

The **120 legacy Kotlin questions** (for 5e, 4e, and 3e) remain in `QuestionRepository` for compatibility. They are kept separate from the new catalog, and none are automatically assigned to a chapter. Any future mapping must be reviewed explicitly. The existing [question audit](docs/QUESTION_AUDIT.md) covers only this legacy bank, not the catalog's 655 questions.

## Current limitations

- FocusQuest tracks usage of selected apps, but does not block them or run as a permanent background monitoring service.
- Tracking requires the special Usage access permission in Android settings. Without it, app usage statistics cannot be read.
- The quiz is the only learning activity currently available. Chapters without questions can be viewed but not started.
- Teachers have not certified curriculum coverage, wording, or question difficulty.
- Data and catalogs are stored locally in the app. The project does not require Firebase or a remote server.

## Architecture and data

- `MainActivity.kt` starts the Compose app; `ui/FocusQuestApp.kt` connects onboarding, home, the catalog flow, quiz, and results.
- `data/catalog/CurriculumRepository.kt` loads subjects and chapters; `ChapterQuestionRepository.kt` loads chapter-linked quizzes from `app/src/main/assets/`.
- `data/QuestionRepository.kt` contains the 120-question legacy bank.
- `data/ScreenTimeRepository.kt` stores the time balance and app selection with Preferences DataStore; `UsageStatsRepository.kt` queries Android usage statistics.
- `data/OnboardingRepository.kt` stores onboarding completion state.

## Setup and commands

Requirements: Android Studio, JDK 17, and the Android SDK components required by the project. The app targets Android 36 and supports Android 7.0 (API 24) and later.

From the repository root:

```powershell
# Windows
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest  # requires a connected device or emulator
```

```bash
# macOS / Linux
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest  # requires a connected device or emulator
```

The debug APK is generated in `app/build/outputs/apk/debug/`. The project uses Gradle 9.3.1, Android Gradle Plugin 9.1.1, and Kotlin 2.2.10.
