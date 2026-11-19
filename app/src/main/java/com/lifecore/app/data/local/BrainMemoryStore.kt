package com.lifecore.app.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class BrainEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val isJournal: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

class BrainMemoryStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lifecore_brain_memory",
        Context.MODE_PRIVATE
    )

    fun loadEntries(): List<BrainEntry> = runCatching {
        val array = JSONArray(preferences.getString(KEY_ENTRIES, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val tagsArray = item.optJSONArray("tags") ?: JSONArray()
                val tags = buildList {
                    for (tagIndex in 0 until tagsArray.length()) {
                        val tag = tagsArray.optString(tagIndex).trim()
                        if (tag.isNotEmpty()) add(tag)
                    }
                }
                val title = item.optString("title").trim()
                if (title.isNotEmpty()) {
                    add(
                        BrainEntry(
                            id = item.optString("id", UUID.randomUUID().toString()),
                            title = title,
                            body = item.optString("body"),
                            tags = tags.distinct(),
                            isJournal = item.optBoolean("isJournal", false),
                            createdAt = item.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = item.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
            }
        }.sortedByDescending { it.updatedAt }
    }.getOrDefault(emptyList())

    fun saveEntries(entries: List<BrainEntry>) {
        val array = JSONArray()
        entries.forEach { entry ->
            val tags = JSONArray()
            entry.tags.map(String::trim).filter(String::isNotEmpty).distinct().forEach(tags::put)
            array.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("title", entry.title.trim())
                    .put("body", entry.body)
                    .put("tags", tags)
                    .put("isJournal", entry.isJournal)
                    .put("createdAt", entry.createdAt)
                    .put("updatedAt", entry.updatedAt)
            )
        }
        preferences.edit().putString(KEY_ENTRIES, array.toString()).apply()
    }

    private companion object {
        const val KEY_ENTRIES = "entries_json"
    }
}
