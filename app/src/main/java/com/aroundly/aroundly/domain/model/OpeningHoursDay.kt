package com.aroundly.aroundly.domain.model

/** One row in the weekly schedule (e.g. Monday 9:00–17:00 or closed). */
data class OpeningHoursDay(
    val dayLabel: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val isClosed: Boolean = false
)
