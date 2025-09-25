package org.qbrp.main.engine.controls

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.game.GameTicker
import org.qbrp.main.core.keybinds.KeybindId
import org.qbrp.main.core.keybinds.onKeybind
import org.qbrp.main.core.mc.context.withMinecraftContext
import org.qbrp.main.core.mc.player.events.MinecraftPlayerLeaveEvent
import org.qbrp.main.core.mc.server.context.withCoreContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator
import org.qbrp.main.core.networking.ServerNetworkContext
import org.qbrp.main.engine.controls.dsl.InteractionDslContext
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.system.lookup.LookupManager
import org.qbrp.main.engine.controls.dto.InteractionViewMapper
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.controls.model.InteractionNodeComposer
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.networking.InteractCallPacket
import org.qbrp.main.engine.controls.networking.InteractionModeApplyPacket
import org.qbrp.main.engine.controls.networking.InteractionSelectPacket
import org.qbrp.main.engine.controls.networking.NoClipPacket
import org.qbrp.main.engine.controls.networking.ServerInteractionListener
import org.qbrp.main.engine.controls.system.InteractionCallRequest
import org.qbrp.main.engine.controls.system.InteractionCallSystem
import org.qbrp.main.engine.controls.system.InteractionListRequest
import org.qbrp.main.engine.controls.system.InteractionNetworkSystem
import org.qbrp.main.engine.controls.system.InteractionSystem
import org.qbrp.main.engine.controls.system.InteractionSystemImpl
import org.qbrp.main.engine.controls.system.modes.InteractionModeSystem
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.engine
import org.qbrp.main.engine.players.model.events.PlayerInstantiateEvent

@Autoload(Priorities.LOWEST)
class ControlsModule: EngineModule("controls"), ServerInteractionListener {
    private val interactionSystem: InteractionSystem by inject()

    override fun getKoinModule(): Module = module {
        single { InteractionViewMapper(get()) }
        single { InteractionService(get()) }
        single { LookupManager(get(), get()) }
        single { InteractionDslContext(get()) }
        single { InteractionNodeComposer(get()) }
        single<InteractionSystem> {
            InteractionSystemImpl(
                InteractionCallSystem(),
                InteractionNetworkSystem(get(), get(), get()),
                InteractionModeSystem(),
                get()
            )
        }
    }

    override fun onEnable() = withCoreContext {
        val interactionService = get<InteractionService>()
        val interactionSystem = interactionSystem as InteractionSystemImpl

        get<GameTicker>().addTickTask {
            interactionSystem.update()
        }

        get<PacketListenerActivator<ServerNetworkContext>>().run {
            run(INTERACT_CHANNEL,
                this@ControlsModule,
                { buf, ctx  ->
                    InteractCallPacket(buf, ctx.player.engine, get<ItemStorage<ServerItemObject>>()) }
            )
            run(SELECT_INTERACTION_CHANNEL,
                this@ControlsModule,
                { buf, ctx  -> InteractionSelectPacket(buf, ctx.player.engine) })
            run(NO_CLIP_CHANNEL,
                this@ControlsModule,
                { buf, ctx  -> NoClipPacket(buf, ctx.player.engine) })
            run(INTERACTION_MODE_CHANNEL,
                this@ControlsModule,
                { buf, ctx  -> InteractionModeApplyPacket(buf, ctx.player.engine) })
        }

        on<PlayerInstantiateEvent>(priority = EventPriority.LATEST) {
            interactionService.onPlayerJoin(player)
            interactionSystem.onPlayerJoin(player)
        }

        on<MinecraftPlayerLeaveEvent> {
            interactionService.onPlayerLeave(player.uuid)
        }

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

    override fun onListRequest(
        player: ServerPlayerObject,
        call: InteractionRequestContext
    ) = withEventContext {
        interactionSystem.requestList(InteractionListRequest(call))
    }

    override fun onSelectInteraction(player: ServerPlayerObject, id: InteractionId) = withMinecraftContext {
        interactionSystem.requestCall(InteractionCallRequest.Manual(player, id))
    }

    override fun onInteractionMode(player: ServerPlayerObject, mode: InteractionMode) {
        interactionSystem.setInteractionMode(player, mode)
    }

    override fun onNoClip(player: ServerPlayerObject, enabled: Boolean) {
        interactionSystem.setNoClip(player, enabled)
    }

    companion object {
        val DEFAULT_INTENT_KEYBIND = KeybindId("default_intent")
        val FINE_INTENT_KEYBIND = KeybindId("fine_intent")
        val RUDE_INTENT_KEYBIND = KeybindId("rude_intent")
        val TOGGLE_ALTERNATE_KEYBIND = KeybindId("toggle_alternate")
        const val SYNC_CHANNEL = "interaction_sync"
        const val INTERACTION_LIST_CHANNEL = "interaction_list"
        const val INTERACT_CHANNEL = "subinteraction"
        const val SELECT_INTERACTION_CHANNEL = "select_interaction"
        const val INTERACTION_MODE_CHANNEL = "interaction_mode"
        const val NO_CLIP_CHANNEL = "noclip"
    }
}