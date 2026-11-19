# M10 — Settings & Integrations

## Implemented
- Persistent Light, Dark and System theme selection.
- Theme preference is stored locally and applied at the application root.
- Profile hub includes Settings & Integrations.
- Integration status is explicit and truthful:
  - External calendar provider: not connected.
  - Cloud sync: not connected.
  - AI provider: not configured.
  - Website package activation: awaiting user-provided URL and secure backend.
- About section displays the app version.
- Privacy and data handling summary is shown in settings.
- No placeholder connection is presented as active.

## Files
- `app/src/main/java/com/lifecore/app/data/local/ThemeSettingsStore.kt`
- `app/src/main/java/com/lifecore/app/ui/settings/SettingsCenterScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/profile/ProfileCenterScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`
- `app/src/main/java/com/lifecore/app/MainActivity.kt`
- `app/src/test/java/com/lifecore/app/data/local/ThemeSettingsTest.kt`

## Acceptance criteria
- System theme follows Android appearance.
- Light and Dark modes override system appearance.
- Selection persists after app restart.
- Unknown stored values safely fall back to System.
- Integration status does not claim disconnected services are active.
- Debug APK assembles and unit tests pass in CI.

## Scope boundary
External calendar sync, cloud accounts, AI provider integrations and website activation are not configured because required external endpoints, credentials and permissions have not been provided. Settings displays their real status instead of simulating a successful connection.
