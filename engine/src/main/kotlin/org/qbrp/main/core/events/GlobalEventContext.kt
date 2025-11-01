package org.qbrp.main.core.events

object GlobalEventContext : EventContext by Sync {
    object Sync : EventContext {
        override val eventBus: EventBus = SynchronousEventBus()
    }
}