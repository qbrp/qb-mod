package org.qbrp.core.game.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.qbrp.core.game.model.components.json.StateSerializer
import org.qbrp.core.game.model.objects.BaseObject

abstract class SerializeFabric<T : BaseObject, K: ObjectJsonField> {
    abstract fun toJson(t: T): K
    abstract fun fromJson(json: K): T

    companion object {
        val MAPPER = ObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            registerKotlinModule()
            registerModule(SimpleModule().apply {
                addSerializer(MutableMap::class.java, StateSerializer())
            })
            registerModule(GuavaModule())
        }
    }
}