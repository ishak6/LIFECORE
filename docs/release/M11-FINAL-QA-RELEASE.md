# M11 — Final QA & APK Release Gate

## Automated quality gate
The final Android CI workflow runs:
1. Debug APK assembly.
2. Local JVM unit tests.
3. Android lint on the debug variant.
4. Release APK assembly.
5. Upload of the installable debug APK.
6. Upload of the unsigned release APK.

## Artifacts
- `lifecore-debug-apk`: installable development/test APK.
- `lifecore-release-unsigned-apk`: unsigned release build output; cannot be installed until signed.

## Manual release checklist
- [ ] Install the debug APK on a physical Android device.
- [ ] Launch, background, resume and force-stop/relaunch the app.
- [ ] Verify Light, Dark and System themes.
- [ ] Navigate Home, Goals, Brain, Plan and Profile.
- [ ] Create/edit/delete goals, habits, notes, journal entries, tasks and projects.
- [ ] Verify each destructive delete action requires explicit confirmation.
- [ ] Verify local records survive process restart.
- [ ] Verify weekly task agenda and focus timer start/pause/reset.
- [ ] Verify reminder permission flow and notification delivery.
- [ ] Verify biometric vault create/unlock/save/lock/reset on a supported device.
- [ ] Verify vault plaintext is not saved in Activity state, the vault auto-locks on app background, and screenshots are blocked while unlocked.
- [ ] Export a backup, clear test data, restore and compare record counts.
- [ ] Verify malformed/unsupported backup files do not overwrite local data.
- [ ] Inspect app behavior offline.
- [ ] Verify accessibility labels, font scaling, contrast and small-screen layouts.
- [ ] Verify crash-free startup and navigation on supported Android API levels.
- [ ] Review privacy/security and release notes.

## Security hardening
- Vault plaintext is kept in non-saveable in-memory Compose state, cleared on screen disposal and app background, and the window uses `FLAG_SECURE` while the vault is unlocked.
- These protections reduce accidental exposure but do not replace OS/device security or physical-device penetration testing.

## Release limitations
- A signed production APK requires a protected signing key and GitHub Actions secrets. No signing credentials were supplied, so CI intentionally produces an unsigned release APK and an installable debug APK.
- Physical-device testing cannot be completed by CI alone; it must be performed on target devices.
- Website, backend and admin dashboard are separate W/B workstreams and are not completed by Android M0–M11.
- External calendar, cloud sync, AI provider and website package activation remain disconnected until the required endpoints and credentials are supplied.
- No production release or merge to `main` is performed without explicit approval.

## Completion policy
A milestone is complete only after its current branch's CI passes and its manual/device-only limitations are recorded. A successful CI run does not by itself prove physical-device behavior.
