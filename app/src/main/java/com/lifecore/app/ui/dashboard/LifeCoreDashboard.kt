package com.lifecore.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

private data class DashboardWidget(
    val id: String,
    val title: String,
    val detail: String,
    val availability: String
)

private val dashboardWidgets = listOf(
    DashboardWidget("daily", "Daily overview", "A snapshot of today's tasks and priorities.", "Open Plan to review your saved tasks"),
    DashboardWidget("goals", "Goal progress", "Keep long-term goals visible as you plan your day.", "Open Goals to review your progress"),
    DashboardWidget("habits", "Habit tracker", "Build consistency with a simple daily check-in.", "Open Goals to check today's habits"),
    DashboardWidget("focus", "Focus center", "A dedicated space for distraction-free work.", "Open Plan to start a focus session"),
    DashboardWidget("calendar", "Calendar", "See upcoming events and important dates.", "Open Plan to view your weekly agenda"),
    DashboardWidget("notes", "Quick notes", "Capture a thought before it gets away.", "Open Brain to view and edit your notes"),
    DashboardWidget("memory", "Recent memory", "Return to journal entries and saved memories.", "Open Brain to review your journal"),
    DashboardWidget("projects", "Project status", "Keep active projects and next steps in view.", "Open Project Center to manage projects"),
    DashboardWidget("analytics", "Productivity insights", "Understand patterns in your completed work.", "Open Project Center for local activity summaries"),
    DashboardWidget("reminders", "Reminders", "Keep important follow-ups from slipping through.", "Open Profile to schedule a reminder")
)

private val defaultWidgetIds = listOf("daily", "goals", "habits", "focus", "calendar", "notes")

@Composable
fun LifeCoreDashboard(modifier: Modifier = Modifier, onOpenProjects: () -> Unit = {}) {
    var editMode by rememberSaveable { mutableStateOf(false) }
    var compactMode by rememberSaveable { mutableStateOf(false) }
    var visibleIds by rememberSaveable(
        stateSaver = listSaver(save = { it.toList() }, restore = { it })
    ) { mutableStateOf(defaultWidgetIds) }
    val visibleWidgets = visibleIds.mapNotNull { id -> dashboardWidgets.firstOrNull { it.id == id } }
    val availableWidgets = dashboardWidgets.filterNot { it.id in visibleIds }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Your life, in one place.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "A little clarity can change your whole day.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeCoreSectionTitle("Your dashboard")
                TextButton(onClick = { editMode = !editMode }) {
                    Text(if (editMode) "Done" else "Customize")
                }
            }
            if (editMode) {
                Text(
                    text = "Show, hide and reorder your widgets. Changes are saved for this app session and restored after Activity recreation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { compactMode = !compactMode }) {
                        Text(if (compactMode) "Comfortable" else "Compact")
                    }
                    OutlinedButton(onClick = { visibleIds = defaultWidgetIds }) {
                        Text("Reset")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        visibleIds = listOf("daily", "focus", "notes", "calendar")
                    }) { Text("Focus preset") }
                    TextButton(onClick = {
                        visibleIds = defaultWidgetIds
                    }) { Text("Balanced preset") }
                }
                if (availableWidgets.isNotEmpty()) {
                    Text(
                        text = "Add widgets",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    availableWidgets.forEach { widget ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(widget.title, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { visibleIds = visibleIds + widget.id }) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }

        if (visibleWidgets.isEmpty()) {
            LifeCoreCard {
                LifeCoreSectionTitle("Your dashboard is clear")
                Text(
                    text = "Add a widget to make this space useful to you.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        visibleWidgets.forEachIndexed { index, widget ->
            LifeCoreCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = widget.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (editMode) {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            TextButton(
                                enabled = index > 0,
                                onClick = {
                                    val next = visibleIds.toMutableList()
                                    val item = next.removeAt(index)
                                    next.add(index - 1, item)
                                    visibleIds = next
                                }
                            ) { Text("↑") }
                            TextButton(
                                enabled = index < visibleIds.lastIndex,
                                onClick = {
                                    val next = visibleIds.toMutableList()
                                    val item = next.removeAt(index)
                                    next.add(index + 1, item)
                                    visibleIds = next
                                }
                            ) { Text("↓") }
                            TextButton(onClick = {
                                visibleIds = visibleIds.filterNot { it == widget.id }
                            }) { Text("Hide") }
                        }
                    }
                }
                if (widget.id == "projects") {
                    TextButton(onClick = onOpenProjects) {
                        Text("Open project center")
                    }
                }
                if (!compactMode) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = widget.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = widget.availability,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
