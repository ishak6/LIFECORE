package com.lifecore.app.ui.packages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.data.local.BackupSummary
import com.lifecore.app.data.local.LocalBackupManager
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

private data class PackageDisplay(
    val name: String,
    val description: String
)

private val packages = listOf(
    PackageDisplay("BASIC", "Core personal organization and daily planning."),
    PackageDisplay("ADVANCED", "Expanded productivity and customization tools."),
    PackageDisplay("ULTRA", "Advanced workflows and deeper personal insights."),
    PackageDisplay("SECURITY", "Privacy and device-protected information features."),
    PackageDisplay("FUTURE", "A reserved package area for future capabilities.")
)

@Composable
fun BackupPackagesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var status by rememberSaveable { mutableStateOf("") }
    var pendingImport by remember { mutableStateOf<String?>(null) }
    var showImportConfirmation by rememberSaveable { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) {
            status = "Backup export cancelled."
        } else {
            runCatching {
                val json = LocalBackupManager.exportJson(context)
                val stream = context.contentResolver.openOutputStream(uri)
                    ?: throw IllegalStateException("Could not open the selected backup file.")
                stream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            }.onSuccess {
                status = "Backup exported. Store the file somewhere safe."
            }.onFailure {
                status = it.message ?: "Backup export failed."
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            status = "Backup import cancelled."
        } else {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: throw IllegalStateException("Could not read the selected backup file.")
            }.onSuccess { json ->
                pendingImport = json
                showImportConfirmation = true
            }.onFailure {
                status = it.message ?: "Could not read backup file."
            }
        }
    }

    if (showImportConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showImportConfirmation = false
                pendingImport = null
            },
            title = { Text("Restore this backup?") },
            text = {
                Text("Restoring replaces the current local goals, habits, notes, journal entries, tasks and projects. Export a backup of current data first if you may need it.")
            },
            confirmButton = {
                TextButton(onClick = {
                    val json = pendingImport
                    if (json != null) {
                        runCatching { LocalBackupManager.restoreJson(context, json) }
                            .onSuccess { summary ->
                                status = "Restored: ${summary.goals} goals, ${summary.habits} habits, ${summary.brainEntries} notes/journal entries, ${summary.tasks} tasks and ${summary.projects} projects."
                            }
                            .onFailure { error ->
                                status = error.message ?: "Backup restore failed."
                            }
                    }
                    pendingImport = null
                    showImportConfirmation = false
                }) { Text("Replace local data") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingImport = null
                    showImportConfirmation = false
                }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Backup & Packages",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Keep a portable copy of your local records and review the LIFECORE package structure.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Local backup")
            Text(
                text = "Export or restore goals, habits, notes, journal entries, tasks and projects as a versioned JSON file.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { exportLauncher.launch("LIFECORE-backup.json") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Export backup") }
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json", "text/json", "*/*")) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Import backup") }
            Text(
                text = "Restore replaces the selected local records. The encrypted Privacy Vault, reminders and package activation data are excluded. Backup files contain readable personal text; protect the exported file.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
            if (status.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(status, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        LifeCoreCard {
            LifeCoreSectionTitle("LIFECORE packages")
            Text(
                text = "All five packages are planned as separate feature groups inside one Android APK. They are free initially; this app has no ads, coins or subscription paywall.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            packages.forEach { item ->
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.name, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Free initially", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    Text(item.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                text = "Website-based activation and key management are not connected. No activation key or external website is assumed.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
