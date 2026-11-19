package com.lifecore.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class BrainEntryTest {
    @Test
    fun parsesCommaSeparatedTagsAndRemovesDuplicates() {
        assertEquals(
            listOf("work", "idea", "life"),
            parseBrainTags(" work, idea,work, , life ")
        )
    }

    @Test
    fun emptyTagInputProducesNoTags() {
        assertEquals(emptyList<String>(), parseBrainTags(" , , "))
    }

    @Test
    fun preservesTagsContainingSpaces() {
        assertEquals(
            listOf("personal growth", "reading list"),
            parseBrainTags("personal growth, reading list")
        )
    }
}
