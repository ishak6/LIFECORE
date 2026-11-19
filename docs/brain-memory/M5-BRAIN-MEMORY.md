# M5 — Brain & Personal Memory

## Implemented
- Native Brain tab integrated into the five-tab Compose app shell.
- Notes and Journal modes.
- Create, edit and delete entries.
- Entry title, body and comma-separated tags.
- Tag normalization removes blanks and duplicate tags.
- Search across title, body and tags within the selected mode.
- Timeline ordering by most recently updated.
- Local app-private SharedPreferences JSON persistence.
- Corrupt stored JSON falls back to an empty state rather than crashing.
- Editor drafts, selected mode and search query use saveable Compose state.

## Files
- `app/src/main/java/com/lifecore/app/data/local/BrainMemoryStore.kt`
- `app/src/main/java/com/lifecore/app/ui/brain/BrainMemoryScreen.kt`
- `app/src/main/java/com/lifecore/app/ui/shell/LifeCoreAppShell.kt`
- `app/src/test/java/com/lifecore/app/data/local/BrainEntryTest.kt`

## Acceptance criteria
- User can create, edit and delete notes and journal entries.
- Blank titles cannot be saved.
- Tags are trimmed, deduplicated and searchable.
- Search checks title, body and tags.
- Notes and Journal lists remain separate.
- Entries persist after Activity/process recreation.
- Malformed local JSON does not crash the screen.
- Editor draft state survives Activity recreation.
- Debug APK assembles and unit tests pass in CI.

## Scope boundary
Entries are currently text-only. Voice recordings, image attachments, folders, backlinks/graph visualization, encrypted vault storage, backup and cross-device sync are not implemented in M5 and remain subject to their planned modules and security design. Do not represent these as active features.
