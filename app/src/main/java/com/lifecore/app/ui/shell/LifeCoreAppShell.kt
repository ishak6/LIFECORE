package com.lifecore.app.ui.shell

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.ui.dashboard.LifeCoreDashboard
import com.lifecore.app.ui.goals.GoalsHabitsScreen
import com.lifecore.app.ui.brain.BrainMemoryScreen
import com.lifecore.app.ui.plan.PlanFocusScreen
import com.lifecore.app.ui.projects.ProjectsAnalyticsScreen
import com.lifecore.app.ui.profile.ProfileCenterScreen
import com.lifecore.app.data.local.LifeCoreThemeMode

private data class MainDestination(
    val label: String,
    val symbol: String
)

private val destinations = listOf(
    MainDestination("Home", "⌂"),
    MainDestination("Goals", "◎"),
    MainDestination("Brain", "✧"),
    MainDestination("Plan", "▦"),
    MainDestination("Profile", "◉")
)

@Composable
fun LifeCoreAppShell(
    themeMode: LifeCoreThemeMode,
    onThemeModeChange: (LifeCoreThemeMode) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    var showProjectsCenter by rememberSaveable { mutableStateOf(false) }
    val selected = destinations[selectedIndex]

    BackHandler(enabled = showProjectsCenter && selectedIndex == 0) {
        showProjectsCenter = false
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar {
                destinations.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index; showProjectsCenter = false },
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
                        text = if (showProjectsCenter && selectedIndex == 0) "Projects" else selected.label,
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

            if (showProjectsCenter && selectedIndex == 0) {
                TextButton(onClick = { showProjectsCenter = false }) { Text("← Back to Home") }
            }
            when (selectedIndex) {
                0 -> if (showProjectsCenter) ProjectsAnalyticsScreen(Modifier.weight(1f))
                     else LifeCoreDashboard(Modifier.weight(1f)) { destination ->
                         when (destination) {
                             "goals" -> selectedIndex = 1
                             "brain" -> selectedIndex = 2
                             "plan" -> selectedIndex = 3
                             "profile" -> selectedIndex = 4
                             "projects" -> showProjectsCenter = true
                         }
                     }
                1 -> GoalsHabitsScreen(Modifier.weight(1f))
                2 -> BrainMemoryScreen(Modifier.weight(1f))
                3 -> PlanFocusScreen(Modifier.weight(1f))
                4 -> ProfileCenterScreen(themeMode, onThemeModeChange, Modifier.weight(1f))
            }
        }
    }
}
