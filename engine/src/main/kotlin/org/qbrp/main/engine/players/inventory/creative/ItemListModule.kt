package org.qbrp.main.engine.players.inventory.creative

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.assets.prefabs.PrefabsAPI
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.keybinds.onKeybind
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.core.mc.commands.CommandsAPI
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.utils.format.Format.formatMm
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.ContainerEntity
import org.qbrp.main.engine.containers.v2.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.v2.system.request.ItemPrefabPrototype
import org.qbrp.main.engine.inventory.player.impl.ServerPlayerInventoryManager
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.model.ItemInstantiateContext
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.networking.ItemSynchronizer
import org.qbrp.main.engine.items.service.ItemFactory
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.world.location.WorldPosition
import org.qbrp.main.engine.world.model.World

@Autoload(Priorities.PLAYERS - 2)
class ItemListModule: EngineModule("item-list"), CommandRegistryEntry {
    override fun getKoinModule(): Module = module {
        single<ContainerEntryPrototype> { ItemPrefabPrototype(get()) }
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("reloadprefabs").executes { ctx ->
            try {
                createItemListContainer()
                ctx.source.sendMessage("<green>Префабы перезагружены".formatMm())
            } catch (e: Exception) {
                ctx.source.sendMessage("<red>Возникла ошибка перезагрузки префабов: ${e.message}".formatMm())
            }
            1
        })
    }

    override fun onEnable() = withEventContext {
        get<CommandsAPI>().add(this)
        on<ModLoadedEvent>(EventPriority.LATEST) { createItemListContainer() }
        onKeybind(INVENTORY_KEYBIND) { player ->
            if (player.hasItemListPermission()) {
                player.require<ServerPlayerInventoryManager>().open(ITEM_LIST_CONTAINER_ID)
            } else {
                player.sendChatMessage("<red>Недостаточно прав")
            }
        }
    }

    private fun createItemListContainer() { createItemListContainer(get(), get(), get(), get()) }

    fun createItemListContainer(service: ContainerService, prefabsAPI: PrefabsAPI, factory: ItemFactory, world: World) {
        val items = prefabsAPI.parseCategory(ItemsModule.PREFAB_CATEGORY)
            .flatMap { it.tags }
            .map { factory.newCreativeInventoryInstance(it, world) }
        service.newContainer(
            name = ContainerDisplayName("Список предметов"),
            items = items,
            containerId = ITEM_LIST_CONTAINER_ID
        )
    }

    companion object {
        val PERMISSION = Permission("item-list")
        fun ServerPlayerObject.hasItemListPermission(): Boolean {
            return hasPermission(PERMISSION)
        }
        fun ItemFactory.newCreativeInventoryInstance(tag: Prefab.Tag,
                                                     world: World,
                                                     ctx: ItemInstantiateContext = ItemInstantiateContext.Companion.DEFAULT): ServerItemObject {
            val item = ServerItemObject(ctx)
            item.state.parent = item
            item.add(Meta(tag))
            item.add(PositionProxy(world, WorldPosition()))
            return handleInstance(item, tag).apply {
                get<ItemSynchronizer>()?.globalSynchronization = true
            }
        }

        val INVENTORY_KEYBIND = KeybindId("item-list")
        val ITEM_LIST_CONTAINER_ID = ContainerId("item-list")
    }
}
