package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON, EnvType.CLIENT, false)
class Todo : ClientChatAddon("todo") {

    override fun load() {
        super.load()
        MessageAddedEvent.EVENT.register { message, sender ->
            message.setText(transformText(message.getText()))
            ActionResult.PASS
        }
    }

    private fun transformText(input: String): String {
        val regex = Regex("""\*\s*(.+?)\s*\*""")
        return regex.replace(input) { match ->
            val start = match.range.first
            val end = match.range.last
            val before = input.substring(0, start)
            val after = input.substring(end + 1)
            val hasBefore = before.trim().isNotEmpty()
            val hasAfter = after.trim().isNotEmpty()
            val content = match.groupValues[1]
            val prefix = if (hasBefore) "<bold>—</bold> " else ""
            val suffix = if (hasAfter) " <bold>—</bold>" else ""
            "<aqua>$prefix$content$suffix</aqua>"
        }
    }
}
