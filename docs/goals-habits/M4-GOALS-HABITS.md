# M4 — Goals & Habits

## Implemented
- Create and delete personal goals.
- Track goal progress with a bounded progress control.
- Create and delete daily habits.
- Mark habits complete for the current local calendar date and uncheck them.
- Persist goal and habit records locally in app-private SharedPreferences using JSON.
- Recover from malformed local JSON by presenting a safe empty state instead of crashing.

## Files
- `app/src/main/java/com/lifecore/app/data/local/GoalsHabitsStore.kt`
- `app/src/main/java/com/lifecore/app/ui/goals/GoalsHabitsScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`

## Acceptance criteria
- Empty goals/habits show clear guidance.
- Blank names cannot be submitted.
- New goals and habits appear immediately and persist after app recreation.
- Goal progress remains within 0..target.
- Habit completion is date-scoped to the device's local date.
- Delete/remove actions update UI and local storage.
- Corrupt stored JSON does not crash the screen.
- Debug APK and unit tests pass in CI.

## Scope boundary
This milestone uses app-private SharedPreferences as a small local persistence layer. Database migration, backup/restore, cross-device sync, reminders and advanced habit schedules are outside M4 and must be implemented only in their designated milestones.
