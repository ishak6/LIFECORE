package com.lifecore.app.ui.projects

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
import com.lifecore.app.data.local.GoalsHabitsStore
import com.lifecore.app.data.local.ProjectItem
import com.lifecore.app.data.local.calculateProgressPercent
import com.lifecore.app.data.local.ProjectStatus
import com.lifecore.app.data.local.ProjectStore
import com.lifecore.app.data.local.TaskStore
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

@Composable
fun ProjectsAnalyticsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val projectStore = remember(context) { ProjectStore(context) }
    val taskStore = remember(context) { TaskStore(context) }
    val goalsStore = remember(context) { GoalsHabitsStore(context) }
    var projects by remember { mutableStateOf(projectStore.loadProjects()) }
    val tasks = remember(context) { taskStore.loadTasks() }
    val goals = remember(context) { goalsStore.loadGoals() }
    val habits = remember(context) { goalsStore.loadHabits() }
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("ALL") }
    var pendingDeleteProjectId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingProjectId by rememberSaveable { mutableStateOf<String?>(null) }
    var editProjectName by rememberSaveable { mutableStateOf("") }
    var editProjectDescription by rememberSaveable { mutableStateOf("") }

    if (editingProjectId != null) {
        AlertDialog(
            onDismissRequest = { editingProjectId = null },
            title = { Text("Edit project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editProjectName,
                        onValueChange = { editProjectName = it },
                        label = { Text("Project name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editProjectDescription,
                        onValueChange = { editProjectDescription = it },
                        label = { Text("Description (optional)") },
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = editProjectName.isNotBlank(),
                    onClick = {
                        val id = editingProjectId
                        if (id != null) {
                            projects = projects.map { project ->
                                if (project.id == id) project.copy(
                                    name = editProjectName.trim(),
                                    description = editProjectDescription.trim(),
                                    updatedAt = System.currentTimeMillis()
                                ) else project
                            }
                            projectStore.saveProjects(projects)
                        }
                        editingProjectId = null
                    }
                ) { Text("Save changes") }
            },
            dismissButton = {
                TextButton(onClick = { editingProjectId = null }) { Text("Cancel") }
            }
        )
    }

    if (pendingDeleteProjectId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteProjectId = null },
            title = { Text("Delete project?") },
            text = { Text("This project and its local status record will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    val id = pendingDeleteProjectId
                    if (id != null) {
                        projects = projects.filterNot { it.id == id }
                        projectStore.saveProjects(projects)
                    }
                    pendingDeleteProjectId = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteProjectId = null }) { Text("Cancel") } }
        )
    }

    val filteredProjects = projects.filter {
        filter == "ALL" || it.status.name == filter
    }
    val completedTasks = tasks.count { it.isCompleted }
    val openTasks = tasks.size - completedTasks
    val goalProgress = calculateProgressPercent(
        goals.sumOf { it.completedSteps },
        goals.sumOf { it.targetSteps }
    )
    val habitsDoneToday = habits.count { it.isCompletedToday() }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Projects & insights",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Keep projects moving and understand your current activity.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Productivity overview")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Metric("Open tasks", openTasks.toString(), Modifier.weight(1f))
                Metric("Completed", completedTasks.toString(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Metric("Goal progress", "$goalProgress%", Modifier.weight(1f))
                Metric("Habits today", habitsDoneToday.toString(), Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Insights are calculated from saved local records on this device.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Create a project")
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Project name") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description (optional)") },
                minLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val cleanName = name.trim()
                    if (cleanName.isNotEmpty()) {
                        projects = listOf(
                            ProjectItem(name = cleanName, description = description.trim())
                        ) + projects
                        projectStore.saveProjects(projects)
                        name = ""
                        description = ""
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add project") }
        }

        LifeCoreCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LifeCoreSectionTitle("Project center")
                Text(
                    text = "${projects.count { it.status == ProjectStatus.ACTIVE }} active",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "ACTIVE", "PAUSED", "COMPLETED").forEach { value ->
                    FilterChip(
                        selected = filter == value,
                        onClick = { filter = value },
                        label = { Text(value.lowercase().replaceFirstChar(Char::uppercase)) }
                    )
                }
            }
            if (filteredProjects.isEmpty()) {
                Text(
                    text = if (projects.isEmpty()) "No projects yet. Add a project to get started."
                    else "No projects in this filter.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                filteredProjects.forEach { project ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (project.description.isNotBlank()) {
                            Text(
                                text = project.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = project.status.name.lowercase().replaceFirstChar(Char::uppercase),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                val next = when (project.status) {
                                    ProjectStatus.ACTIVE -> ProjectStatus.PAUSED
                                    ProjectStatus.PAUSED -> ProjectStatus.COMPLETED
                                    ProjectStatus.COMPLETED -> ProjectStatus.ACTIVE
                                }
                                projects = projects.map {
                                    if (it.id == project.id) it.copy(
                                        status = next,
                                        updatedAt = System.currentTimeMillis()
                                    ) else it
                                }
                                projectStore.saveProjects(projects)
                            }) { Text("Change status") }
                            TextButton(onClick = {
                                editingProjectId = project.id
                                editProjectName = project.name
                                editProjectDescription = project.description
                            }) { Text("Edit") }
                            TextButton(onClick = { pendingDeleteProjectId = project.id }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
