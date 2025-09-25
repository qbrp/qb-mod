package org.qbrp.main.core.events.component

import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.events.EventContext

interface ComponentEventContext: EventContext {
    val eventSupportersStorage: EventSupportersStorage
}