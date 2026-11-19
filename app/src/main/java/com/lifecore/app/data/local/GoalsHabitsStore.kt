package com.lifecore.app.data.local

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

data class GoalItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetSteps: Int = 10,
    val completedSteps: Int = 0
)

data class HabitItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completedDate: String? = null
) {
    fun isCompletedToday(): Boolean = completedDate == LocalDate.now().toString()
}

class GoalsHabitsStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lifecore_goals_habits",
        Context.MODE_PRIVATE
    )

    fun loadGoals(): List<GoalItem> = runCatching {
        val array = JSONArray(preferences.getString(KEY_GOALS, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    GoalItem(
                        id = item.optString("id", UUID.randomUUID().toString()),
                        title = item.optString("title"),
                        targetSteps = item.optInt("targetSteps", 10).coerceAtLeast(1),
                        completedSteps = item.optInt("completedSteps", 0).coerceAtLeast(0)
                    )
                )
            }
        }.filter { it.title.isNotBlank() }
    }.getOrDefault(emptyList())

    fun saveGoals(goals: List<GoalItem>) {
        val array = JSONArray()
        goals.forEach { goal ->
            array.put(
                JSONObject()
                    .put("id", goal.id)
                    .put("title", goal.title)
                    .put("targetSteps", goal.targetSteps)
                    .put("completedSteps", goal.completedSteps.coerceIn(0, goal.targetSteps))
            )
        }
        preferences.edit().putString(KEY_GOALS, array.toString()).apply()
    }

    fun loadHabits(): List<HabitItem> = runCatching {
        val array = JSONArray(preferences.getString(KEY_HABITS, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    HabitItem(
                        id = item.optString("id", UUID.randomUUID().toString()),
                        title = item.optString("title"),
                        completedDate = item.optString("completedDate").takeIf { it.isNotBlank() }
                    )
                )
            }
        }.filter { it.title.isNotBlank() }
    }.getOrDefault(emptyList())

    fun saveHabits(habits: List<HabitItem>) {
        val array = JSONArray()
        habits.forEach { habit ->
            array.put(
                JSONObject()
                    .put("id", habit.id)
                    .put("title", habit.title)
                    .put("completedDate", habit.completedDate ?: "")
            )
        }
        preferences.edit().putString(KEY_HABITS, array.toString()).apply()
    }

    private companion object {
        const val KEY_GOALS = "goals_json"
        const val KEY_HABITS = "habits_json"
    }
}
