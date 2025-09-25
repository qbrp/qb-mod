package org.qbrp.client.render.ui

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.mc.connection.event.ConnectEvent
import org.qbrp.client.core.mc.connection.event.DisconnectEvent
import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.core.mouse.events.MouseClickEvent
import org.qbrp.client.core.mouse.events.MouseDraggedEvent
import org.qbrp.client.core.mouse.events.MouseReleasedEvent
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.screen.event.ScreenRenderPipeline
import org.qbrp.client.render.text.render.model.TextComponentRenderer
import org.qbrp.client.render.text.render.model.TextComponentRendererRegistry
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.client.render.ui.components.behaviour.MouseClickedOutView
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.text.Text
import org.qbrp.client.render.ui.components.texture.Background
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.components.transform.RootLayout
import org.qbrp.client.render.ui.dsl.buildView
import org.qbrp.client.render.ui.dsl.finish
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.client.render.ui.view.ScreenEventHandler
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewManager
import org.qbrp.client.render.ui.view.event.UiEventListener
import org.qbrp.client.render.window.Window
import org.qbrp.client.render.window.WindowResizeEvent
import org.qbrp.main.core.components.ComponentRegistrar
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.EventId
import org.qbrp.main.core.events.EventType
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.text.literal

@Autoload(6, env = EnvType.CLIENT)
class UiModule: EngineModule("ui") {
    override fun getKoinModule(): Module = module {
        single { Screen() }
        single { ViewManager(get(), get(), get(), get()) }
        single {
            UiContext(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }
    private val mouseState by inject<MouseState>()
    private val window by inject<Window>()

    override fun onEnable() {
        UiComponentRegistrar(get<ComponentRegistrar>()).registerAll()
        instantiateScreenView(get())

        withEventContext {
            with(get<UiContext>()) {
                registerEvents()
            }
        }
    }

    context(ctx: EventContext, ui: UiContext)
    private fun registerEvents() {
        val viewManager = get<ViewManager>()
        val screen = get<Screen>()
        on<ScreenRenderPipeline> {
            viewManager.frame()
            viewManager.onRender(context, mouseState.pos)
        }
        on<WindowResizeEvent> { viewManager.onResize(client, window) }
        on<DisconnectEvent> { viewManager.closeAll() }
        on<MouseClickEvent, Boolean> { viewManager.mouseClicked(pos, button) }
        on<MouseDraggedEvent, Boolean> { viewManager.mouseDragged(pos); true }
        on<MouseReleasedEvent, Boolean> { viewManager.mouseReleased(pos, button) }
        ctx.eventBus.subscribe(
            UiEventListener<MouseClickedOutView, MouseClickEvent, Boolean>(
                EventType(EventId.ofKclass(MouseClickEvent::class)),
                5,
                ScreenEventHandler(
                    screen,
                    MouseClickedOutView::class,
                ) { component, event ->
                    component.shouldClose = true
                    false
                }
            )
        )
        ClientTickEvents.END_CLIENT_TICK.register { viewManager.tick() }
    }


    private fun instantiateScreenView(screen: Screen) {
        screen.add(RectTransform(screen.componentContext, Anchors()))
        screen.add(RootLayout(screen.componentContext))
    }
}