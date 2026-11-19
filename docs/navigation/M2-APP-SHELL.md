# M2 — Native Main Navigation & App Shell

## Scope
- Native Kotlin + Jetpack Compose application shell.
- Five primary destinations: Home, Goals, Brain, Plan, Profile.
- Persistent-in-process selected-tab state using Compose saveable state.
- Shared brand header and Material 3 bottom navigation.
- Honest placeholder states for modules that are scheduled for later milestones.
- Home landing content with explicit empty-state copy; no fabricated user data.

## Files
- `app/src/main/java/com/lifecore/app/MainActivity.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`

## Acceptance criteria
- App launches into Home without a WebView.
- All five tabs are visible and selectable.
- Selected destination changes the screen content.
- Tab selection survives Activity recreation through saveable Compose state.
- System bars and safe drawing insets do not overlap app content.
- Future modules are clearly marked as not yet implemented.
- Debug APK assembles and unit tests pass in GitHub Actions.

## Verification
CI workflow: `.github/workflows/android-build.yml`
Build command: `gradle --no-daemon :app:assembleDebug :app:testDebugUnitTest`

## Known scope boundary
Goals, Brain, Plan, Profile data workflows and persistence are not implemented by M2. Their functional implementation belongs to later milestones. M2 provides navigation and honest destination states only.
