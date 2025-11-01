package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.CoreContext
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.system.modes.InteractionModeSystem
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.core.util.PlayerId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class InteractionSystemImpl(
    private val callSystem: InteractionCallSystem,
) : InteractionSystem {
    private val callRequests: ConcurrentLinkedQueue<InteractionCallRequest> = ConcurrentLinkedQueue()
    private val listRequests: ConcurrentLinkedQueue<InteractionListRequest> = ConcurrentLinkedQueue()
    private val intentions: ConcurrentHashMap<PlayerId, PlayerIntention> = ConcurrentHashMap()
    private val selections: ConcurrentHashMap<PlayerId, InteractionSelection> = ConcurrentHashMap()

    private var ServerPlayerObject.intention
        get() = intentions.getOrPut(playerId) { PlayerIntention(Intent.DEFAULT, false) }
        set(value) = intentions.set(playerId, value)

    private var ServerPlayerObject.selection
        get() = selections.getOrPut(playerId) { InteractionSelection(listOf()) }
        set(value) = selections.set(playerId, value)

    override fun update() {
        callSystem.update(callRequests.toList(), listRequests.toList(), intentions, selections)
        callRequests.clear()
        listRequests.clear()
    }

    fun onPlayerJoin(player: ServerPlayerObject) {
        intentions[player.playerId] = PlayerIntention(Intent.DEFAULT, false)
    }

    override fun requestCall(request: InteractionCallRequest) {
        callRequests += request
    }

    override fun requestList(request: InteractionListRequest) {
        listRequests += request
    }

    override fun toggleAlternateMode(player: ServerPlayerObject) {
        val intention = player.intention
        player.intention = intention.copy(alternate = !intention.alternate)
    }

    override fun setIntent(player: ServerPlayerObject, intent: Intent) {
        val intention = player.intention
        player.intention = intention.copy(intent = intent)
    }
}