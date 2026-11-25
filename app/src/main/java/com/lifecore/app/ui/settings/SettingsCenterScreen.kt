package com.lifecore.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lifecore.app.BuildConfig
import com.lifecore.app.data.local.LifeCoreThemeMode
import com.lifecore.app.ui.components.LifeCoreCard
import com.lifecore.app.ui.components.LifeCoreSectionTitle

private enum class SettingsCategory(val label: String, val symbol: String) {
    APPEARANCE("Appearance", "◐"),
    INTEGRATIONS("Integrations", "⌘"),
    PRIVACY("Privacy & data", "◇"),
    ABOUT("About", "ⓘ")
}

@Composable
fun SettingsCenterScreen(
    themeMode: LifeCoreThemeMode,
    onThemeModeChange: (LifeCoreThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by rememberSaveable { mutableStateOf(SettingsCategory.APPEARANCE) }
    val categories = remember { SettingsCategory.entries.toList() }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val wideLayout = maxWidth >= 720.dp

        if (wideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    modifier = Modifier.width(190.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    categories.forEach { category ->
                        val selected = selectedCategory == category
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable {
                                selectedCategory = category
                            },
                            shape = MaterialTheme.shapes.medium,
                            color = if (selected) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 13.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    category.symbol,
                                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    category.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SettingsHeader()
                    CategoryContent(
                        category = selectedCategory,
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsHeader()
                categories.forEach { category ->
                    CategoryContent(
                        category = category,
                        themeMode = themeMode,
                        onThemeModeChange = onThemeModeChange
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Settings & Integrations",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Control appearance, connections and privacy.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryContent(
    category: SettingsCategory,
    themeMode: LifeCoreThemeMode,
    onThemeModeChange: (LifeCoreThemeMode) -> Unit
) {
    when (category) {
        SettingsCategory.APPEARANCE -> AppearanceSettings(themeMode, onThemeModeChange)
        SettingsCategory.INTEGRATIONS -> IntegrationsSettings()
        SettingsCategory.PRIVACY -> PrivacySettings()
        SettingsCategory.ABOUT -> AboutSettings()
    }
}

@Composable
private fun AppearanceSettings(
    themeMode: LifeCoreThemeMode,
    onThemeModeChange: (LifeCoreThemeMode) -> Unit
) {
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
}

@Composable
private fun IntegrationsSettings() {
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
}

@Composable
private fun PrivacySettings() {
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
}

@Composable
private fun AboutSettings() {
    LifeCoreCard {
        LifeCoreSectionTitle("About")
        Text(
            "LIFECORE",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
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
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                status,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            detail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
