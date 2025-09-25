package org.qbrp.main.engine.controls.system

import org.koin.core.context.GlobalContext
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.system.lookup.CameraLookup
import org.qbrp.main.engine.controls.system.lookup.InteractionLookup
import org.qbrp.main.engine.controls.system.lookup.InventoryLookup
import org.qbrp.main.engine.world.WorldRaycastManager

fun lookupInteractions(
    call: InteractionRequestContext,
    raycastManager: WorldRaycastManager = GlobalContext.get().get(),
    interactionService: InteractionService = GlobalContext.get().get()
) : InteractionLookup {
    val player = call.player
    val lookup = if (call.inventoryOpened) {
        InventoryLookup(player, false, call)
    } else {
        CameraLookup(player, raycastManager)
    }
    lookup.lookup(interactionService.getPlayerInteractable(player), interactionService)
    return lookup
}