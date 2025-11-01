package org.qbrp.main.network.interaction

import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.controls.entity.interactions.toInteractionId
import org.qbrp.main.engine.controls.model.InteractionNodeComposer
import org.qbrp.main.engine.controls.system.InteractionCallEvent
import org.qbrp.main.engine.controls.system.InteractionCallRequest
import org.qbrp.main.engine.controls.system.InteractionListRequest
import org.qbrp.main.engine.controls.system.InteractionSystem
import org.qbrp.main.engine.controls.system.groupInteractionsByObjects
import org.qbrp.main.engine.controls.system.modes.setInteractionMode
import org.qbrp.main.engine.controls.system.modes.setNoClip
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.network.transport.readEnum
import org.qbrp.main.network.transport.NetworkChannel
import org.qbrp.main.network.transport.Packet
import org.qbrp.main.network.transport.PacketCodec
import org.qbrp.main.network.transport.wrapEndpoint
import org.qbrp.main.network.transport.writeEnum

private val interactionNodeComposer: InteractionNodeComposer by GlobalContext.get().inject()

val INTERACTION_LIST_ENDPOINT = NetworkChannel<InteractionListPacket>()
    .wrapEndpoint()

@Serializable
data class InteractionListPacket(val contextMenuDto: InteractionContextMenuViewDto) : Packet

val INTERACTION_CALL_ENDPOINT = NetworkChannel<InteractCallPacket>()
    .wrapEndpoint()

@Serializable
data class InteractCallPacket(val hoveredInventoryId: ContainerId?, val selectedItem: ItemId?) : Packet

val NO_CLIP_ENDPOINT = NetworkChannel<NoClipPacket>(
    codec = PacketCodec.Binary(
        { NoClipPacket(readBoolean()) },
        { writeBoolean(it.enabled) }
    )
)
    .wrapEndpoint()

data class NoClipPacket(val enabled: Boolean) : Packet

val INTERACTION_MODE_APPLY_ENDPOINT = NetworkChannel<InteractionModeApplyPacket>(
    codec = PacketCodec.Binary(
        { InteractionModeApplyPacket(readEnum()) },
        { writeEnum(it.mode) }
    )
)
    .wrapEndpoint()

data class InteractionModeApplyPacket(val mode: InteractionMode) : Packet

val INTERACTION_SELECT_ENDPOINT = NetworkChannel<InteractionSelectPacket>(
    codec = PacketCodec.Binary(
        { InteractionSelectPacket(readLong().toInteractionId()) },
        { writeLong(it.interactionId.value) }
    )
).wrapEndpoint()

class InteractionSelectPacket(val interactionId: InteractionId) : Packet

fun KoinComponent.runInteractionNetworkSynchronization(
    composer: InteractionNodeComposer,
    system: InteractionSystem,
    playerStorage: ServerPlayerStorage,
    itemStorage: ItemStorage<ServerItemObject>
) {
    on<InteractionCallEvent>() {
        val groupInteractions = groupInteractionsByObjects(selection.interactions)
        val nodes = composer.composeToNodes(groupInteractions, InteractionContext(intention, context))
        val contextMenuViewData = InteractionContextMenuViewData(nodes)
        val dto = contextMenuViewToDto(contextMenuViewData)
        INTERACTION_LIST_ENDPOINT
            .sendS2C(InteractionListPacket(dto), player)
    }

    INTERACTION_CALL_ENDPOINT
        .registerReceiver { ctx ->
            val player = ctx.player ?: return@registerReceiver
            val item = selectedItem?.value?.let { itemStorage.getById(it) }
            system.requestList(
                InteractionListRequest(
                    InteractionRequestContext(
                        player,
                        hoveredInventoryId,
                        item
                    )
                )
            )
        }

    NO_CLIP_ENDPOINT
        .registerReceiver { ctx ->
            ctx.player?.setNoClip(enabled)
        }

    INTERACTION_MODE_APPLY_ENDPOINT
        .registerReceiver { ctx ->
            ctx.player?.setInteractionMode(mode)
        }

    INTERACTION_SELECT_ENDPOINT
        .registerReceiver { ctx ->
            val player = ctx.player
            if (player == null) return@registerReceiver
            system.requestCall(InteractionCallRequest.Manual(player, interactionId))
        }
}