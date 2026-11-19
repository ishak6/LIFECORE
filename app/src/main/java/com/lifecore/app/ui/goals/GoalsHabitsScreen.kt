package com.lifecore.app.ui.goals

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.data.local.GoalItem
import com.lifecore.app.data.local.GoalsHabitsStore
import com.lifecore.app.data.local.HabitItem
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle
import java.time.LocalDate

@Composable
fun GoalsHabitsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember(context) { GoalsHabitsStore(context) }
    var goals by remember { mutableStateOf(store.loadGoals()) }
    var habits by remember { mutableStateOf(store.loadHabits()) }
    var goalDraft by remember { mutableStateOf("") }
    var habitDraft by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Make progress that matters",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Set a direction, then build small daily actions.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Goals")
            OutlinedTextField(
                value = goalDraft,
                onValueChange = { goalDraft = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("New goal") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val title = goalDraft.trim()
                    if (title.isNotEmpty()) {
                        goals = goals + GoalItem(title = title)
                        store.saveGoals(goals)
                        goalDraft = ""
                    }
                },
                enabled = goalDraft.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add goal") }
            Spacer(Modifier.height(12.dp))
            if (goals.isEmpty()) {
                Text(
                    text = "No goals yet. Add one meaningful outcome to get started.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                goals.forEach { goal ->
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(goal.title, modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleSmall)
                            TextButton(onClick = {
                                goals = goals.filterNot { it.id == goal.id }
                                store.saveGoals(goals)
                            }) { Text("Delete") }
                        }
                        Text(
                            text = "${goal.completedSteps} of ${goal.targetSteps} steps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = goal.completedSteps.toFloat(),
                            onValueChange = { value ->
                                goals = goals.map {
                                    if (it.id == goal.id) it.copy(
                                        completedSteps = value.toInt().coerceIn(0, goal.targetSteps)
                                    ) else it
                                }
                                store.saveGoals(goals)
                            },
                            valueRange = 0f..goal.targetSteps.toFloat()
                        )
                    }
                }
            }
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Daily habits")
            OutlinedTextField(
                value = habitDraft,
                onValueChange = { habitDraft = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("New habit") },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val title = habitDraft.trim()
                    if (title.isNotEmpty()) {
                        habits = habits + HabitItem(title = title)
                        store.saveHabits(habits)
                        habitDraft = ""
                    }
                },
                enabled = habitDraft.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add habit") }
            Spacer(Modifier.height(8.dp))
            if (habits.isEmpty()) {
                Text(
                    text = "No habits yet. Add a small action you want to repeat.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                habits.forEach { habit ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = habit.isCompletedToday(),
                            onCheckedChange = { checked ->
                                habits = habits.map {
                                    if (it.id == habit.id) it.copy(
                                        completedDate = if (checked) LocalDate.now().toString() else null
                                    ) else it
                                }
                                store.saveHabits(habits)
                            }
                        )
                        Text(habit.title, modifier = Modifier.weight(1f))
                        OutlinedButton(onClick = {
                            habits = habits.filterNot { it.id == habit.id }
                            store.saveHabits(habits)
                        }) { Text("Remove") }
                    }
                }
            }
        }
    }
}
