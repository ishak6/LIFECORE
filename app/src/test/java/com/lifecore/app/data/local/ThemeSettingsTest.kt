package com.lifecore.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ThemeSettingsTest {
    @Test
    fun knownThemeModesRestore() {
        assertEquals(LifeCoreThemeMode.LIGHT, LifeCoreThemeMode.fromStored("LIGHT"))
        assertEquals(LifeCoreThemeMode.DARK, LifeCoreThemeMode.fromStored("DARK"))
        assertEquals(LifeCoreThemeMode.SYSTEM, LifeCoreThemeMode.fromStored("SYSTEM"))
    }

    @Test
    fun unknownOrMissingThemeDefaultsToSystem() {
        assertEquals(LifeCoreThemeMode.SYSTEM, LifeCoreThemeMode.fromStored("unknown"))
        assertEquals(LifeCoreThemeMode.SYSTEM, LifeCoreThemeMode.fromStored(null))
    }
}
