package com.lifecore.app.data.local

import android.content.Context

enum class LifeCoreThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromStored(value: String?): LifeCoreThemeMode =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}

class ThemeSettingsStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lifecore_settings",
        Context.MODE_PRIVATE
    )

    fun loadThemeMode(): LifeCoreThemeMode =
        LifeCoreThemeMode.fromStored(preferences.getString(KEY_THEME_MODE, LifeCoreThemeMode.SYSTEM.name))

    fun saveThemeMode(mode: LifeCoreThemeMode) {
        preferences.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    private companion object {
        const val KEY_THEME_MODE = "theme_mode"
    }
}
