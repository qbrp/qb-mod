package org.qbrp.main.engine.controls.system.modes

import org.qbrp.main.core.CoreContext
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.players.model.util.PlayerId

class InteractionModeSystem {
    context(core: CoreContext)
    fun update(interactionsStates: MutableMap<PlayerId, InteractionState>, changes: List<InteractionModeUpdate>) {
        for (edit in changes) {
            val player = edit.player
            val state = interactionsStates[player.playerId] ?: InteractionState(InteractionMode.DEFAULT, false)
            var updateMode = state.mode
            var updateNoClip = state.noClip
            if (edit.noClip == null && edit.mode == null) continue
            edit.mode?.let { mode ->
                when(mode) {
                    InteractionMode.DEFAULT -> setDefaultMode(player)
                    InteractionMode.BUILDER -> setBuilderMode(player)
                    InteractionMode.CROW -> setCrowMode(player)
                    InteractionMode.GM -> setGmMode(player)
                }
                updateMode = mode
            }
            if (updateMode != InteractionMode.CROW) {
                edit.noClip?.let {
                    updateNoClip = it
                    if (it) enableNoClip(player) else disableNoClip(player, updateMode)
                }
            }
            interactionsStates[player.playerId] = InteractionState(updateMode, updateNoClip)
        }
    }
}