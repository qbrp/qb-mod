package org.qbrp.main.engine.controls.system.lookup

import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.WorldRaycastManager

class LookupManager(
    private val raycastManager: WorldRaycastManager,
    private val interactionService: InteractionService
) {
    fun getLookup(player: ServerPlayerObject, call: InteractionRequestContext): InteractionLookup {
        val lookup = if (call.inventoryOpened) {
            InventoryLookup(player, false, call)
        } else {
            CameraLookup(player, raycastManager)
        }
        lookup.lookup(interactionService.getPlayerInteractable(player), interactionService)
        return lookup
    }
}