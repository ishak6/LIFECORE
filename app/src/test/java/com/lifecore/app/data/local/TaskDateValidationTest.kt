package com.lifecore.app.data.local

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskDateValidationTest {
    @Test
    fun acceptsBlankOptionalDate() {
        assertTrue(isValidTaskDate(""))
        assertTrue(isValidTaskDate("   "))
    }

    @Test
    fun acceptsIsoLocalDate() {
        assertTrue(isValidTaskDate("2026-12-31"))
    }

    @Test
    fun rejectsMalformedOrImpossibleDates() {
        assertFalse(isValidTaskDate("31-12-2026"))
        assertFalse(isValidTaskDate("2026-02-30"))
        assertFalse(isValidTaskDate("tomorrow"))
    }
}
