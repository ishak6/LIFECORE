# M6 — Tasks, Calendar & Focus

## Implemented
- Create and delete tasks.
- Mark tasks complete and reopen them.
- Optional ISO local due dates with strict date validation.
- Persist tasks in app-private SharedPreferences JSON.
- Open/completed/overdue task counts.
- Filter completed tasks.
- Weekly calendar strip with selectable days and per-day task agenda.
- 25-minute focus timer with start, pause and reset.
- Task editor drafts and selected calendar date use saveable Compose state.
- Corrupt task JSON falls back to an empty task list.

## Files
- `app/src/main/java/com/lifecore/app/data/local/TaskStore.kt`
- `app/src/main/java/com/lifecore/app/ui/plan/PlanFocusScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`
- `app/src/test/java/com/lifecore/app/data/local/TaskDateValidationTest.kt`

## Acceptance criteria
- Blank task names cannot be saved.
- Optional dates accept blank or valid ISO yyyy-MM-dd values only.
- Invalid dates are rejected without crashing.
- Task completion, reopening and deletion update persisted state.
- Weekly calendar selects seven dates and lists matching tasks.
- Focus timer starts, pauses, resets and stops at zero.
- Debug APK assembles and unit tests pass in CI.

## Scope boundary
The calendar is a local seven-day agenda, not a full month grid or external calendar sync. Focus sessions are in-app and do not run as a foreground service when the app is closed. Notifications, recurring tasks, calendar provider integration and background timer notifications are not implemented here.
