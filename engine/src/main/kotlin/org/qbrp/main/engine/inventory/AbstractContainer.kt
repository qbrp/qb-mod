package org.qbrp.main.engine.inventory

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.inventory.Inventory
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.game.model.components.Activateable
import org.qbrp.main.core.game.model.components.Behaviour
import org.qbrp.main.core.game.model.components.Loadable
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.synchronization.SynchronizeConvertible
import org.qbrp.main.core.synchronization.components.S2CMessaging
import org.qbrp.main.core.utils.log.LoggerUtil
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.components.ItemTick
import org.qbrp.main.engine.items.components.ItemTickContext
import org.qbrp.main.engine.items.model.ItemRepository

/**
 * Контейнер с бесконечной вместимостью. Ограничение регулируется с помощью canPutItem() и canTakeItem().
 * @param itemRepository Хранилище предметов, вставлять из ItemsModule
 * @param channel Канал отправки сообщений. Чаще всего подходит объект, в котором размещается компонент.
 */
interface AbstractContainer : Loadable, Container, SynchronizeConvertible {
    companion object {
        val SYNC_ID_ENTRY = ClusterEntry<String>("container.id")
        protected val LOGGER = LoggerUtil.get("items", "container")
    }
    @Transient val channel: S2CMessaging
    @Transient val itemRepository: ItemRepository
    val data: ContainerData
    val inventory get() = data.inventory

    fun canTakeItem(player: PlayerObject) {}
    fun canPutItem(player: PlayerObject) {}
    fun shouldCloseInventory(player: PlayerObject): Boolean { return false }

    fun closeInventory(player: ServerPlayerObject) {
        channel.sendMessage(Cluster(), player, "inventory.$id.close")
    }

    fun openInventory(player: ServerPlayerObject) {
        channel.sendMessage(Cluster(), player, "inventory.$id.open")
    }

    override fun onLoad() {
        data.load(itemRepository, Engine.getModule(), { LOGGER.error("Предмет в контейнере не найден: $id")})
    }

    override fun takeItem(slot: Int): InventoryEntry? {
        if (slot !in inventory.indices) return null
        return inventory.removeAt(slot)
    }

    override fun putItem(slot: Int, item: InventoryEntry): InventoryEntry? {
        return when (slot) {
            in 0 until inventory.size -> {
                val cachedItem = inventory[slot]
                inventory[slot] = item
                cachedItem
            }
            inventory.size -> {
                inventory.add(item)
                null
            }
            else -> null
        }
    }

    override fun swapItem(slot: Int, item: InventoryEntry): InventoryEntry {
        return when (slot) {
            in 0 until inventory.size -> {
                val old = inventory[slot]
                inventory[slot] = item
                old
            }
            else -> item
        }
    }

    override fun getEntries(): List<InventoryEntry> = inventory.toList()

    override fun toCluster(player: ServerPlayerObject): Cluster {
        return ClusterBuilder()
            .component(SYNC_ID_ENTRY, id)
            .build()
    }
}
