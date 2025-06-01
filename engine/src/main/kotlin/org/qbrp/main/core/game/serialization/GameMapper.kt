package org.qbrp.main.core.game.serialization

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import org.qbrp.main.core.game.model.components.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

object GameMapper {
    private val serializers: MutableMap<KClass<out Component>, KSerializer<*>> = mutableMapOf()
    val serializersModule
        get() = SerializersModule {
            serializers.forEach { (klass, serializer) ->
                @Suppress("UNCHECKED_CAST")
                polymorphic(Component::class, klass as KClass<Component>, serializer as KSerializer<Component>)
            }
        }

    @OptIn(InternalSerializationApi::class)
    fun registerSerializer(klass: KClass<out Component>) {
        serializers[klass] = klass.serializer()
    }

    fun <T: Any> getDeserializer(klass: KClass<T>): DeserializationStrategy<T> {
        return COMPONENTS_JSON.serializersModule.serializer(klass.createType()) as DeserializationStrategy<T>
    }

    val COMPONENTS_JSON by lazy {
        Json {
            serializersModule = this@GameMapper.serializersModule
            classDiscriminator = "type"
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    val JSON by lazy {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
}