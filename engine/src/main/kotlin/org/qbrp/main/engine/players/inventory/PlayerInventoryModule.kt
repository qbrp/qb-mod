package org.qbrp.main.engine.players.inventory

import net.minecraft.server.command.CommandManager
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.fabricmc.commands.registerCommand
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.util.format.Format.formatMm
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.system.request.ItemPrefabPrototype
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.players.characters.CharacterApplyEvent
import org.qbrp.main.engine.players.inventory.serialization.CharacterInventoryDto
import org.qbrp.main.engine.players.inventory.serialization.CharacterInventoryMapper

@Autoload(Priorities.PLAYERS - 2)
class PlayerInventoryModule : EngineModule("player-inventory") {
    private val containerService by inject<ContainerService>()
    private val manipulator by inject<ManipulatorSystem>()

    override fun getKoinModule(): Module = module {
        single<ContainerEntryPrototype> { ItemPrefabPrototype(get()) }
    }

    override fun onEnable() {
        registerComponents()
        get<ComponentDtoMapper>().apply {
            register(
                CharacterInventoryMapper(containerService, manipulator),
                CharacterInventory::class,
                CharacterInventoryDto::class
            )
        }
        get<SerializersRegistry>().apply {
            registerSerializer(SerializerRegistryId.ofComponent("CharacterInventory"), CharacterInventoryDto.serializer())
        }

        on<CharacterApplyEvent>() {
            player.get<CharacterInventory>()?.close()
        }

        on<ModLoadedEvent>(EventPriority.LATEST) {
            createItemListContainer()
        }

        registerCommand { dispatcher ->
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
    }

    private fun createItemListContainer() {
        containerService.createItemListContainer(get(), get(), get())
    }

    private fun registerComponents() {
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { componentClass(Hand::class) }
            it.register { componentClass(CharacterInventory::class) }
            it.register { data<Equip>() }
            it.register { componentClass(ItemListInstance::class) }
        }
    }
}