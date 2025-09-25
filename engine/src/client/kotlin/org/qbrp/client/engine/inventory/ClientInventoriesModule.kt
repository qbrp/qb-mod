package org.qbrp.client.engine.inventory

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.qbrp.client.core.mc.connection.event.DisconnectEvent
import org.qbrp.client.engine.inventory.networking.ClientInventoriesNetworkHandler
import org.qbrp.client.deprecated.HeldItem
import org.qbrp.client.deprecated.HeldItemManager
import org.qbrp.client.engine.inventory.service.ContainerStates
import org.qbrp.client.engine.inventory.service.InventoryCloseListener
import org.qbrp.client.engine.inventory.service.InventoryHoldersStorage
import org.qbrp.client.engine.inventory.service.InventoryManager
import org.qbrp.client.engine.inventory.service.InventoryManagerImpl
import org.qbrp.client.engine.items.ClientItemStorage
import org.qbrp.client.render.ui.components.behaviour.CloseListener
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.inventory.networking.WidgetsInventoryListener

@Autoload(priority = Priorities.LOWEST + 2, env = EnvType.CLIENT)
class ClientInventoriesModule: EngineModule("inventories") {
    override fun getKoinModule() = innerWithApi({
        single<HeldItem> { HeldItemManager() }
        single { InventoryHoldersStorage() }
        single { ContainerStates() }
        single<InventoryManager> { getLocal<InventoryManagerImpl>() }
        single { ClientInventoriesNetworkHandler(get(), get(), get(), get()) }
        single<WidgetsInventoryListener> { get<ClientInventoriesNetworkHandler>() }
        single { InventoryCloseListener(getLocal()) }
    }, {
        scoped { InventoryManagerImpl(get(), get()) }
    })

    private val manager by inject<InventoryManager>()
    override fun onEnable() = withEventContext {
        on<DisconnectEvent> { manager.closeAll() }
        get<ClientInventoriesNetworkHandler>().run()
    }
}