package org.qbrp.client.render.ui.interaction

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.qbrp.client.controls.ClientInteractionModeManager
import org.qbrp.client.controls.InteractionContextMenuApplyEvent
import org.qbrp.client.controls.InteractionModeApplyEvent
import org.qbrp.client.controls.NO_CLIP_KEYBIND
import org.qbrp.client.controls.NoClipApplyEvent
import org.qbrp.client.controls.onKeybind
import org.qbrp.client.core.mc.connection.ConnectEvent
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.client.render.ui.interaction.crosshair.IntentCrosshair
import org.qbrp.client.core.onClientTick
import org.qbrp.client.render.screen.event.ScreenRenderPipeline
import org.qbrp.main.engine.tooltip.TooltipColorProfile
import org.qbrp.client.render.ui.interaction.list.InteractionListFactory
import org.qbrp.client.render.ui.interaction.list.InteractionListManager
import org.qbrp.client.render.ui.interaction.list.InteractionListManagerImpl
import org.qbrp.client.render.ui.interaction.mode.InteractionModeViewManager
import org.qbrp.client.render.ui.withUiContext
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.controls.entity.InteractionMode

@Autoload(6, env = EnvType.CLIENT)
class InteractionsViewModule : EngineModule("interactions-render") {
    private val crosshair: IntentCrosshair by lazy { IntentCrosshair(get()) }
    private val screen by inject<Screen>()

    override fun getKoinModule(): Module = innerWithApi({
        single { InteractionListFactory(get()) }
        single<InteractionListManager> { getLocal<InteractionListManagerImpl>() }
    }, {
        scoped<TooltipColorProfile> { TooltipColorProfile.GRAY }
        scoped { InteractionModeViewManager(get()) }
        scoped { InteractionListManagerImpl(get(), get(), getLocal()) }
    })

    override fun onEnable() = withUiContext {
        val interactionListManager: InteractionListManagerImpl = getLocal()
        val modeManager: InteractionModeViewManager = getLocal()
        val clientInteractionModeManager = get<ClientInteractionModeManager>()
        on<InteractionContextMenuApplyEvent> { interactionListManager.openInteractionList(contextMenuViewData) }
        on<ConnectEvent> { instantiateCross() }
        on<ScreenRenderPipeline> { renderCross() }
        on<NoClipApplyEvent> {
            if (clientInteractionModeManager.noClipAccess) {
                modeManager.flashNoClip()
            }
        }
        on<InteractionModeApplyEvent> {
            if (clientInteractionModeManager.hasAccess(interactionMode) || clientInteractionModeManager.currentMode != InteractionMode.CROW) {
                modeManager.flashInteractionMode()
            }
        }
        onKeybind(NO_CLIP_KEYBIND) {
            modeManager.flashNoClip()
        }
        onClientTick {
            modeManager.tick()
        }
    }

    private fun instantiateCross() {
        screen.addView(crosshair.cross)
    }

    private fun renderCross() {
        crosshair.render()
    }

    companion object {
        const val VIEW_ID = "contextmenu"
    }
}
