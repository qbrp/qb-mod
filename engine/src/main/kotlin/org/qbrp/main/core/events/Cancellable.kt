package org.qbrp.main.core.events

interface Cancellable {
    val cancelled: Boolean
    fun cancel()
}