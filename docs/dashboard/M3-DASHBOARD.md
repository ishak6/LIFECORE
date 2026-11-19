# M3 — Home & Dashboard Widgets

## Implemented in this milestone branch
- Home dashboard integrated into the native five-tab app shell.
- Dashboard widgets for daily overview, goals, habits, focus, calendar, quick notes, recent memory, projects, productivity insights and reminders.
- Customize mode with add, hide and reorder controls.
- Compact/comfortable display mode.
- Balanced and Focus presets plus reset to defaults.
- Widget selection/order is stored in saveable Compose state and survives Activity recreation.
- Honest empty states; no fabricated task counts, completion percentages or analytics.

## Files
- `app/src/main/java/com/lifecore/app/ui/dashboard/LifeCoreDashboard.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`

## Acceptance criteria
- Dashboard content scrolls within the app shell without covering the bottom navigation.
- Users can enter/exit customization mode.
- Users can add and hide available widgets.
- Users can reorder widgets and cannot move beyond list boundaries.
- Presets and reset restore known widget arrangements.
- Compact mode reduces widget detail without deleting data.
- Empty states clearly distinguish missing user data from unavailable functionality.
- Debug APK assembles and unit tests pass in CI.

## Scope boundary
Widget layout preferences are saved through Compose saveable state, not a durable database. Real goals, habits, tasks, calendar entries, notes, memories, reminders, project data and analytics are implemented in their respective later milestones. The Focus widget is a truthful placeholder until the Focus milestone.
