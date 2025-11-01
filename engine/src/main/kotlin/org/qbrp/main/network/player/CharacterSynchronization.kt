package org.qbrp.main.network.player

import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.players.characters.CharacterApplyEvent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.wrapEndpoint

val CHARACTER_APPLY_ENDPOINT = NetworkChannel<CharacterApplyPacket>().wrapEndpoint()

@Serializable
data class CharacterApplyPacket(val character: CharacterData) : Packet

fun KoinComponent.runServerCharacterSynchronizer() {
    on<CharacterApplyEvent> {
        CHARACTER_APPLY_ENDPOINT.sendS2C(CharacterApplyPacket(character), player as ServerPlayerObject)
    }
}