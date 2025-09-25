package org.qbrp.main.engine.players.inventory

import org.koin.core.component.get
import org.koin.core.component.inject
import org.qbrp.main.core.CoreContext
import org.qbrp.main.core.components.ComponentRegistryInitializationEvent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.onComponentEvent
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.keybinds.onKeybind
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.players.model.events.PlayerDisconnectEvent
import org.qbrp.main.engine.players.characters.components.characterComponent
import org.qbrp.main.engine.players.characters.components.hasCharacter
import org.qbrp.main.engine.players.characters.components.sendCharacterNotAppliedNotification
import org.qbrp.main.engine.players.characters.events.CharacterApplyEvent
import org.qbrp.main.engine.players.inventory.hand.Hand
import org.qbrp.main.engine.players.inventory.serialization.CharacterInventoryDto
import org.qbrp.main.engine.players.inventory.serialization.CharacterInventoryMapper

@Autoload(Priorities.PLAYERS - 2)
class CharacterInventoryModule: EngineModule("player-inventory") {
    private val containerService by inject<ContainerService>()
    private val manipulator by inject<Manipulator>()

    override fun onEnable() = withCoreContext {
        registerComponents()
        registerEvents()
        get<ComponentDtoMapper>().apply {
            register(CharacterInventoryMapper(containerService, manipulator),
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
    }

    context(core: CoreContext)
    private fun registerEvents() = withEventContext {
        onKeybind(OPEN_HANDS_KEYBIND) { player ->
            if (!player.hasCharacter()) player.sendCharacterNotAppliedNotification("Открытие инвентаря")
            else player.require<CharacterInventory>().openHands()
        }
    }

    private fun registerComponents() {
        ComponentRegistryInitializationEvent.EVENT.register {
            it.register { componentClass(Hand::class) }
            it.register {
                componentClass(CharacterInventory::class)
                characterComponent { CharacterInventory(it, containerService, it.require()) }
            }
        }
    }

    companion object {
        val OPEN_HANDS_KEYBIND = KeybindId("open_hands")
    }
}