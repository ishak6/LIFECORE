package com.lifecore.app.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class BackupSummary(
    val goals: Int,
    val habits: Int,
    val brainEntries: Int,
    val tasks: Int,
    val projects: Int
)

object LocalBackupManager {
    private const val BACKUP_VERSION = 1

    fun exportJson(context: Context): String {
        val goalsStore = GoalsHabitsStore(context)
        val brainStore = BrainMemoryStore(context)
        val taskStore = TaskStore(context)
        val projectStore = ProjectStore(context)

        val root = JSONObject()
            .put("format", "LIFECORE_LOCAL_BACKUP")
            .put("version", BACKUP_VERSION)
            .put("createdAt", System.currentTimeMillis())

        val goals = JSONArray()
        goalsStore.loadGoals().forEach { item ->
            goals.put(JSONObject()
                .put("id", item.id)
                .put("title", item.title)
                .put("targetSteps", item.targetSteps)
                .put("completedSteps", item.completedSteps))
        }
        root.put("goals", goals)

        val habits = JSONArray()
        goalsStore.loadHabits().forEach { item ->
            habits.put(JSONObject()
                .put("id", item.id)
                .put("title", item.title)
                .put("completedDate", item.completedDate ?: ""))
        }
        root.put("habits", habits)

        val brain = JSONArray()
        brainStore.loadEntries().forEach { item ->
            val tags = JSONArray()
            item.tags.forEach { tag -> tags.put(tag) }
            brain.put(JSONObject()
                .put("id", item.id)
                .put("title", item.title)
                .put("body", item.body)
                .put("tags", tags)
                .put("isJournal", item.isJournal)
                .put("createdAt", item.createdAt)
                .put("updatedAt", item.updatedAt))
        }
        root.put("brainEntries", brain)

        val tasks = JSONArray()
        taskStore.loadTasks().forEach { item ->
            tasks.put(JSONObject()
                .put("id", item.id)
                .put("title", item.title)
                .put("dueDate", item.dueDate)
                .put("isCompleted", item.isCompleted)
                .put("createdAt", item.createdAt))
        }
        root.put("tasks", tasks)

        val projects = JSONArray()
        projectStore.loadProjects().forEach { item ->
            projects.put(JSONObject()
                .put("id", item.id)
                .put("name", item.name)
                .put("description", item.description)
                .put("status", item.status.name)
                .put("updatedAt", item.updatedAt))
        }
        root.put("projects", projects)

        return root.toString(2)
    }

    fun restoreJson(context: Context, json: String): BackupSummary {
        val root = JSONObject(json)
        require(root.optString("format") == "LIFECORE_LOCAL_BACKUP") {
            "This is not a LIFECORE backup file."
        }
        require(root.optInt("version", -1) == BACKUP_VERSION) {
            "Unsupported backup version."
        }

        val goals = parseGoals(root.optJSONArray("goals") ?: JSONArray())
        val habits = parseHabits(root.optJSONArray("habits") ?: JSONArray())
        val brainEntries = parseBrain(root.optJSONArray("brainEntries") ?: JSONArray())
        val tasks = parseTasks(root.optJSONArray("tasks") ?: JSONArray())
        val projects = parseProjects(root.optJSONArray("projects") ?: JSONArray())

        // Parse and validate the complete file before replacing any existing local records.
        val goalsStore = GoalsHabitsStore(context)
        goalsStore.saveGoals(goals)
        goalsStore.saveHabits(habits)
        BrainMemoryStore(context).saveEntries(brainEntries)
        TaskStore(context).saveTasks(tasks)
        ProjectStore(context).saveProjects(projects)

        return BackupSummary(goals.size, habits.size, brainEntries.size, tasks.size, projects.size)
    }

    private fun parseGoals(array: JSONArray): List<GoalItem> = buildList {
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            val title = item.optString("title").trim()
            if (title.isNotEmpty()) {
                val target = item.optInt("targetSteps", 10).coerceAtLeast(1)
                add(GoalItem(
                    id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                    title = title,
                    targetSteps = target,
                    completedSteps = item.optInt("completedSteps", 0).coerceIn(0, target)
                ))
            }
        }
    }

    private fun parseHabits(array: JSONArray): List<HabitItem> = buildList {
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            val title = item.optString("title").trim()
            if (title.isNotEmpty()) add(HabitItem(
                id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                title = title,
                completedDate = item.optString("completedDate").takeIf { it.isNotBlank() }
            ))
        }
    }

    private fun parseBrain(array: JSONArray): List<BrainEntry> = buildList {
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            val title = item.optString("title").trim()
            if (title.isNotEmpty()) {
                val tagArray = item.optJSONArray("tags") ?: JSONArray()
                val tags = buildList {
                    for (j in 0 until tagArray.length()) {
                        tagArray.optString(j).trim().takeIf { it.isNotEmpty() }?.let(::add)
                    }
                }.distinct()
                add(BrainEntry(
                    id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                    title = title,
                    body = item.optString("body"),
                    tags = tags,
                    isJournal = item.optBoolean("isJournal", false),
                    createdAt = item.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
                ))
            }
        }
    }

    private fun parseTasks(array: JSONArray): List<TaskItem> = buildList {
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            val title = item.optString("title").trim()
            if (title.isNotEmpty()) add(TaskItem(
                id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                title = title,
                dueDate = item.optString("dueDate"),
                isCompleted = item.optBoolean("isCompleted", false),
                createdAt = item.optLong("createdAt", System.currentTimeMillis())
            ))
        }
    }

    private fun parseProjects(array: JSONArray): List<ProjectItem> = buildList {
        for (i in 0 until array.length()) {
            val item = array.getJSONObject(i)
            val name = item.optString("name").trim()
            if (name.isNotEmpty()) add(ProjectItem(
                id = item.optString("id").ifBlank { UUID.randomUUID().toString() },
                name = name,
                description = item.optString("description"),
                status = parseProjectStatus(item.optString("status")),
                updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
            ))
        }
    }
}
