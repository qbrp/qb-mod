package org.qbrp.client.render.hud

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import org.qbrp.client.engine.inventory.HeldItem

class HeldItemRenderer(val heldItem: HeldItem) {
    fun render(context: DrawContext, mouseX: Int, mouseY: Int, textRenderer: TextRenderer) {
        val heldStack = heldItem.getStack()
        if (!heldStack.isEmpty) {
            context.drawItem(heldStack, mouseX + 6, mouseY + 6)
            context.drawItemInSlot(textRenderer, heldStack, mouseX, mouseY)
        }
    }
}