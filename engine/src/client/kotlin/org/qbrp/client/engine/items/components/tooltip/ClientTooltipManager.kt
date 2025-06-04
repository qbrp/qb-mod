package org.qbrp.client.engine.items.components.tooltip

import net.minecraft.client.gui.tooltip.TooltipComponent
import net.minecraft.client.item.TooltipData
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import org.qbrp.client.engine.items.components.tooltip.mc.StaticTooltipRender
import org.qbrp.client.engine.items.components.tooltip.mc.TooltipContainer
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.synchronization.`interface`.state.SynchronizeUpdate

class ClientTooltipManager(var lines: List<String>): ItemBehaviour(), SynchronizeUpdate, TooltipProvider {
    val additional: MutableList<String> = mutableListOf()
    override fun update(cluster: ClusterViewer) {
        lines = cluster.getComponentData<List<String>>("lines")!!
    }

    override fun provideTooltip(stack: ItemStack): TooltipData? {
        val children = mutableListOf<TooltipComponent>()
        val allLines = lines + additional
        if (allLines.isNotEmpty()) {
            val textList: List<Text> = allLines.map { ("$it<newline>").asMiniMessage() }
            children += StaticTooltipRender(textList)
        }

        requireState().getComponentsIsInstance<DynamicTooltip>().forEach {
            children += it
        }

        if (children.isEmpty()) return null
        return TooltipContainer(children.toList())
    }
}