# LIFECORE

**LIFECORE is a native Android personal life operating system** for goals, habits, notes, journaling, tasks, focus, projects, reminders, privacy and local backup.

## Current Android implementation

The Android application is Kotlin + Jetpack Compose and does not use WebView.

- **Home:** customizable dashboard widgets, presets, compact mode and widget ordering.
- **Goals:** create/delete goals and track bounded progress.
- **Habits:** daily completion check-ins.
- **Brain:** searchable notes and journal entries with tags; edit and delete.
- **Plan:** local tasks, due dates, a seven-day agenda and a 25-minute focus timer.
- **Projects:** local project status management and summaries calculated from saved task/goal/habit data.
- **Automation:** WorkManager-based one-time reminders with Android notification permission handling.
- **Privacy Vault:** AES-GCM encryption with an Android Keystore key and strong biometric authentication.
- **Backup:** JSON export/import for goals, habits, notes, journal entries, tasks and projects.
- **Packages:** BASIC, ADVANCED, ULTRA, SECURITY and FUTURE are represented in the app package center; all are free initially.
- **Settings:** persistent Light, Dark and System theme selection plus truthful integration status.

The app is local-first. The encrypted Privacy Vault is intentionally excluded from JSON backups. Exported backups contain readable personal text and must be stored securely.

## Build and test

The GitHub Actions workflow builds the debug APK, runs JVM unit tests, runs Android lint and assembles the release variant.

Local command (from repository root):

```bash
gradle --no-daemon :app:assembleDebug :app:testDebugUnitTest
```

M11 CI also runs:

```bash
gradle --no-daemon :app:lintDebug :app:assembleRelease
```

Artifacts:
- `lifecore-debug-apk`: installable debug APK for testing.
- `lifecore-release-unsigned-apk`: unsigned release APK; it must be signed before installation/distribution.

## Repository layout

The Android Gradle project currently uses the conventional root Gradle files and an `app/` module. The broader product plan reserves `website/`, `backend/`, `admin/` and `database/` for separate workstreams; those directories are not implemented yet.

```text
LIFECORE/
├── .github/workflows/
├── app/
│   └── src/
├── docs/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Not connected / not released

- Website, backend, database and admin dashboard are separate planned workstreams.
- Cloud sync, external calendar integration and AI provider connections are not configured.
- Website package activation is not connected because no website URL or secure backend has been supplied.
- No signing key or GitHub signing secrets were supplied; CI does not create a signed production APK.
- Physical-device testing is still required for notification delivery, biometric vault behavior, app lifecycle, screen sizes and compatibility.

## Product principles

- Native Android UI; WebView prohibited.
- No Android ads, coins or subscription paywall.
- Local data stays on-device unless the user exports a backup.
- Never claim an integration or capability is active until it is implemented and verified.
- Do not merge to `main` or publish a production release without explicit approval.
