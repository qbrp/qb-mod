package org.qbrp.main.engine.items.components.containers

import kotlinx.serialization.Serializable
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.engine.inventory.AbstractContainer
import org.qbrp.main.engine.inventory.InventorySynchronizer
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.items.components.ItemTick
import org.qbrp.main.engine.items.components.ItemTickContext

@Serializable
/**
 * Адаптер AbstractContainer для предметов
 */
abstract class ItemContainer : ItemBehaviour(), AbstractContainer, ItemTick {
    override val id: String = IDGenerator.nextId().toString()

    /**
     * Каждый тик, при условии shouldCloseInventory() шлет на клиентский экземпляр предмета сообщение о закрытии экрана.
     * TODO: Изменить алгоритм, возможно перенести на уровень модуля
     */
    override fun tick(context: ItemTickContext) {
        context.player?.let { player ->
            if (shouldCloseInventory(player)) closeInventory(player)
        }
    }

    override fun onEnable() {
        requireState().addComponentIfNotExist(InventorySynchronizer(channel))
    }
}
