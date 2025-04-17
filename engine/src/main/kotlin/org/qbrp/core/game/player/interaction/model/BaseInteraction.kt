package org.qbrp.core.game.player.interaction.model

import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.interaction.Interaction
import org.qbrp.core.game.player.interaction.Interactions

class BaseInteraction: Interaction(Interactions.BASE) {
    override fun registerInvoker() {
        UseEntityCallback.EVENT.register() { player, world, hand, entity, hitResult ->
            if (player is ServerPlayerEntity) {
                val session = PlayerManager.getPlayerSession(player)
                if (session.account?.appliedCharacter != null) {
                    invoke(player)
                } else {
                    blockedCallback(session, "Аккаунт игрока не зарегистрирован.")
                }
            }
            ActionResult.PASS
        }
    }

    override fun invoke(player: ServerPlayerEntity) {
        val target = PlayerManager.getPlayerSession(PlayerManager.getPlayerLookingAt(player)!!).account!!.appliedCharacter!!
        PlayerManager.getPlayerSession(player).let {
            CharacterPrimaryInteraction.EVENT.invoker().onInteract(
                it.account!!.appliedCharacter!!,
                target, it.interactionManager.intent)
        }
    }
}