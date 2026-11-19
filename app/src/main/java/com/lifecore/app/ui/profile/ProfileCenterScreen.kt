package com.lifecore.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle
import com.lifecore.app.ui.packages.BackupPackagesScreen
import com.lifecore.app.ui.security.AutomationVaultScreen
import com.lifecore.app.data.local.LifeCoreThemeMode
import com.lifecore.app.ui.settings.SettingsCenterScreen

@Composable
fun ProfileCenterScreen(
    themeMode: LifeCoreThemeMode,
    onThemeModeChange: (LifeCoreThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var section by rememberSaveable { mutableStateOf("MENU") }

    Column(modifier = modifier.fillMaxSize()) {
        if (section == "MENU") {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Your LIFECORE space",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                LifeCoreCard {
                    LifeCoreSectionTitle("Automation & Privacy")
                    Text(
                        text = "Schedule local reminders and protect private text with biometric encryption.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { section = "AUTOMATION" }, modifier = Modifier.fillMaxWidth()) {
                        Text("Open automation & vault")
                    }
                }
                LifeCoreCard {
                    LifeCoreSectionTitle("Settings & Integrations")
                    Text(
                        text = "Choose Light, Dark or System appearance and review connection status.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { section = "SETTINGS" }, modifier = Modifier.fillMaxWidth()) {
                        Text("Open settings")
                    }
                }
                LifeCoreCard {
                    LifeCoreSectionTitle("Backup & Packages")
                    Text(
                        text = "Export or restore your local records and review the five planned package groups.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { section = "BACKUP" }, modifier = Modifier.fillMaxWidth()) {
                        Text("Open backup & packages")
                    }
                }
            }
        } else {
            TextButton(onClick = { section = "MENU" }) { Text("← Profile") }
            when (section) {
                "AUTOMATION" -> AutomationVaultScreen(Modifier.weight(1f))
                "BACKUP" -> BackupPackagesScreen(Modifier.weight(1f))
                "SETTINGS" -> SettingsCenterScreen(themeMode, onThemeModeChange, Modifier.weight(1f))
            }
        }
    }
}
