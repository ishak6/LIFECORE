package com.lifecore.app.data.local

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GoalHabitModelTest {
    @Test
    fun habitIsCompleteWhenItsCompletionDateIsToday() {
        val habit = HabitItem(title = "Read", completedDate = LocalDate.now().toString())
        assertTrue(habit.isCompletedToday())
    }

    @Test
    fun habitFromPreviousDayIsNotCompleteToday() {
        val yesterday = LocalDate.now().minusDays(1).toString()
        val habit = HabitItem(title = "Read", completedDate = yesterday)
        assertFalse(habit.isCompletedToday())
    }

    @Test
    fun newHabitStartsIncomplete() {
        val habit = HabitItem(title = "Walk")
        assertFalse(habit.isCompletedToday())
    }
}
