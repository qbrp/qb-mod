package org.qbrp.main.engine.players.characters

import net.minecraft.util.ActionResult
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.chat.addons.tools.MessageFormatTools
import org.qbrp.main.engine.chat.addons.tools.MessageTextTools
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.engine.players.characters.components.CharactersBank
import org.qbrp.main.engine.players.model.service.PlayersUtil

data class RgbColor(val r: Int, val g: Int, val b: Int) {
    fun getHex(color: RgbColor): String {
        return "#%02X%02X%02X".format(color.r.coerceIn(0, 255), color.g.coerceIn(0, 255), color.b.coerceIn(0, 255))
    }
}

internal fun registerMessageSendColoring(config: ServerConfigData) {
    MessageSendEvent.register() { sender, message, receiver, receiverEntity, networking ->
        val player = PlayersUtil.getPlayerSession(message.authorName) ?: return@register ActionResult.PASS
        val character = player.get<CharactersBank>()?.characterData ?: return@register ActionResult.PASS
        val rpNameColor = character.colors.first()
        message.getTagsBuilder()
            .placeholder("rpNameColor", rpNameColor)
            .placeholder("rpNameColorFaded", rpNameColor)
        if (message.getTags().getComponentData<Boolean>("colorText") == true) {
            val format = MessageFormatTools
            val text = MessageTextTools
            val originalColor = parseHexToRgb(MessageFormatTools.getFormattingHex(message) ?: "#FFFFFFF")
            val rpNameColorRgb = parseHexToRgb(rpNameColor)
            val factor = config.chat.characterColoringFactor
            val newColor = blendColors(originalColor, rpNameColorRgb, factor)
            text.setTextContent(message, format.stripLeadingColor(text.getTextContent(message)))
            text.setTextContent(message, format.setFormatting(message, "<${newColor}>"))
        }
        ActionResult.PASS
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

fun blendColors(color1: RgbColor, color2: RgbColor, factor: Double): String {
    require(factor in 0.0..1.0) { "Factor must be between 0 and 1" }

    val newR = (color1.r * (1 - factor) + color2.r * factor).toInt().coerceIn(0, 255)
    val newG = (color1.g * (1 - factor) + color2.g * factor).toInt().coerceIn(0, 255)
    val newB = (color1.b * (1 - factor) + color2.b * factor).toInt().coerceIn(0, 255)

    return "#%02X%02X%02X".format(newR, newG, newB)
}