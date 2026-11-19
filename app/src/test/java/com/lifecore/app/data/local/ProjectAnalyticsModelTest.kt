package com.lifecore.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectAnalyticsModelTest {
    @Test
    fun progressIsZeroWhenThereIsNoTarget() {
        assertEquals(0, calculateProgressPercent(0, 0))
    }

    @Test
    fun progressIsBoundedToOneHundredPercent() {
        assertEquals(100, calculateProgressPercent(12, 10))
    }

    @Test
    fun progressCannotBeNegative() {
        assertEquals(0, calculateProgressPercent(-2, 10))
    }

    @Test
    fun unknownStoredStatusDefaultsToActive() {
        assertEquals(ProjectStatus.ACTIVE, parseProjectStatus("UNKNOWN"))
    }

    @Test
    fun parsesKnownProjectStatus() {
        assertEquals(ProjectStatus.PAUSED, parseProjectStatus("PAUSED"))
    }
}
