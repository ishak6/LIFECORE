package com.lifecore.app.ui.security

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.lifecore.app.automation.ReminderWorker
import com.lifecore.app.automation.parseReminderDelayMinutes
import com.lifecore.app.data.local.VaultStore
import com.lifecore.app.security.VaultCrypto
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher

@Composable
fun AutomationVaultScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val vaultStore = remember(context) { VaultStore(context) }
    var reminderTitle by rememberSaveable { mutableStateOf("") }
    var reminderMessage by rememberSaveable { mutableStateOf("") }
    var delayMinutes by rememberSaveable { mutableStateOf("15") }
    var reminderStatus by rememberSaveable { mutableStateOf("") }
    var vaultText by remember { mutableStateOf("") }
    var vaultUnlocked by remember { mutableStateOf(false) }
    var vaultStatus by rememberSaveable { mutableStateOf("") }
    var showResetConfirmation by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                vaultText = ""
                vaultUnlocked = false
            }
        }
        activity?.lifecycle?.addObserver(observer)
        onDispose {
            activity?.lifecycle?.removeObserver(observer)
            vaultText = ""
            vaultUnlocked = false
        }
    }
    DisposableEffect(activity?.window, vaultUnlocked) {
        val window = activity?.window
        if (vaultUnlocked) window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        else window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose { window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }

    fun scheduleReminder() {
        val minutes = parseReminderDelayMinutes(delayMinutes)
        if (reminderTitle.isBlank() || minutes == null) {
            reminderStatus = "Enter a title and delay from 1 to 10,080 minutes."
            return
        }
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(minutes, TimeUnit.MINUTES)
            .setInputData(workDataOf(
                ReminderWorker.KEY_TITLE to reminderTitle.trim(),
                ReminderWorker.KEY_MESSAGE to reminderMessage.trim()
            ))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ReminderWorker.UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request
        )
        reminderStatus = "Reminder scheduled for about $minutes minute(s). Android may defer delivery."
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) scheduleReminder()
        else reminderStatus = "Notifications are disabled. Allow notifications to receive reminders."
    }

    fun requestReminderScheduling() {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        else scheduleReminder()
    }

    fun authenticateVault(action: String) {
        if (activity == null) {
            vaultStatus = "Secure authentication is unavailable in this screen."
            return
        }
        if (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) !=
            BiometricManager.BIOMETRIC_SUCCESS
        ) {
            vaultStatus = "Strong biometric authentication is not available or enrolled on this device."
            return
        }
        try {
            val storedPayload = if (action == ACTION_UNLOCK) {
                vaultStore.readEncryptedPayload() ?: throw IllegalStateException("No encrypted vault exists.")
            } else null
            val cipher: Cipher
            val ciphertext: ByteArray?
            if (action == ACTION_UNLOCK) {
                val decoded = VaultCrypto.decodePayload(storedPayload!!)
                cipher = VaultCrypto.createDecryptCipher(decoded.first)
                ciphertext = decoded.second
            } else {
                cipher = VaultCrypto.createEncryptCipher()
                ciphertext = null
            }

            val prompt = BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(code: Int, message: CharSequence) {
                        vaultStatus = message.toString()
                    }
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        try {
                            val authenticatedCipher = result.cryptoObject?.cipher
                                ?: throw IllegalStateException("Authenticated cipher unavailable.")
                            when (action) {
                                ACTION_CREATE -> {
                                    val encrypted = authenticatedCipher.doFinal(ByteArray(0))
                                    vaultStore.writeEncryptedPayload(
                                        VaultCrypto.encodePayload(authenticatedCipher.iv, encrypted)
                                    )
                                    vaultText = ""
                                    vaultUnlocked = true
                                    vaultStatus = "Encrypted vault created and unlocked."
                                }
                                ACTION_UNLOCK -> {
                                    vaultText = String(
                                        authenticatedCipher.doFinal(ciphertext!!),
                                        StandardCharsets.UTF_8
                                    )
                                    vaultUnlocked = true
                                    vaultStatus = "Vault unlocked."
                                }
                                ACTION_SAVE -> {
                                    val encrypted = authenticatedCipher.doFinal(
                                        vaultText.toByteArray(StandardCharsets.UTF_8)
                                    )
                                    vaultStore.writeEncryptedPayload(
                                        VaultCrypto.encodePayload(authenticatedCipher.iv, encrypted)
                                    )
                                    vaultStatus = "Vault encrypted and saved."
                                }
                            }
                        } catch (error: Exception) {
                            vaultUnlocked = false
                            vaultStatus = "Vault access failed. A biometric enrollment change may have invalidated its key; reset will permanently erase the vault."
                        }
                    }
                }
            )
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle("LIFECORE Privacy Vault")
                .setSubtitle(
                    when (action) {
                        ACTION_CREATE -> "Authenticate to create your encrypted vault"
                        ACTION_SAVE -> "Authenticate to encrypt and save your changes"
                        else -> "Authenticate to decrypt your private data"
                    }
                )
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancel")
                .build()
            prompt.authenticate(info, BiometricPrompt.CryptoObject(cipher))
        } catch (error: Exception) {
            vaultStatus = error.message ?: "Unable to start vault authentication."
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Reset privacy vault?") },
            text = { Text("This permanently deletes encrypted vault data and its device key. It cannot be recovered.") },
            confirmButton = {
                TextButton(onClick = {
                    vaultStore.deleteVault()
                    VaultCrypto.deleteKey()
                    vaultText = ""
                    vaultUnlocked = false
                    vaultStatus = "Vault reset. You can create a new vault."
                    showResetConfirmation = false
                }) { Text("Delete vault") }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Automation & Privacy", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
            Text("Schedule a reminder and protect private text with device biometrics.",
                style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LifeCoreCard {
            LifeCoreSectionTitle("Reminder automation")
            OutlinedTextField(value = reminderTitle, onValueChange = { reminderTitle = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Reminder title") }, singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = reminderMessage, onValueChange = { reminderMessage = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Message (optional)") }, minLines = 2)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = delayMinutes,
                onValueChange = { delayMinutes = it.filter(Char::isDigit).take(5) },
                modifier = Modifier.fillMaxWidth(), label = { Text("Delay in minutes (1–10,080)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { requestReminderScheduling() },
                enabled = reminderTitle.isNotBlank() && parseReminderDelayMinutes(delayMinutes) != null,
                modifier = Modifier.fillMaxWidth()) { Text("Schedule reminder") }
            OutlinedButton(onClick = {
                WorkManager.getInstance(context).cancelUniqueWork(ReminderWorker.UNIQUE_WORK_NAME)
                reminderStatus = "Pending LIFECORE reminder cancelled."
            }, modifier = Modifier.fillMaxWidth()) { Text("Cancel pending reminder") }
            if (reminderStatus.isNotBlank()) Text(reminderStatus, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LifeCoreCard {
            LifeCoreSectionTitle("Encrypted privacy vault")
            Text("AES-GCM encryption uses an Android Keystore key that requires strong biometric authentication for every cryptographic operation.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            if (!vaultStore.hasVault()) {
                Button(onClick = { authenticateVault(ACTION_CREATE) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Create encrypted vault")
                }
            } else if (!vaultUnlocked) {
                Button(onClick = { authenticateVault(ACTION_UNLOCK) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Unlock with biometrics")
                }
            } else {
                OutlinedTextField(value = vaultText, onValueChange = { vaultText = it },
                    modifier = Modifier.fillMaxWidth(), label = { Text("Private vault text") }, minLines = 5)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { authenticateVault(ACTION_SAVE) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Encrypt and save")
                }
                OutlinedButton(onClick = {
                    vaultText = ""
                    vaultUnlocked = false
                    vaultStatus = "Vault locked."
                }, modifier = Modifier.fillMaxWidth()) { Text("Lock vault") }
            }
            if (vaultStatus.isNotBlank()) Text(vaultStatus, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = { showResetConfirmation = true }) { Text("Reset and permanently delete vault") }
            Text("Biometric enrollment changes can invalidate the Keystore key and make existing vault data unrecoverable. Keep a separate secure backup.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
        }
    }
}

private const val ACTION_CREATE = "create"
private const val ACTION_UNLOCK = "unlock"
private const val ACTION_SAVE = "save"
