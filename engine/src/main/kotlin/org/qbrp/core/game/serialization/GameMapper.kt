package org.qbrp.core.game.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.qbrp.core.game.model.State
import org.qbrp.core.game.model.components.json.StateSerializer

object GameMapper: ObjectMapper() {
    private fun readResolve(): Any = GameMapper
    init {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        registerKotlinModule()
        registerModule(SimpleModule().apply {
            addSerializer(State::class.java, StateSerializer())
        })
        registerModule(GuavaModule())
    }
}