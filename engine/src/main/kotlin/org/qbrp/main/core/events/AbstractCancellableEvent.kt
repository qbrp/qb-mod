package org.qbrp.main.core.events

abstract class AbstractCancellableEvent : Event, Cancellable {
    final override var cancelled: Boolean = false
    override fun cancel() { cancelled = true }
}
