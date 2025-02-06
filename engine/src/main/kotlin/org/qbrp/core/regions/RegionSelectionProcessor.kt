package org.qbrp.core.regions

import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import org.qbrp.system.utils.format.Format.formatMinecraft
import java.util.WeakHashMap

object RegionSelectionProcessor {
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
            player.sendMessage("&aУстановлена первая позиция: ${blockPos.x}, ${blockPos.y}, ${blockPos.z}".formatMinecraft(), false)
        } else {
            playerSelections[player] = selection.copy(secondPos = blockPos)
            player.sendMessage("&aУстановлена вторая позиция: ${blockPos.x}, ${blockPos.y}, ${blockPos.z}".formatMinecraft(), false)
        }
    }
}