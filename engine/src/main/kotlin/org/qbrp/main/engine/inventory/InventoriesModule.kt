package org.qbrp.main.engine.inventory

import org.koin.core.component.get
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.serializerId
import org.qbrp.main.engine.containers.v2.event.ItemAddedInContainerEvent
import org.qbrp.main.engine.containers.v2.event.ItemRemovedFromContainerEvent
import org.qbrp.main.engine.inventory.networking.impl.ContainerInventoryNetworkHandler
import org.qbrp.main.engine.inventory.networking.InventoryPacketSender
import org.qbrp.main.engine.inventory.networking.InventorySynchronizer
import org.qbrp.main.engine.inventory.networking.impl.ContainerSynchronizerImpl
import org.qbrp.main.deprecated.SpacePacketDto
import org.qbrp.main.engine.inventory.player.impl.ServerPlayerInventoryManager
import org.qbrp.main.engine.players.model.events.PlayerInitAfterJsonLoadEvent

@Autoload(3)
class InventoriesModule: EngineModule("inventories") {
    companion object {
        const val OPEN = "inventory_open"
        const val CLOSE = "inventory_close"
        const val SYNC = "inventory_request_sync"
        const val ITEMS = "receive_items"
    }
    private val networkHandler by lazy { getLocal<ContainerInventoryNetworkHandler>() }

    override fun getKoinModule() = innerWithApi({
        single<InventoryPacketSender> { getLocal<ContainerInventoryNetworkHandler>() }
        single { ContainerInventoryNetworkHandler(get(), get(), get(), get()) }
        single<InventorySynchronizer> { ContainerSynchronizerImpl(get(), get()) }
    }, {})

    override fun onEnable() {
        val synchronizer = get<InventorySynchronizer>()
        get<SerializersRegistry>().registerSerializer(SpacePacketDto::class.serializerId(), SpacePacketDto.serializer())
        componentRegistrar { use<ServerPlayerInventoryManager>() }

        withEventContext {
            on<PlayerInitAfterJsonLoadEvent> {
                player.getOrAdd { ServerPlayerInventoryManager(it.player.componentContext, get(), get(), get()) }
            }
            on<ItemAddedInContainerEvent> {
                synchronizer.broadcast(containerId)
            }
            on<ItemRemovedFromContainerEvent> {
                synchronizer.broadcast(containerId)
            }
        }

        networkHandler.run()
    }
}