package org.qbrp.client.engine.players.inventory

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.lwjgl.glfw.GLFW
import org.qbrp.client.core.keybinds.ClientKeybindsAPI
import org.qbrp.client.engine.inventory.networking.requestSync
import org.qbrp.client.engine.inventory.service.ContainerStates
import org.qbrp.client.engine.players.event.LocalPlayerSyncEvent
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.inventory.creative.ItemListModule
import org.qbrp.main.engine.players.inventory.creative.ItemListModule.Companion.ITEM_LIST_CONTAINER_ID

@Autoload(env = EnvType.CLIENT)
class ClientCreativeInventoryModule: EngineModule("client-creative-inventory") {
    override fun onEnable() = withEventContext {
        val containerStates: ContainerStates by inject()
        on<LocalPlayerSyncEvent> { containerStates.getOrAdd(ITEM_LIST_CONTAINER_ID).requestSync() }
        get<ClientKeybindsAPI>().registerKeybinding(
            "Творческий инвентарь",
            GLFW.GLFW_KEY_Y,
            ItemListModule.INVENTORY_KEYBIND,
            method = {}
        )
    }
}