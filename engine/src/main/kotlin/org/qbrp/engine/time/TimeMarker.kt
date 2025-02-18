package org.qbrp.engine.time

class TimeMarker(val time: Int, private val onReached: (() -> Unit)) {
    fun execute() { onReached() }
}