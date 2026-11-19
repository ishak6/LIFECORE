package com.lifecore.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.BuildConfig
import com.lifecore.app.data.local.LifeCoreThemeMode
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

@Composable
fun SettingsCenterScreen(
    themeMode: LifeCoreThemeMode,
    onThemeModeChange: (LifeCoreThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Settings & Integrations",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Control the app appearance and review available connections.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Appearance")
            Text(
                text = "Choose how LIFECORE follows your device or uses a fixed theme.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LifeCoreThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { onThemeModeChange(mode) },
                        label = { Text(mode.name.lowercase().replaceFirstChar(Char::uppercase)) }
                    )
                }
            }
            Text(
                text = "Your selection is saved on this device.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Integrations")
            IntegrationStatus(
                "Calendar provider",
                "Not connected",
                "The in-app weekly calendar works locally. External calendar access is not enabled."
            )
            IntegrationStatus(
                "Cloud sync",
                "Not connected",
                "Your current records are stored locally. No cloud account or sync server is configured."
            )
            IntegrationStatus(
                "AI provider",
                "Not configured",
                "No AI provider or API key has been connected to this build."
            )
            IntegrationStatus(
                "Website package activation",
                "Awaiting setup",
                "No website URL or secure activation backend has been supplied or connected."
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("Privacy & data")
            Text(
                text = "Goals, habits, notes, journal entries, tasks and projects are stored locally on this device. Exported JSON backups contain readable personal text. The Privacy Vault uses Android Keystore and biometric-gated encryption.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "LIFECORE Android is designed without in-app advertising, coins or subscription paywalls.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LifeCoreCard {
            LifeCoreSectionTitle("About")
            Text("LIFECORE", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            Text(
                text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Native Android application • Local-first foundation",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IntegrationStatus(title: String, status: String, detail: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface)
            Text(status, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
        }
        Text(detail, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
