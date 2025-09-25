    package org.qbrp.client.engine.interactions

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.dsl.module
import org.lwjgl.glfw.GLFW
import org.qbrp.client.core.keybinds.ClientKeybindsAPI
import org.qbrp.client.core.keybinds.KeyBindModifier
import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.client.engine.interactions.networking.InteractionManagerNetworkHandler
import org.qbrp.client.engine.interactions.networking.InteractionManagerNetworkHandlerImpl
import org.qbrp.client.engine.interactions.networking.InteractionPacketSender
import org.qbrp.client.engine.interactions.networking.InteractionPacketSenderImpl
import org.qbrp.client.engine.interactions.service.ClientInteractionManager
import org.qbrp.client.engine.interactions.service.ClientInteractionModeManager
import org.qbrp.client.engine.interactions.service.SelectionCollector
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.engine.players.event.LocalPlayerPermissionsEvent
import org.qbrp.client.engine.players.event.LocalPlayerSyncEvent
import org.qbrp.client.render.ui.interaction.event.InventorySelection
import org.qbrp.client.render.ui.interaction.event.InventorySelectionCallback
import org.qbrp.client.render.ui.interaction.event.ItemSelection
import org.qbrp.main.core.events.EventId
import org.qbrp.main.core.events.collect
import org.qbrp.main.core.events.collectLists
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.ControlsModule
import org.qbrp.main.engine.controls.dto.InteractionViewMapper
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.networking.InteractionListPacket
import org.qbrp.main.engine.controls.networking.InteractionManagerSyncPacket
import org.qbrp.main.engine.items.service.ItemStorage

@Autoload(priority = Priorities.MODULE - 2, env = EnvType.CLIENT)
class ClientInteractionsModule: EngineModule("client-interactions"), SelectionCollector {
    private val itemStorage by inject<ItemStorage<ClientItemObject>>()

    override fun getKoinModule() = module {
        single { InteractionViewMapper(get()) }
        single<InteractionPacketSender> { InteractionPacketSenderImpl(get()) }
        single { ClientInteractionManager(get(), this@ClientInteractionsModule) }
        single<InteractionManagerNetworkHandler> { InteractionManagerNetworkHandlerImpl(get(), get(), get()) }
        single { ClientInteractionModeManager(get(), get()) }
    }

    override fun onEnable() = withCoreContext {
        val networkHandler: InteractionManagerNetworkHandler by inject()
        val manager: ClientInteractionManager by inject()
        val interactionModeManager: ClientInteractionModeManager by inject()

        on<LocalPlayerPermissionsEvent>() {
            interactionModeManager.setAccessibility(permissions)
        }

        get<PacketListenerActivator<ClientContext>>().apply {
            run(
                ControlsModule.Companion.SYNC_CHANNEL,
                networkHandler,
                { buf, ctx -> InteractionManagerSyncPacket(buf) }
            )
            run(
                ControlsModule.Companion.INTERACTION_LIST_CHANNEL,
                networkHandler,
                { buf, ctx -> InteractionListPacket(buf) }
            )
        }

        get<ClientKeybindsAPI>().apply {
            registerKeybinding("Взаимодействие", GLFW.GLFW_KEY_K, KeybindId("interact"), method = { manager.call() })
            registerKeybinding(
                "Альтернативный режим",
                GLFW.GLFW_MOD_ALT,
                ControlsModule.Companion.TOGGLE_ALTERNATE_KEYBIND,
                method = {}
            )
            registerKeybinding(
                "Основной интент",
                GLFW.GLFW_KEY_1,
                ControlsModule.Companion.DEFAULT_INTENT_KEYBIND,
                method = {})
            registerKeybinding(
                "Тонкий интент",
                GLFW.GLFW_KEY_2,
                ControlsModule.Companion.FINE_INTENT_KEYBIND,
                method = {})
            registerKeybinding(
                "Грубый интент",
                GLFW.GLFW_KEY_3,
                ControlsModule.Companion.RUDE_INTENT_KEYBIND,
                method = {})
            registerKeybinding(
                "Переключение NoClip",
                GLFW.GLFW_KEY_TAB,
                NO_CLIP_KEYBIND,
                modifiers = listOf(KeyBindModifier.Control),
                method = {
                    interactionModeManager.toggleNoClip()
                }
            )
            registerKeybinding(
                "Стандартный режим",
                GLFW.GLFW_KEY_1,
                DEFAULT_INTERACTION_MODE_KEYBIND,
                modifiers = listOf(KeyBindModifier.Control),
                method = {
                    interactionModeManager.setMode(InteractionMode.DEFAULT)
                },
                direct = true
            )
            registerKeybinding(
                "Строительный режим",
                GLFW.GLFW_KEY_2,
                BUILDER_INTERACTION_MODE_KEYBIND,
                modifiers = listOf(KeyBindModifier.Control),
                method = {
                    interactionModeManager.setMode(InteractionMode.BUILDER)
                },
                direct = true
            )
            registerKeybinding(
                "Режим ГМа",
                GLFW.GLFW_KEY_3,
                GM_INTERACTION_MODE_KEYBIND,
                modifiers = listOf(KeyBindModifier.Control),
                method = {
                    interactionModeManager.setMode(InteractionMode.GM)
                },
                direct = true
            )
            registerKeybinding(
                "Режим наблюдения",
                GLFW.GLFW_KEY_4,
                CROW_INTERACTION_MODE_KEYBIND,
                modifiers = listOf(KeyBindModifier.Control),
                method = {
                    interactionModeManager.setMode(InteractionMode.CROW)
                },
                direct = true
            )
        }
    }

    override fun getSelectedItem(): ClientItemObject? = withEventContext {
        val id = InventorySelectionCallback()
            .collectLists<ItemSelection>(ITEM_SELECT)
            .firstNotNullOfOrNull { it.selectedItem } ?: return@withEventContext null
        itemStorage.getById(id.value)
    }

    override fun getSelectedInventory(): ContainerId? = withEventContext {
        InventorySelectionCallback().collect<InventorySelection>(INVENTORY_SELECT).firstOrNull()?.selectedInventory
    }

    companion object {
        val NO_CLIP_KEYBIND = KeybindId("no-clip")
        val DEFAULT_INTERACTION_MODE_KEYBIND = KeybindId("default-interaction-mode")
        val BUILDER_INTERACTION_MODE_KEYBIND = KeybindId("builder-interaction-mode")
        val GM_INTERACTION_MODE_KEYBIND = KeybindId("gm-interaction-mode")
        val CROW_INTERACTION_MODE_KEYBIND = KeybindId("crow-interaction-mode")
        val ITEM_SELECT = EventId("item_select")
        val INVENTORY_SELECT = EventId("inventory_select")
    }
}