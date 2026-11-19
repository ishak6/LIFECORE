package com.lifecore.app.ui.dashboard

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

private data class DashboardWidget(
    val id: String,
    val title: String,
    val detail: String,
    val availability: String,
    val destination: String
)

private val dashboardWidgets = listOf(
    DashboardWidget("daily", "Daily overview", "A snapshot of today's tasks and priorities.", "Tap to open today's plan", "plan"),
    DashboardWidget("goals", "Goal progress", "Keep long-term goals visible as you plan your day.", "Tap to manage goals and progress", "goals"),
    DashboardWidget("habits", "Habit tracker", "Build consistency with a simple daily check-in.", "Tap to check in to today's habits", "goals"),
    DashboardWidget("focus", "Focus center", "A dedicated space for distraction-free work.", "Tap to start or manage a focus session", "plan"),
    DashboardWidget("calendar", "Calendar", "See upcoming tasks in your weekly agenda.", "Tap to view your weekly agenda", "plan"),
    DashboardWidget("notes", "Quick notes", "Capture a thought before it gets away.", "Tap to create and search notes", "brain"),
    DashboardWidget("memory", "Recent memory", "Return to journal entries and saved memories.", "Tap to review your journal", "brain"),
    DashboardWidget("projects", "Project status", "Keep active projects and next steps in view.", "Tap to manage projects", "projects"),
    DashboardWidget("analytics", "Productivity insights", "Understand patterns in your completed work.", "Tap to review your project activity", "projects"),
    DashboardWidget("reminders", "Reminders", "Keep important follow-ups from slipping through.", "Tap to schedule or cancel reminders", "profile")
)

private val defaultWidgetIds = listOf("daily", "goals", "habits", "focus", "calendar", "notes")
private const val DASHBOARD_PREFS = "lifecore_dashboard"
private const val KEY_WIDGETS = "visible_widget_ids"
private const val KEY_COMPACT = "compact_mode"

@Composable
fun LifeCoreDashboard(
    modifier: Modifier = Modifier,
    onOpenDestination: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences(DASHBOARD_PREFS, Context.MODE_PRIVATE)
    }
    var editMode by rememberSaveable { mutableStateOf(false) }
    var compactMode by remember(preferences) {
        mutableStateOf(preferences.getBoolean(KEY_COMPACT, false))
    }
    var visibleIds by remember(preferences) {
        val saved = preferences.getString(KEY_WIDGETS, null)
        mutableStateOf(saved?.split(",")?.filter { id -> dashboardWidgets.any { it.id == id } }
            ?.distinct()?.ifEmpty { defaultWidgetIds } ?: defaultWidgetIds)
    }
    val visibleWidgets = visibleIds.mapNotNull { id -> dashboardWidgets.firstOrNull { it.id == id } }
    val availableWidgets = dashboardWidgets.filterNot { it.id in visibleIds }

    fun saveWidgets(ids: List<String>) {
        visibleIds = ids.distinct()
        preferences.edit().putString(KEY_WIDGETS, visibleIds.joinToString(",")).apply()
    }

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
                    text = "Show, hide, reorder and save your widgets on this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        compactMode = !compactMode
                        preferences.edit().putBoolean(KEY_COMPACT, compactMode).apply()
                    }) {
                        Text(if (compactMode) "Comfortable" else "Compact")
                    }
                    OutlinedButton(onClick = { saveWidgets(defaultWidgetIds) }) {
                        Text("Reset")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        saveWidgets(listOf("daily", "focus", "notes", "calendar"))
                    }) { Text("Focus preset") }
                    TextButton(onClick = { saveWidgets(defaultWidgetIds) }) {
                        Text("Balanced preset")
                    }
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
                            TextButton(onClick = { saveWidgets(visibleIds + widget.id) }) {
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
            LifeCoreCard(
                modifier = Modifier
                    .semantics { contentDescription = "Open ${widget.title}" }
                    .clickable(enabled = !editMode) { onOpenDestination(widget.destination) }
            ) {
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
                                    saveWidgets(next)
                                }
                            ) { Text("↑") }
                            TextButton(
                                enabled = index < visibleIds.lastIndex,
                                onClick = {
                                    val next = visibleIds.toMutableList()
                                    val item = next.removeAt(index)
                                    next.add(index + 1, item)
                                    saveWidgets(next)
                                }
                            ) { Text("↓") }
                            TextButton(onClick = {
                                saveWidgets(visibleIds.filterNot { it == widget.id })
                            }) { Text("Hide") }
                        }
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
