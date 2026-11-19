# M9 — Backup & Package Center

## Implemented
- Export versioned JSON backups through Android's system document picker.
- Import backups through the system document picker.
- Restore is confirmed before replacing local data.
- Backup contains goals, habits, notes, journal entries, tasks and projects.
- Entire backup is parsed before any local store is replaced.
- Invalid format or unsupported version is rejected.
- Backup restore reports counts for each restored record group.
- Profile hub links to Automation & Privacy and Backup & Packages.
- Package Center displays BASIC, ADVANCED, ULTRA, SECURITY and FUTURE.
- All packages are described as free initially; no Android ads, coins or subscription paywall.
- No website key generation or activation endpoint is assumed.

## Files
- `app/src/main/java/com/lifecore/app/data/local/LocalBackupManager.kt`
- `app/src/main/java/com/lifecore/app/ui/packages/BackupPackagesScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/profile/ProfileCenterScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`

## Acceptance criteria
- User can export a JSON backup to a chosen location.
- User can select a JSON backup for restore.
- Restore requires explicit confirmation.
- Invalid or unsupported backup files do not replace existing records.
- Successful restore reports record counts.
- Package Center lists all five approved package groups.
- No fake activation status or unprovided website integration is shown.
- Debug APK assembles and unit tests pass in CI.

## Data protection and scope
- The encrypted Privacy Vault is intentionally excluded from backup.
- Reminder scheduling state and future server package activation are excluded.
- Exported backup contains readable personal text; user is warned to protect the file.
- Restore replaces local records for the included stores; export current data first if needed.
- Cloud sync, encrypted backup archives, automatic scheduled backup and package key integration are not included.
