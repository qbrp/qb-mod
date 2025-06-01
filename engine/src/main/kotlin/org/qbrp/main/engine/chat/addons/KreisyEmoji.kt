package org.qbrp.main.engine.chat.addons

import net.minecraft.util.ActionResult
import org.koin.dsl.module
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.data.config.KreisyEmojiConfig
import org.qbrp.main.engine.chat.addons.tools.MessageTextTools
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule

@Autoload(LoadPriority.ADDON)
class KreisyEmoji(): QbModule("kreisy-emoji") {
    override fun onLoad() {
        val emojiMap: MutableMap<String, String> = mutableMapOf()
        getLocal<KreisyEmojiConfig>().emojis.forEach { name, emoji ->
            emojiMap[":$name:"] = emoji.symbol
        }

        MessageReceivedEvent.EVENT.register() { message ->
            var text = MessageTextTools.getTextContent(message)
            emojiMap.forEach { emoji, symbol ->
                text = text.replace(emoji, symbol)
            }
            MessageTextTools.setTextContent(message, text)
            ActionResult.PASS
        }
    }

    override fun getKoinModule() = module {
        single { ServerResources.getKreisyEmojiConfig() }
    }
}