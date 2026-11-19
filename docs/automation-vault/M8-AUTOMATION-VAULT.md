# M8 — Automation Engine & Privacy Vault

## Implemented
- Schedule one pending local reminder through WorkManager.
- Replace an existing pending LIFECORE reminder when a new one is scheduled.
- Cancel the pending reminder.
- Request Android 13+ notification permission before scheduling.
- Notification worker creates a notification channel and posts a reminder when permission is still granted.
- Reminder delay is validated to 1–10,080 minutes.
- Private text vault uses AES/GCM encryption.
- AES key is generated and held in Android Keystore.
- Each create, unlock and save operation requires strong biometric authentication through BiometricPrompt and a Keystore-backed Cipher.
- Vault ciphertext is stored in app-private preferences; plaintext is held only while unlocked in the current screen state.
- Explicit, confirmed reset deletes encrypted payload and Keystore key.

## Files
- `app/src/main/java/com/lifecore/app/automation/ReminderWorker.kt`
- `app/src/main/java/com/lifecore/app/automation/ReminderRules.kt`
- `app/src/main/java/com/lifecore/app/security/VaultCrypto.kt`
- `app/src/main/java/com/lifecore/app/data/local/VaultStore.kt`
- `app/src/main/java/com/lifecore/app/ui/security/AutomationVaultScreen.kt`
- `app/src/main/java/com/lifecore/app/MainActivity.kt`
- `app/src/main/AndroidManifest.xml`

## Acceptance criteria
- Reminder input rejects blank title and delay outside 1–10,080 minutes.
- Notification permission is requested on Android 13+.
- New reminder replaces the previous unique reminder; cancel removes it.
- Vault data is encrypted with AES-GCM and an Android Keystore key.
- Vault cryptographic operations require strong biometric authentication.
- Plaintext is not written to the vault preferences.
- Vault reset requires explicit confirmation and deletes both payload and key.
- Unit tests cover reminder delay validation.
- Debug APK assembles and unit tests pass in CI.

## Security and platform limitations
- WorkManager schedules deferrable work; Android may delay delivery. This is not an exact alarm.
- If notification permission is revoked before execution, the worker fails without posting.
- Devices without enrolled strong biometrics cannot use the vault.
- Changing biometric enrollment can invalidate the Keystore key; existing vault content may become unrecoverable. Reset then permanently deletes it.
- The vault is encrypted at rest and biometric-gated for crypto operations; it is not a cloud backup or a substitute for device security.
