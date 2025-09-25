package org.qbrp.main.engine.players.inventory.creative

import org.koin.core.component.inject
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.keybinds.onKeybind
import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.player.impl.ServerPlayerInventoryManager
import org.qbrp.main.engine.players.inventory.creative.ItemListModule.Companion.INVENTORY_KEYBIND
import kotlin.getValue

@Autoload
class InfinityInventoryModule: EngineModule("infinity-inventory") {
    private val containerService by inject<ContainerService>()

    override fun onEnable() = withCoreContext() {
        onKeybind(INVENTORY_KEYBIND) { player ->
            if (!player.hasPermission(INFINITY_INVENTORY_PERMISSION)) return@onKeybind
            val infinity = player.getOrAdd(name = COMPONENT_NAME) { Container(createInfinityContainer()) }
            player
                .require<ServerPlayerInventoryManager>()
                .open(infinity.id)
        }
    }

    private fun createInfinityContainer(): ContainerId {
        return containerService.newContainer(
            ContainerDisplayName("Неограниченный инвентарь"),
            Capacity.Infinity
        )
    }

    companion object {
        val COMPONENT_NAME = Container.name("infinity")
        val INFINITY_INVENTORY_PERMISSION = Permission("infinity-inventory")
        fun PlayerSnapshot.hasInfinityInventoryPermission() = hasPermission(INFINITY_INVENTORY_PERMISSION)
    }
}