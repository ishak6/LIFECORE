package com.lifecore.app
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle
import com.lifecore.app.ui.theme.LifeCoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LifeCorePreviewApp() }
    }
}

@Composable
private fun LifeCorePreviewApp() {
    var themeMode by rememberSaveable { mutableIntStateOf(2) }
    val systemDark = (LocalContext.current.resources.configuration.uiMode and
        Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    val useDarkTheme = when (themeMode) {
        0 -> false
        1 -> true
        else -> systemDark
    }
    LifeCoreTheme(darkTheme = useDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("LIFECORE", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text("Your life, in one place.", style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground)
                Text("A calm space to plan, remember and grow.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                LifeCoreCard {
                    LifeCoreSectionTitle("Appearance")
                    Text("Choose how LIFECORE looks on this device.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        ThemeChoice("Light", themeMode == 0) { themeMode = 0 }
                        ThemeChoice("Dark", themeMode == 1) { themeMode = 1 }
                        ThemeChoice("System", themeMode == 2) { themeMode = 2 }
                    }
                }
                LifeCoreCard {
                    LifeCoreSectionTitle("Design foundation")
                    Text("• Native Android interface with Jetpack Compose")
                    Text("• iOS-inspired spacing and rounded surfaces")
                    Text("• Consistent color, typography and shape tokens")
                    Text("• Light, dark and system appearance modes")
                }
                LifeCoreCard {
                    LifeCoreSectionTitle("Next modules")
                    Text("Goals • Habits • Brain • Planner • Focus")
                    Spacer(Modifier.height(8.dp))
                    Text("These modules are planned for later milestones and are not active yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = { themeMode = 2 }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )) {
                    Text("Use system appearance")
                }
            }
        }
    }
}

@Composable
private fun ThemeChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) Button(onClick = onClick) { Text(label) }
    else TextButton(onClick = onClick) { Text(label) }
}
