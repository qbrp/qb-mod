package org.qbrp.main.core.components.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.Component
import kotlin.reflect.KClass
import kotlin.lazy

object Serialization {
    val JSON by lazy {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            classDiscriminator = "identity"
        }
    }
}