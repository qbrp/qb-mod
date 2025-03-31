package org.qbrp.core.game.player.interaction

import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult

class BaseInteraction: Interaction(Interactions.BASE) {
    override fun registerInvoker() {
        UseEntityCallback.EVENT.register() { player, world, hand, entity, hitResult ->
            if (player is ServerPlayerEntity) {
                invoke(player)
            }
            ActionResult.PASS
        }
    }
}