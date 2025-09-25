package org.qbrp.client.engine.players.inventory

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.lwjgl.glfw.GLFW
import org.qbrp.client.core.keybinds.ClientKeybindsAPI
import org.qbrp.client.engine.inventory.service.ContainerStates
import org.qbrp.client.engine.players.characers.ClientCharactersBank
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.players.characters.events.CharacterApplyEvent
import org.qbrp.main.engine.players.inventory.CharacterInventoryModule.Companion.OPEN_HANDS_KEYBIND
import org.qbrp.main.engine.players.inventory.hand.Hand
import org.qbrp.main.engine.players.inventory.handContainerId

@Autoload(priority = Priorities.LOWEST, env = EnvType.CLIENT)
class ClientCharacterInventoryModule() : EngineModule("client-inventory") {
    private val characterBank by inject<ClientCharactersBank>()
    private val containerStates by inject<ContainerStates>()
    private val rightHandId get() = handContainerId(Hand.Side.RIGHT,characterBank.character!!.id)
    private val leftHandId get() = handContainerId(Hand.Side.LEFT,characterBank.character!!.id)

    override fun onEnable(): Unit = withEventContext {
        val keybinds = get<ClientKeybindsAPI>()
        keybinds.apply {
            registerKeybinding("Показать руки", GLFW.GLFW_KEY_E, OPEN_HANDS_KEYBIND, method = {})
        }

        on<CharacterApplyEvent> {
            player.add(
                HandItemsComponent(
                    player.componentContext,
                    containerStates.add(rightHandId),
                    containerStates.add(leftHandId)
                )
            )
        }

        componentRegistrar { use<HandItemsComponent>() }
    }
}