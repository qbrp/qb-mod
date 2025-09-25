package org.qbrp.client.render.ui.utilview

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.MessageScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.lwjgl.glfw.GLFW
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.ClickListener
import org.qbrp.client.render.ui.components.behaviour.CloseWidget
import org.qbrp.client.render.ui.components.behaviour.OnClick
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.components.behaviour.Highlight
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.dsl.UiDsl
import org.qbrp.client.render.ui.dsl.continueView
import org.qbrp.client.render.ui.dsl.widget
import org.qbrp.client.render.ui.view.ViewManager
import org.qbrp.client.render.ui.withUiContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.modules.Priorities

@Autoload(env = EnvType.CLIENT, priority = Priorities.LOWEST)
class GameMenuModule: EngineModule("game-menu-view"), GameMenu {
    private val viewManager by inject<ViewManager>()
    private val client = MinecraftClient.getInstance()
    private val pause get() = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.handle, GLFW.GLFW_KEY_F3)
    override val isOpen: Boolean
        get() = viewManager.isOpen(ID)

    override fun getKoinModule(): Module = module {
        single<GameMenu> { this@GameMenuModule }
    }

    override fun open() = withUiContext {
        viewManager.openView(createView(), ID)
    }

    private val handleLeave = ClickListener { _, _, _ ->
        val isSingleplayer: Boolean = client.isInSingleplayer
        client.world?.disconnect()
        if (isSingleplayer) {
            this.client.disconnect(MessageScreen(VANILLA_SAVE_LEVEL_TEXT))
        } else {
            this.client.disconnect()
        }

        val titleScreen = TitleScreen()
        if (isSingleplayer) {
            this.client.setScreen(titleScreen)
        } else {
            this.client.setScreen(MultiplayerScreen(titleScreen))
        }
        true
    }

    private val handleVanillaMenu = ClickListener { _, _, _ ->
        client.openPauseMenu(pause)
        true
    }

    context(ctx: UiContext)
    private fun createView() = widget(ID) {
        anchors(0.5f, 0.3f)
        title("Игровое меню")
        content {
            vertical(2f)
            use(button("Выйти с сервера", handleLeave))
            use(button("Ванильное меню", handleVanillaMenu))
        }
    }

    @UiDsl
    context(ctx: UiContext)
    private fun button(text: String, clickHandler: ClickListener) = continueView {
        view {
            text(text)
            matchParent()
            component { OnClick(it, clickHandler, true) }
            component { OnClick(it, CloseWidget(ID), true) }
            component { OnHover(it, Highlight(HighlightParams.DEFAULT)) }
        }
    }

    companion object {
        val VANILLA_SAVE_LEVEL_TEXT: Text = Text.translatable("menu.savingLevel")
        const val ID = "game-menu"
    }
}