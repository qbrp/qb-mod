package org.qbrp.main.engine.controls.system

import org.qbrp.main.core.CoreContext
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.model.player.condition.InteractionProvider
import org.qbrp.main.engine.controls.system.modes.InteractionModeSystem
import org.qbrp.main.engine.controls.system.modes.InteractionModeUpdate
import org.qbrp.main.engine.controls.system.modes.InteractionState
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.util.PlayerId
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class InteractionSystemImpl(
    private val callSystem: InteractionCallSystem,
    private val networkSystem: InteractionNetworkSystem,
    private val modeSystem: InteractionModeSystem,
    private val coreContext: CoreContext
) : InteractionSystem {
    private val callRequests: ConcurrentLinkedQueue<InteractionCallRequest> = ConcurrentLinkedQueue()
    private val listRequests: ConcurrentLinkedQueue<InteractionListRequest> = ConcurrentLinkedQueue()
    private val modeRequests: ConcurrentLinkedQueue<InteractionModeUpdate> = ConcurrentLinkedQueue()
    private val intentions: ConcurrentHashMap<PlayerId, PlayerIntention> = ConcurrentHashMap()
    private val selections: ConcurrentHashMap<PlayerId, InteractionSelection> = ConcurrentHashMap()
    private val states: ConcurrentHashMap<PlayerId, InteractionState> = ConcurrentHashMap()

    private var ServerPlayerObject.intention
        get() = intentions.getOrPut(playerId) { PlayerIntention(Intent.DEFAULT, false) }
        set(value) = intentions.set(playerId, value)

    private var ServerPlayerObject.selection
        get() = selections.getOrPut(playerId) { InteractionSelection(listOf(), false) }
        set(value) = selections.set(playerId, value)

    context(ctx: CoreContext)
    fun update() {
        callSystem.update(callRequests.toList(), listRequests.toList(), intentions, selections)
        networkSystem.update(listRequests.toList(), selections, intentions)
        modeSystem.update(states, modeRequests.toList())
        callRequests.clear()
        listRequests.clear()
        modeRequests.clear()
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

    override fun toggleAlternateMode(player: ServerPlayerObject) = with(coreContext) {
        val intention = player.intention
        player.intention = intention.copy(alternate = !intention.alternate)
        player.syncInteractionState(player.intention)
    }

    override fun setIntent(player: ServerPlayerObject, intent: Intent) = with(coreContext) {
        val intention = player.intention
        player.intention = intention.copy(intent = intent)
        player.syncInteractionState(player.intention)
    }

    override fun setInteractionMode(player: ServerPlayerObject, mode: InteractionMode) {
        modeRequests += InteractionModeUpdate(player, mode, null)
    }

    override fun setNoClip(player: ServerPlayerObject, value: Boolean) {
        modeRequests += InteractionModeUpdate(player, null, value)
    }
}