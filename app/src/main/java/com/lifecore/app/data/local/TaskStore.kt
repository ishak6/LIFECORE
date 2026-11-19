package com.lifecore.app.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

class TaskStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lifecore_tasks",
        Context.MODE_PRIVATE
    )

    fun loadTasks(): List<TaskItem> = runCatching {
        val array = JSONArray(preferences.getString(KEY_TASKS, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val title = item.optString("title").trim()
                if (title.isNotEmpty()) {
                    add(
                        TaskItem(
                            id = item.optString("id", UUID.randomUUID().toString()),
                            title = title,
                            dueDate = item.optString("dueDate"),
                            isCompleted = item.optBoolean("isCompleted", false),
                            createdAt = item.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        }.sortedWith(compareBy<TaskItem> { it.isCompleted }.thenBy { it.dueDate.ifBlank { "9999-12-31" } }.thenBy { it.createdAt })
    }.getOrDefault(emptyList())

    fun saveTasks(tasks: List<TaskItem>) {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title.trim())
                    .put("dueDate", task.dueDate)
                    .put("isCompleted", task.isCompleted)
                    .put("createdAt", task.createdAt)
            )
        }
        preferences.edit().putString(KEY_TASKS, array.toString()).apply()
    }

    private companion object {
        const val KEY_TASKS = "tasks_json"
    }
}

fun isValidTaskDate(value: String): Boolean {
    if (value.isBlank()) return true
    return runCatching { LocalDate.parse(value.trim()) }.isSuccess
}
