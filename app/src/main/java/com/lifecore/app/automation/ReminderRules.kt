package com.lifecore.app.automation

fun parseReminderDelayMinutes(raw: String): Long? =
    raw.trim().toLongOrNull()?.takeIf { it in 1L..10080L }
