package com.lifecore.app.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.data.local.TaskItem
import com.lifecore.app.data.local.TaskStore
import com.lifecore.app.data.local.isValidTaskDate
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun PlanFocusScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember(context) { TaskStore(context) }
    var tasks by remember { mutableStateOf(store.loadTasks()) }
    var draftTitle by rememberSaveable { mutableStateOf("") }
    var draftDate by rememberSaveable { mutableStateOf("") }
    var dateError by rememberSaveable { mutableStateOf(false) }
    var showCompleted by rememberSaveable { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var remainingSeconds by rememberSaveable { mutableIntStateOf(25 * 60) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var editTaskTitle by rememberSaveable { mutableStateOf("") }
    var editTaskDate by rememberSaveable { mutableStateOf("") }
    var editTaskDateError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSeconds) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        } else if (remainingSeconds == 0) {
            isRunning = false
        }
    }

    if (editingTaskId != null) {
        AlertDialog(
            onDismissRequest = { editingTaskId = null },
            title = { Text("Edit task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTaskTitle,
                        onValueChange = { editTaskTitle = it },
                        label = { Text("Task name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editTaskDate,
                        onValueChange = {
                            editTaskDate = it
                            editTaskDateError = !isValidTaskDate(it)
                        },
                        label = { Text("Due date (YYYY-MM-DD, optional)") },
                        singleLine = true,
                        isError = editTaskDateError,
                        supportingText = {
                            if (editTaskDateError) Text("Enter a valid date or leave it blank.")
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = editTaskTitle.isNotBlank() && isValidTaskDate(editTaskDate),
                    onClick = {
                        val id = editingTaskId
                        if (id != null) {
                            tasks = tasks.map { task ->
                                if (task.id == id) task.copy(
                                    title = editTaskTitle.trim(),
                                    dueDate = editTaskDate.trim()
                                ) else task
                            }
                            store.saveTasks(tasks)
                        }
                        editingTaskId = null
                    }
                ) { Text("Save changes") }
            },
            dismissButton = {
                TextButton(onClick = { editingTaskId = null }) { Text("Cancel") }
            }
        )
    }

    if (pendingDeleteTaskId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteTaskId = null },
            title = { Text("Delete task?") },
            text = { Text("This task will be permanently removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = pendingDeleteTaskId
                    if (id != null) {
                        tasks = tasks.filterNot { it.id == id }
                        store.saveTasks(tasks)
                    }
                    pendingDeleteTaskId = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteTaskId = null }) { Text("Cancel") } }
        )
    }

    val visibleTasks = tasks.filter { showCompleted || !it.isCompleted }
    val today = LocalDate.now().toString()
    val overdueCount = tasks.count { !it.isCompleted && it.dueDate.isNotBlank() && it.dueDate < today }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Plan with intention",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Tasks, dates and focused work in one calm space.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Add a task")
            OutlinedTextField(
                value = draftTitle,
                onValueChange = { draftTitle = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Task name") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = draftDate,
                onValueChange = {
                    draftDate = it
                    dateError = !isValidTaskDate(it)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Due date (YYYY-MM-DD, optional)") },
                singleLine = true,
                isError = dateError,
                supportingText = {
                    if (dateError) Text("Enter a valid date such as 2026-12-31.")
                    else Text("Leave blank if this task has no due date.")
                }
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val cleanTitle = draftTitle.trim()
                    if (cleanTitle.isNotEmpty() && isValidTaskDate(draftDate)) {
                        tasks = listOf(
                            TaskItem(title = cleanTitle, dueDate = draftDate.trim())
                        ) + tasks
                        store.saveTasks(tasks)
                        draftTitle = ""
                        draftDate = ""
                        dateError = false
                    }
                },
                enabled = draftTitle.isNotBlank() && isValidTaskDate(draftDate),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save task") }
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Weekly calendar")
            Text(
                text = "Choose a day to see its scheduled tasks.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val startOfWeek = LocalDate.now().minusDays((LocalDate.now().dayOfWeek.value - 1).toLong())
                (0L..6L).forEach { offset ->
                    val date = startOfWeek.plusDays(offset)
                    val value = date.toString()
                    FilterChip(
                        selected = selectedDate == value,
                        onClick = { selectedDate = value },
                        label = {
                            Text("${date.dayOfWeek.name.take(3)} ${date.dayOfMonth}")
                        }
                    )
                }
            }
            val dateTasks = tasks.filter { it.dueDate == selectedDate }
            Text(
                text = selectedDate,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            if (dateTasks.isEmpty()) {
                Text(
                    text = "No tasks scheduled for this day.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                dateTasks.forEach { task ->
                    Text(
                        text = (if (task.isCompleted) "✓ " else "• ") + task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        LifeCoreCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeCoreSectionTitle("Task list")
                TextButton(onClick = { showCompleted = !showCompleted }) {
                    Text(if (showCompleted) "Hide completed" else "Show completed")
                }
            }
            Text(
                text = "${tasks.count { !it.isCompleted }} open • ${tasks.count { it.isCompleted }} completed • $overdueCount overdue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (visibleTasks.isEmpty()) {
                Text(
                    text = if (showCompleted) "No tasks yet. Add your first task above."
                    else "You're clear. Add a task or show completed items.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                visibleTasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { checked ->
                                tasks = tasks.map { if (it.id == task.id) it.copy(isCompleted = checked) else it }
                                store.saveTasks(tasks)
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (task.dueDate.isNotBlank()) {
                                Text(
                                    text = "Due ${task.dueDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        TextButton(onClick = {
                            editingTaskId = task.id
                            editTaskTitle = task.title
                            editTaskDate = task.dueDate
                            editTaskDateError = false
                        }) { Text("Edit") }
                        TextButton(onClick = { pendingDeleteTaskId = task.id }) { Text("Delete") }
                    }
                }
            }
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Focus center")
            Text(
                text = "A 25-minute focus session. Pause whenever you need; reset to start again.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.weight(1f)
                ) { Text(if (isRunning) "Pause" else if (remainingSeconds == 0) "Start again" else "Start") }
                TextButton(
                    onClick = {
                        isRunning = false
                        remainingSeconds = 25 * 60
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Reset") }
            }
            if (remainingSeconds == 0) {
                Text(
                    text = "Focus session complete. Take a short break.",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
