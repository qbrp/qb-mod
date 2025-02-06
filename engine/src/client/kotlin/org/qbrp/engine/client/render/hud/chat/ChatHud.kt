package org.qbrp.engine.client.render.hud.chat

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.render.Tessellator
import net.minecraft.client.util.Window
import net.minecraft.entity.player.PlayerEntity
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.render.hud.HudElement
import org.qbrp.system.utils.world.getPlayersInRadius

class ChatHud: HudElement {
    companion object { private const val TYPING_PLAYER_DISTANCE = 15 }
    lateinit var textRenderer: TextRenderer

    private fun getTypingPlayers(player: PlayerEntity): List<PlayerEntity> {
        return EngineClient.getChatModuleAPI()?.getWritingPlayers(player.world)?.getPlayersInRadius(player, TYPING_PLAYER_DISTANCE.toDouble()) ?: listOf()
    }

    private fun buildTypingPlayersString(players: List<PlayerEntity>): String {
        return if (players.isNotEmpty()) {
            players.joinToString(", ") { player -> player.displayName.string } + if (players.size == 1) " печатает..." else " печатают..."
        } else {
            ""
        }
    }

    private fun getTextHeight(client: MinecraftClient): Int {
        val isChatOpen = client.currentScreen is ChatScreen
        return if (!isChatOpen) {
            3
        } else {
            18
        }
    }

    override fun render(drawContext: DrawContext, tickDelta: Float) {
        if (!::textRenderer.isInitialized) {
            textRenderer = MinecraftClient.getInstance().textRenderer
        }
        val client = MinecraftClient.getInstance()
        val clientPlayer = client.player ?: return
        val window = client.window

        val scale = 1f
        val scaledWidth = (window.scaledWidth / scale).toInt()
        val scaledHeight = (window.scaledHeight / scale).toInt()

        drawContext.matrices.push()
        drawContext.matrices.scale(scale, scale, 1f)

        val x = (3 / scale).toInt()
        val y = (scaledHeight - textRenderer.fontHeight - getTextHeight(client) / scale).toInt()

        val players = getTypingPlayers(clientPlayer)
        if (players.isNotEmpty()) {
            drawContext.drawText(
                textRenderer,
                buildTypingPlayersString(players),
                x, y,
                0xe6e6e6,
                true
            )
        }
        drawContext.matrices.pop()
    }
}