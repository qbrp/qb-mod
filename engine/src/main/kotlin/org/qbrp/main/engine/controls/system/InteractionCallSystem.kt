package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.disconnect
import org.qbrp.main.core.utils.playSoundForPlayer
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.networking.DisconnectReason
import org.qbrp.main.engine.players.model.util.PlayerId

class InteractionCallSystem {
    context(ctx: MinecraftContext)
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
            val player = request.context.player
            val intention = intentions[player.playerId]!!
            val lookup = lookupInteractions(request.context)
            val context = InteractionContext(intention, request.context)
            val availableInteractions = findAvailableInteractions(lookup, context)

            selections[player.playerId] = InteractionSelection(availableInteractions, false)
        }
    }

    context(ctx: MinecraftContext)
    private fun processManual(selection: InteractionSelection, call: InteractionCallRequest.Manual) {
        execute(call.id, selection, call.player)
    }

    context(ctx: MinecraftContext)
    private fun execute(id: InteractionId, selection: InteractionSelection, player: ServerPlayerObject) {
        try {
            val interaction = selection.interactions.find { it.id == id }
            if (interaction == null) throw IllegalStateException("Взаимодействие $id не найдено")
            if (!interaction.isAvailable) throw IllegalStateException("Взаимодействие $id недоступно")
            interaction.execute()
        } catch (e: Exception) {
            player.disconnect(DisconnectReason("Ошибка взаимодействия: ${e.message}"))
            e.printStackTrace()
        }
    }
}