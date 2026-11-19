package com.lifecore.app.automation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReminderRulesTest {
    @Test
    fun acceptsMinimumAndMaximumDelay() {
        assertEquals(1L, parseReminderDelayMinutes("1"))
        assertEquals(10080L, parseReminderDelayMinutes("10080"))
    }

    @Test
    fun rejectsOutOfRangeAndInvalidValues() {
        assertNull(parseReminderDelayMinutes("0"))
        assertNull(parseReminderDelayMinutes("10081"))
        assertNull(parseReminderDelayMinutes("later"))
        assertNull(parseReminderDelayMinutes(""))
    }

    @Test
    fun trimsWhitespace() {
        assertEquals(15L, parseReminderDelayMinutes(" 15 "))
    }
}
