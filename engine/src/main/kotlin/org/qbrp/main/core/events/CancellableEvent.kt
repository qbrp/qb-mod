package org.qbrp.main.core.events

abstract class CancellableEvent : Event, Cancellable {
    final override var cancelled: Boolean = false
    override fun cancel() { cancelled = true }
}
