package org.qbrp.client.engine.inventory.space

import net.minecraft.text.Text

interface LinesTooltipProvider {
    fun getTooltip(): List<Text>
}