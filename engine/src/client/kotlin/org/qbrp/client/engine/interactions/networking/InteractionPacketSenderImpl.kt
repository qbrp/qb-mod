package org.qbrp.client.engine.interactions.networking

import org.qbrp.client.core.networking.v2.ClientPacketSender
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.ControlsModule
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.controls.networking.InteractCallPacket
import org.qbrp.main.engine.controls.networking.InteractionModeApplyPacket
import org.qbrp.main.engine.controls.networking.InteractionSelectPacket
import org.qbrp.main.engine.controls.networking.NoClipPacket

class InteractionPacketSenderImpl(private val packetSender: ClientPacketSender): InteractionPacketSender {
    override fun sendInteractionCallPacket(
        selectedInventory: ContainerId?,
        selectedItem: ClientItemObject?
    ) {
        packetSender.send(
            InteractCallPacket(selectedInventory, selectedItem),
            ControlsModule.INTERACT_CHANNEL
        )
    }

    override fun sendInvokeInteractionPacket(id: InteractionId) {
        packetSender.send(
            InteractionSelectPacket(id),
            ControlsModule.SELECT_INTERACTION_CHANNEL
        )
    }

    override fun sendInteractionModePacket(mode: InteractionMode) {
        packetSender.send(
            InteractionModeApplyPacket(null, mode),
            ControlsModule.INTERACTION_MODE_CHANNEL
        )
    }

    override fun sendNoClipPacket(enabled: Boolean) {
        packetSender.send(
            NoClipPacket(enabled),
            ControlsModule.NO_CLIP_CHANNEL
        )
    }
}