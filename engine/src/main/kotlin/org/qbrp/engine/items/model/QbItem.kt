package org.qbrp.engine.items.model

import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.components.ItemComponent
import org.qbrp.core.resources.content.ItemConfig
import org.qbrp.system.networking.messages.components.Component
import org.qbrp.system.utils.log.Loggers

class QbItem(): Item(Settings()), KoinComponent {
    lateinit var data: ItemData
    private var activated = true
    val components: List<ItemComponent>
        get() = data.components

    fun loadFromDatabase(stack: ItemStack) {
        data = get<ItemLoader>().getOrCreateItem(stack.nbt?.getInt("item_id") ?: return, stack)
        onLoaded()
    }

    inline fun <reified T> getComponent(): T? {
        return components.filterIsInstance<T>().first()
    }

    fun createBlankData(tag: ItemConfig.Tag, config: ItemConfig) {
        data = ItemData(1, tag.name, config.parent, tag.components.map { it.cast() })
        get<ItemLoader>().createItem(data)
        onLoaded()
    }

    private fun onLoaded() = Unit

    override fun inventoryTick(stack: ItemStack?, world: World?, entity: Entity?, slot: Int, selected: Boolean) {
        try {
            super.inventoryTick(stack, world, entity, slot, selected)
            if (::data.isInitialized && activated) {
                components.forEach {
                    it.cache(stack ?: return, entity as? PlayerEntity ?: return)
                    if (!activated) {
                        it.activate(); activated = true
                    }
                    it.tick()
                }
            }
        } catch (e: Exception) {
            logger.error("Ошибка обработки тика предмета ${data.name} с идентификатором ${data.id}: ${e.message}")
            logger.error("- Владелец: ${entity?.name?.string} (${entity?.type?.name?.string}), ${entity?.x}, ${entity?.y}, ${entity?.z}")
            logger.error("- Компоненты: ${components.joinToString("\n")}")
            e.printStackTrace()
            logger.error("Предмет деактивирован.")
            activated = false
        }
    }

    companion object {
        private val logger = Loggers.get("items")
    }
}