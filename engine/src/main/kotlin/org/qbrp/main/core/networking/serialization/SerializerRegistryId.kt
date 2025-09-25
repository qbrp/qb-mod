package org.qbrp.main.core.networking.serialization

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.Name
import kotlin.reflect.KClass

@JvmInline
value class SerializerRegistryId(val id: String) {
    constructor(kClass: KClass<*>): this(kClass.java.simpleName)
    companion object {
        fun ofComponent(type: String) = SerializerRegistryId(type)
        fun ofComponent(name: Name) = SerializerRegistryId(name.type)
    }
}