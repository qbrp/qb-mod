package org.qbrp.main.engine.keybind

import org.koin.core.component.inject
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.system.InteractionSystem
import org.qbrp.main.engine.physics.player.fly
import org.qbrp.main.engine.players.inventory.openCharacterInventory
import org.qbrp.main.engine.players.inventory.openInfinityInventory
import org.qbrp.main.engine.players.inventory.openItemList
import org.qbrp.main.engine.spectators.stopSpectating

@Autoload(priority = Priorities.LOWEST)
class KeybindControllerModule : EngineModule("keybinds-control") {
    override fun onEnable() {
        val containerService: ContainerService by inject()

        onKeybind(OPEN_HANDS_KEYBIND) { player ->
            player.openCharacterInventory()
        }

        onKeybind(CREATIVE_INVENTORIES) { player ->
            player.openInfinityInventory(containerService)
            player.openItemList()
        }

        onKeybind(SPECTATOR_SPAWN_KEYBIND) { player ->
            player.stopSpectating()
        }

        onKeybind(TOGGLE_FLY_KEYBIND) { player ->
            player.apply { fly = !fly }
        }

        registerInteractionKeybinds()
    }

    private fun registerInteractionKeybinds() {
        val interactionSystem: InteractionSystem by inject()

        onKeybind(DEFAULT_INTENT_KEYBIND) { player ->
            interactionSystem.setIntent(player, Intent.DEFAULT)
        }
        onKeybind(FINE_INTENT_KEYBIND) { player ->
            interactionSystem.setIntent(player, Intent.FINE)
        }
        onKeybind(RUDE_INTENT_KEYBIND) { player ->
            interactionSystem.setIntent(player, Intent.ROUGH)
        }
        onKeybind(TOGGLE_ALTERNATE_KEYBIND) { player ->
            interactionSystem.toggleAlternateMode(player)
        }
    }

    companion object {
        val TOGGLE_FLY_KEYBIND = KeybindId("toggle_fly")
        val OPEN_HANDS_KEYBIND = KeybindId("open_hands")
        val CREATIVE_INVENTORIES = KeybindId("creative_inventories")
        val SPECTATOR_SPAWN_KEYBIND = KeybindId("spectator_spawn")
        val DEFAULT_INTENT_KEYBIND = KeybindId("default_intent")
        val FINE_INTENT_KEYBIND = KeybindId("fine_intent")
        val RUDE_INTENT_KEYBIND = KeybindId("rude_intent")
        val TOGGLE_ALTERNATE_KEYBIND = KeybindId("toggle_alternate")
    }
}
