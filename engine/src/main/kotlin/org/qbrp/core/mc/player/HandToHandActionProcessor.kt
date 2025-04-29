package org.qbrp.core.mc.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import org.qbrp.core.keybinds.ServerKeybindCallback

class HandToHandActionProcessor : HandToHandAction {
    init {
        val event = ServerKeybindCallback.getOrCreateEvent("hand_to_hand")
        event.register { player ->
            val target = PlayerManager.getPlayerLookingAt(player) as? PlayerEntity
            if (target == null || target == player) {
                return@register ActionResult.PASS
            }

            val mainHandStack = player.getStackInHand(Hand.MAIN_HAND)
            if (mainHandStack.isEmpty) {
                return@register ActionResult.PASS
            }

            val targetMainHandStack = target.getStackInHand(Hand.MAIN_HAND)
            if (!targetMainHandStack.isEmpty) {
                return@register ActionResult.PASS
            }

            target.setStackInHand(Hand.MAIN_HAND, mainHandStack.copy())
            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY)

            ActionResult.SUCCESS
        }
    }
}