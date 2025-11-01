package org.qbrp.main.network.player

import org.koin.core.component.KoinComponent
import org.qbrp.main.engine.keybind.KeybindId
import org.qbrp.main.engine.keybind.emulateKeybind
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint

val KEYBIND_ACTIVATION_CHANNEL = NetworkChannel<KeybindActivationPacket>(
    codec = PacketCodec.Binary(
        { KeybindActivationPacket(KeybindId(readString())) },
        { writeString(it.id.value) }
    )
).wrapEndpoint()

data class KeybindActivationPacket(val id: KeybindId) : Packet

fun KoinComponent.runKeybindActivationHandler() {
    KEYBIND_ACTIVATION_CHANNEL
        .registerReceiver { ctx ->
            val player = ctx.player ?: return@registerReceiver
            emulateKeybind(player, id)
        }
}