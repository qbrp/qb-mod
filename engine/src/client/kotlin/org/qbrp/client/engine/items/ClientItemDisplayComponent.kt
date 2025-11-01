package org.qbrp.client.engine.items

import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedModelManager

import org.qbrp.client.core.resources.ModelRepository
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.client.render.item.model.data.ItemTooltip
import org.qbrp.main.engine.tooltip.component.NameTooltipComponent
import org.qbrp.main.engine.tooltip.component.PlainTextTooltipComponent
import org.qbrp.main.engine.tooltip.component.TooltipComponent
import org.qbrp.main.engine.items.ModelId
import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.emptyText
import org.qbrp.main.engine.items.ItemModelId

/**
 * Предоставляет рендеру TooltipComponent, объединяющий в себе все DynamicTooltip-компоненты, а также название предмета.
 * **/
class ClientItemDisplayComponent(
    ctx: ItemComponentInstantiateContext,
    private val modelRepo: ModelRepository,
    private val modelManager: BakedModelManager,
) : ItemComponent(ctx), ItemDisplay {
    var description: List<TextComponent> = listOf()
    override var name: TextComponent = emptyText()
    override val tooltip: ItemTooltip? get() = collectTooltip()
    override var model: ModelId = ItemModelId("")

    val bakedModel: BakedModel
        get() {
            val modelId = modelRepo.getResourceLocation(model.modelId)
            return modelManager.getModel(modelId)
        }

    private fun collectTooltip(): ItemTooltip? {
        val children = mutableListOf<TooltipComponent>()
        children.add(NameTooltipComponent(name))
        if (description.isNotEmpty()) {
            children.add(PlainTextTooltipComponent(description))
        }

        filterIsInstance<TooltipComponent>().forEach { children += it }

        if (children.isEmpty()) return null
        return ItemTooltip(children.toList())
    }
}