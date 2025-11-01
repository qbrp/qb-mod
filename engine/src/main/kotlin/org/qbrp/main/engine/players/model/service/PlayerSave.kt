package org.qbrp.main.engine.players.model.service

import kotlinx.serialization.encodeToString
import org.qbrp.main.core.components.extensions.toDTO
import org.qbrp.main.core.components.serialization.Serialization
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.core.util.save.Saver
import org.qbrp.main.engine.players.model.PlayerJsonField
import org.qbrp.main.engine.players.model.ServerPlayerObject

class PlayersTable(val table: TableAccess) : TableAccess by table

typealias PlayerSaver = Saver<ServerPlayerObject>

fun TableAccess.savePlayer(player: ServerPlayerObject) = with(player) {
    val json = encodePlayerToJson(PlayerJsonField(id, state.toDTO(), username.value))
    saveObject(id, json)
}

fun decodePlayerFromJson(json: String): PlayerJsonField {
    return Serialization.JSON.decodeFromString<PlayerJsonField>(json)
}

fun encodePlayerToJson(t: PlayerJsonField): String {
    return Serialization.JSON.encodeToString(t)
}