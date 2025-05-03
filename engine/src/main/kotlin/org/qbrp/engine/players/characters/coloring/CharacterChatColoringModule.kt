package org.qbrp.engine.players.characters.coloring

import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.qbrp.core.mc.player.PlayerManager
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.tools.MessageFormatTools
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.players.characters.Character
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON - 1)
class CharacterChatColoringModule: ChatAddon("character-chat-coloring") {
    override fun load() {
        super.load()
        MessageSendEvent.register() { sender, message, receiver, networking ->
            val player = PlayerManager.getPlayerSession(message.authorName) ?: return@register ActionResult.PASS
            val character = player.getComponent<Character>() ?: return@register ActionResult.PASS
            val rpNameColor = character.data.colors.first()
            message.getTagsBuilder()
                .placeholder("rpNameColor", rpNameColor)
                .placeholder("rpNameColorFaded", rpNameColor)
            if (message.getTags().getComponentData<Boolean>("colorText") == true) {
                val format = MessageFormatTools
                val text = MessageTextTools
                val originalColor = parseHexToRgb(MessageFormatTools.getFormattingHex(message) ?: "#FFFFFFF")
                val rpNameColorRgb = parseHexToRgb(rpNameColor)
                val config = get<ServerConfigData>()
                val factor = config.chat.characterColoringFactor
                val newColor = blendColors(originalColor, rpNameColorRgb, factor)
                text.setTextContent(message, format.stripLeadingColor(text.getTextContent(message)))
                text.setTextContent(message, format.setFormatting(message, "<${newColor}>"))
            }
            ActionResult.PASS
        }
    }

    data class RgbColor(val r: Int, val g: Int, val b: Int) {
        fun getHex(color: RgbColor): String {
            return "#%02X%02X%02X".format(color.r.coerceIn(0, 255), color.g.coerceIn(0, 255), color.b.coerceIn(0, 255))
        }

    }

    fun parseHexToRgb(hex: String): RgbColor {
        val cleanHex = hex.removePrefix("#").padStart(6, '0')
        val r = cleanHex.substring(0, 2).toInt(16)
        val g = cleanHex.substring(2, 4).toInt(16)
        val b = cleanHex.substring(4, 6).toInt(16)
        return RgbColor(r, g, b)
    }

    fun getDarkness(color: RgbColor): Int {
        val avg = (color.r + color.g + color.b) / 3
        return 255 - avg
    }

    /**
     * Смешивает color1 и color2:
     * factor = 0.0 → чистый color1,
     * factor = 1.0 → чистый color2,
     * промежуточные значения — линейная интерполяция.
     */
    fun blendColors(color1: RgbColor, color2: RgbColor, factor: Double): String {
        require(factor in 0.0..1.0) { "Factor must be between 0 and 1" }

        val newR = (color1.r * (1 - factor) + color2.r * factor).toInt().coerceIn(0, 255)
        val newG = (color1.g * (1 - factor) + color2.g * factor).toInt().coerceIn(0, 255)
        val newB = (color1.b * (1 - factor) + color2.b * factor).toInt().coerceIn(0, 255)

        return "#%02X%02X%02X".format(newR, newG, newB)
    }
}