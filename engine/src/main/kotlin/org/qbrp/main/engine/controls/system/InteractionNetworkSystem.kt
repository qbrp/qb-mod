package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.CoreContext
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.mc.server.context.ServerContext
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.controls.ControlsModule
import org.qbrp.main.engine.controls.dto.InteractionViewMapper
import org.qbrp.main.engine.controls.entity.display.InteractionContextMenuViewData
import org.qbrp.main.engine.controls.entity.interactions.AvailableInteraction
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.InteractionNodeComposer
import org.qbrp.main.engine.controls.networking.InteractionListPacket
import org.qbrp.main.engine.controls.networking.InteractionManagerSyncPacket
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.networking.PlayerSynchronizer
import org.qbrp.main.engine.players.model.util.PlayerId

class InteractionNetworkSystem(
    private val packetSender: ServerPacketSender,
    private val mapper: InteractionViewMapper,
    private val composer: InteractionNodeComposer
) {
    context(ctx: CoreContext)
    fun update(
        listRequests: List<InteractionListRequest>,
        selections: Map<PlayerId, InteractionSelection>,
        intentions: Map<PlayerId, PlayerIntention>
    ) {
        val dirtyStates = listRequests
            .map { it.context.player }
            .distinct()

        for (request in listRequests) {
            val player = request.context.player
            val selection = selections[player.playerId]!!
            val intention = intentions[player.playerId]!!
            val groupInteractions = groupInteractionsByObjects(selection.interactions)
            val nodes = composer.composeToNodes(groupInteractions, InteractionContext(intention, request.context))
            val contextMenuViewData = InteractionContextMenuViewData(nodes)
            val dto = mapper.contextMenuViewToDto(contextMenuViewData)
            packetSender.sendImmediately(
                InteractionListPacket(dto),
                ControlsModule.Companion.INTERACTION_LIST_CHANNEL,
                player
            )
        }

        dirtyStates.forEach { it.syncInteractionState(intentions[it.playerId]!!) }
    }
}