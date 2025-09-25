package org.qbrp.main.core.events

import kotlin.reflect.KClass

@JvmInline
value class EventId(val value: String) {
    companion object {
        fun ofKclass(kClass: KClass<out Any>): EventId {
            return EventId(kClass.qualifiedName!!)
        }
    }
}