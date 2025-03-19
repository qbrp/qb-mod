package org.qbrp.engine.time

object TimeUtils {
    fun roundMinutesToNearestHalfHour(minutes: Int): Int {
        val remainder = minutes % 60

        return when {
            remainder < 15 -> minutes - remainder
            remainder < 45 -> minutes - remainder + 30
            else -> minutes - remainder + 60
        }
    }

    fun minutesToTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format("%02d:%02d", hours, mins)
    }
}