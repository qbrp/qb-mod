package org.qbrp.engine.chat.addons

import net.minecraft.util.ActionResult
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.KreisyEmojiConfig
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule

@Autoload(LoadPriority.ADDON)
class KreisyEmoji(): QbModule("kreisy-emoji") {
    override fun load() {
        val emojiMap: MutableMap<String, String> = mutableMapOf()
        get<KreisyEmojiConfig>().emojis.forEach { name, emoji ->
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