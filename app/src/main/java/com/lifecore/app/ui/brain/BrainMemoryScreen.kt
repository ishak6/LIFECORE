package com.lifecore.app.ui.brain

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.data.local.BrainEntry
import com.lifecore.app.data.local.BrainMemoryStore
import com.lifecore.app.data.local.parseBrainTags
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle
import java.text.DateFormat
import java.util.Date

@Composable
fun BrainMemoryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember(context) { BrainMemoryStore(context) }
    var entries by remember { mutableStateOf(store.loadEntries()) }
    var journalMode by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    var tagsText by rememberSaveable { mutableStateOf("") }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val filteredEntries = entries.filter { entry ->
        entry.isJournal == journalMode &&
            (searchQuery.isBlank() ||
                entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.body.contains(searchQuery, ignoreCase = true) ||
                entry.tags.any { it.contains(searchQuery, ignoreCase = true) })
    }

    fun clearEditor() {
        title = ""
        body = ""
        tagsText = ""
        editingId = null
        showEditor = false
    }

    fun editEntry(entry: BrainEntry) {
        title = entry.title
        body = entry.body
        tagsText = entry.tags.joinToString(", ")
        editingId = entry.id
        showEditor = true
    }

    if (pendingDeleteId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("Delete entry?") },
            text = { Text("This note or journal entry will be permanently removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = pendingDeleteId
                    if (id != null) {
                        entries = entries.filterNot { it.id == id }
                        store.saveEntries(entries)
                        if (editingId == id) clearEditor()
                    }
                    pendingDeleteId = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteId = null }) { Text("Cancel") } }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (journalMode) "Personal memory" else "Your knowledge space",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Capture ideas, keep useful notes and reflect on your days.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !journalMode,
                onClick = { journalMode = false; clearEditor() },
                label = { Text("Notes") }
            )
            FilterChip(
                selected = journalMode,
                onClick = { journalMode = true; clearEditor() },
                label = { Text("Journal") }
            )
        }

        LifeCoreCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeCoreSectionTitle(if (showEditor) "Edit entry" else "Capture something")
                if (showEditor) {
                    TextButton(onClick = { clearEditor() }) { Text("Cancel") }
                }
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(if (journalMode) "Entry title" else "Note title") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Write here...") },
                minLines = 4
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tags (comma separated)") },
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    val cleanTitle = title.trim()
                    if (cleanTitle.isNotEmpty()) {
                        val now = System.currentTimeMillis()
                        val tags = parseBrainTags(tagsText)
                        val old = entries.firstOrNull { it.id == editingId }
                        val updated = BrainEntry(
                            id = old?.id ?: java.util.UUID.randomUUID().toString(),
                            title = cleanTitle,
                            body = body,
                            tags = tags,
                            isJournal = journalMode,
                            createdAt = old?.createdAt ?: now,
                            updatedAt = now
                        )
                        entries = if (old == null) listOf(updated) + entries
                        else entries.map { if (it.id == old.id) updated else it }
                        store.saveEntries(entries)
                        clearEditor()
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (editingId == null) "Save entry" else "Update entry") }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search title, text or tags") },
            singleLine = true
        )

        LifeCoreCard {
            LifeCoreSectionTitle(if (journalMode) "Journal timeline" else "Saved notes")
            if (filteredEntries.isEmpty()) {
                Text(
                    text = if (searchQuery.isBlank()) {
                        if (journalMode) "No journal entries yet. Write your first reflection."
                        else "No notes yet. Save an idea or something worth remembering."
                    } else "No matching entries. Try another search.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                filteredEntries.forEach { entry ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (entry.body.isNotBlank()) {
                            Text(
                                text = entry.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (entry.tags.isNotEmpty()) {
                            Text(
                                text = entry.tags.joinToString("  •  ") { "#$it" },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Updated ${DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(entry.updatedAt))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { editEntry(entry) }) { Text("Edit") }
                            TextButton(onClick = { pendingDeleteId = entry.id }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}
