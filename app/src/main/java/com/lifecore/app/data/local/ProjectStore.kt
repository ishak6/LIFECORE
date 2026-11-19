package com.lifecore.app.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class ProjectStatus { ACTIVE, PAUSED, COMPLETED }

fun parseProjectStatus(value: String): ProjectStatus = runCatching {
    ProjectStatus.valueOf(value)
}.getOrDefault(ProjectStatus.ACTIVE)

fun calculateProgressPercent(completed: Int, total: Int): Int =
    if (total <= 0) 0 else (completed.coerceAtLeast(0) * 100 / total).coerceIn(0, 100)

data class ProjectItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val status: ProjectStatus = ProjectStatus.ACTIVE,
    val updatedAt: Long = System.currentTimeMillis()
)

class ProjectStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lifecore_projects",
        Context.MODE_PRIVATE
    )

    fun loadProjects(): List<ProjectItem> = runCatching {
        val array = JSONArray(preferences.getString(KEY_PROJECTS, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val name = item.optString("name").trim()
                if (name.isNotEmpty()) {
                    val status = parseProjectStatus(item.optString("status", ProjectStatus.ACTIVE.name))
                    add(
                        ProjectItem(
                            id = item.optString("id", UUID.randomUUID().toString()),
                            name = name,
                            description = item.optString("description"),
                            status = status,
                            updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        }.sortedByDescending { it.updatedAt }
    }.getOrDefault(emptyList())

    fun saveProjects(projects: List<ProjectItem>) {
        val array = JSONArray()
        projects.forEach { project ->
            array.put(
                JSONObject()
                    .put("id", project.id)
                    .put("name", project.name.trim())
                    .put("description", project.description)
                    .put("status", project.status.name)
                    .put("updatedAt", project.updatedAt)
            )
        }
        preferences.edit().putString(KEY_PROJECTS, array.toString()).apply()
    }

    private companion object {
        const val KEY_PROJECTS = "projects_json"
    }
}
