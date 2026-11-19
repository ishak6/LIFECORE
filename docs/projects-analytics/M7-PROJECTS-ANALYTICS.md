# M7 — Project Center & Analytics

## Implemented
- Create and delete projects with optional descriptions.
- Project lifecycle states: Active, Paused and Completed.
- Cycle project status with a single explicit action.
- Filter project list by status.
- Local app-private SharedPreferences JSON persistence.
- Project Center is reachable from the Home dashboard's Project Status widget.
- Productivity overview derived from actual local records:
  - Open and completed tasks.
  - Aggregate goal progress percentage.
  - Habits completed today.
- Progress percentage is bounded to 0–100 and handles an empty goal set.
- Unknown persisted project statuses safely fall back to Active.

## Files
- `app/src/main/java/com/lifecore/app/data/local/ProjectStore.kt`
- `app/src/main/java/com/lifecore/app/ui/projects/ProjectsAnalyticsScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/dashboard/LifeCoreDashboard.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`
- `app/src/test/java/com/lifecore/app/data/local/ProjectAnalyticsModelTest.kt`

## Acceptance criteria
- Project names cannot be blank.
- Project creation, status changes and deletion persist locally.
- Status filters return the correct subset.
- Project Center is reachable from the Home dashboard.
- Analytics use actual locally saved task, goal and habit data.
- Empty data produces valid zero values, not fabricated statistics.
- Unit tests cover progress bounds and status parsing.
- Debug APK assembles and unit tests pass in CI.

## Scope boundary
Analytics are local summaries, not predictive AI or cloud-wide analytics. No charts, project milestones/dependencies, team collaboration, cloud sync or server-side reporting are included in M7.
