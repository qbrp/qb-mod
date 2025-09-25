package org.qbrp.main.engine.players.model.service

import kotlinx.serialization.encodeToString
import org.qbrp.main.core.components.serialization.Deserializer
import org.qbrp.main.core.components.serialization.Serialization
import org.qbrp.main.core.components.serialization.Serializer

import org.qbrp.main.engine.players.model.mapping.PlayerJsonField

class PlayerSerializer(): Serializer<PlayerJsonField>, Deserializer<PlayerJsonField> {
    override fun fromJson(json: String): PlayerJsonField {
        return Serialization.JSON.decodeFromString<PlayerJsonField>(json)
    }

    override fun toJson(t: PlayerJsonField): String {
        return Serialization.JSON.encodeToString(t)
    }
}