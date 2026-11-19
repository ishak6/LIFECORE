package com.lifecore.app.ui.shell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

private data class MainDestination(
    val label: String,
    val symbol: String,
    val title: String,
    val description: String
)

private val destinations = listOf(
    MainDestination("Home", "⌂", "Your life, in one place", "A calm space to plan, remember and grow."),
    MainDestination("Goals", "◎", "Make progress that matters", "Goal planning and progress tracking arrive in the Goals milestone."),
    MainDestination("Brain", "✧", "Your personal knowledge space", "Notes, links, folders and personal memory arrive in the Brain milestone."),
    MainDestination("Plan", "▦", "Make room for what matters", "Tasks, calendar and focus tools arrive in the Planning milestone."),
    MainDestination("Profile", "◉", "Your LIFECORE space", "Account, preferences and package controls will be added in later milestones.")
)

@Composable
fun LifeCoreAppShell() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val selected = destinations[selectedIndex]

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            Text(
                                text = destination.symbol,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        label = { Text(destination.label) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "LIFECORE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selected.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "LC",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            when (selectedIndex) {
                0 -> HomeLanding()
                else -> DestinationPlaceholder(destination = selected)
            }
        }
    }
}

@Composable
private fun HomeLanding() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Your life, in one place.",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "A little clarity can change your whole day.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LifeCoreCard {
            LifeCoreSectionTitle("Welcome to LIFECORE")
            Text(
                text = "Your personal life operating system is taking shape. Start with one small step.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LifeCoreCard {
            LifeCoreSectionTitle("Your daily overview")
            Text("No tasks or goals yet.")
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Goal, habit and task data will appear here when those modules are implemented.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DestinationPlaceholder(destination: MainDestination) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = destination.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        LifeCoreCard {
            LifeCoreSectionTitle("Coming in a later milestone")
            Text(
                text = destination.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
