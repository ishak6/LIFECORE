package com.lifecore.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.lifecore.app.data.local.LifeCoreThemeMode
import com.lifecore.app.data.local.ThemeSettingsStore
import com.lifecore.app.ui.shell.LifeCoreAppShell
import com.lifecore.app.ui.theme.LifeCoreTheme

class MainActivity : FragmentActivity() {
    private val themeSettingsStore by lazy { ThemeSettingsStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var storedMode by rememberSaveable {
                mutableStateOf(themeSettingsStore.loadThemeMode().name)
            }
            val themeMode = LifeCoreThemeMode.fromStored(storedMode)
            val useDarkTheme = when (themeMode) {
                LifeCoreThemeMode.SYSTEM -> isSystemInDarkTheme()
                LifeCoreThemeMode.LIGHT -> false
                LifeCoreThemeMode.DARK -> true
            }

            LifeCoreTheme(darkTheme = useDarkTheme) {
                LifeCoreAppShell(
                    themeMode = themeMode,
                    onThemeModeChange = { mode ->
                        storedMode = mode.name
                        themeSettingsStore.saveThemeMode(mode)
                    }
                )
            }
        }
    }
}
