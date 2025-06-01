package org.qbrp.main.core.regions

import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.format.Format.formatMinecraft
import java.util.WeakHashMap

class RegionSelection {
    private val playerSelections = WeakHashMap<ServerPlayerEntity, Selection>()
    val selectionTool = Items.IRON_AXE

    fun getPlayerSelection(player: ServerPlayerEntity): Selection = playerSelections.getOrPut(player) { Selection() }

    fun handleInteraction(player: ServerPlayerEntity, hand: Hand, hitResult: BlockHitResult) {
        if (player.hasPermissionLevel(4)) {
            if (isSelectionTool(player, hand)) selection(player, hitResult.blockPos)
        }
    }

    private fun isSelectionTool(player: ServerPlayerEntity, hand: Hand): Boolean {
        val itemStack = player.getStackInHand(hand)
        return itemStack.item == selectionTool
    }

    private fun selection(player: ServerPlayerEntity, blockPos: BlockPos) {
        val selection = playerSelections[player] ?: Selection()
        if (player.isSneaking) {
            playerSelections[player] = selection.copy(firstPos = blockPos)
            player.sendMessage("<green>Установлена первая позиция: ${blockPos.x}, ${blockPos.y}, ${blockPos.z}".asMiniMessage(), false)
        } else {
            playerSelections[player] = selection.copy(secondPos = blockPos)
            player.sendMessage("<green>Установлена вторая позиция: ${blockPos.x}, ${blockPos.y}, ${blockPos.z}".asMiniMessage(), false)
        }
    }
}