package com.lifecore.app.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DesignTokenTest {
    @Test
    fun lightAndDarkBackgroundTokensAreDefined() {
        assertEquals(Color(0xFFF6F7FA), LifeCoreColors.LightBackground)
        assertEquals(Color(0xFF101116), LifeCoreColors.DarkBackground)
    }

    @Test
    fun lightAndDarkSurfaceTokensAreDefined() {
        assertEquals(Color(0xFFFFFFFF), LifeCoreColors.LightSurface)
        assertEquals(Color(0xFF1B1D25), LifeCoreColors.DarkSurface)
    }

    @Test
    fun brandAndSemanticTokensAreDefined() {
        assertEquals(Color(0xFF7667F5), LifeCoreColors.Primary)
        assertEquals(Color(0xFF45B8A6), LifeCoreColors.Secondary)
        assertEquals(Color(0xFF299D73), LifeCoreColors.Success)
        assertEquals(Color(0xFFE6A23C), LifeCoreColors.Warning)
        assertEquals(Color(0xFFD95757), LifeCoreColors.Error)
    }

    @Test
    fun primaryAndErrorColorsRemainDistinct() {
        assertNotEquals(LifeCoreColors.Primary, LifeCoreColors.Error)
    }
}
