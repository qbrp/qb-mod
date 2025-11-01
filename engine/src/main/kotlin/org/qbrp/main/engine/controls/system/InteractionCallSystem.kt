package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.events.emit
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.network.player.DisconnectReason
import org.qbrp.main.core.util.PlayerId

class InteractionCallSystem {
    fun update(
        callRequests: List<InteractionCallRequest>,
        listRequests: List<InteractionListRequest>,
        intentions: Map<PlayerId, PlayerIntention>,
        selections: MutableMap<PlayerId, InteractionSelection>
    ) {
        for (request in callRequests) {
            when(request) {
                is InteractionCallRequest.Context -> TODO()
                is InteractionCallRequest.Manual -> {
                    val selection = selections[request.player.playerId]!!
                    processManual(selection, request)
                }
            }
        }
        for (request in listRequests) {
            try {
                val player = request.context.player
                val intention = intentions[player.playerId]!!
                val lookup = lookupInteractions(request.context)
                val context = InteractionContext(intention, request.context)
                val availableInteractions = findAvailableInteractions(lookup, context)

                val selection = InteractionSelection(availableInteractions)
                selections[player.playerId] = InteractionSelection(availableInteractions)
                InteractionCallEvent(player, selection, intention, request.context).emit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun processManual(selection: InteractionSelection, call: InteractionCallRequest.Manual) {
        execute(call.id, selection, call.player)
    }

    private fun execute(id: InteractionId, selection: InteractionSelection, player: ServerPlayerObject) {
        try {
            val interaction = selection.interactions.find { it.id == id }
            if (interaction == null) throw IllegalStateException("Взаимодействие $id не найдено")
            if (!interaction.isAvailable) throw IllegalStateException("Взаимодействие $id недоступно")
            interaction.execute()
        } catch (e: Exception) {
            //player.disconnect(DisconnectReason("Ошибка взаимодействия: ${e.message}"))
            e.printStackTrace()
        }
    }
}