package org.qbrp.engine.chat.addons.utils

import PermissionManager.hasPermission
import net.minecraft.util.ActionResult
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.ui.model.Button
import org.qbrp.engine.chat.ui.model.Page
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload(LoadPriority.ADDON)
class GmUtils: ChatAddon("gm-utils") {
    override fun onLoad() {
        super.onLoad()
        MessageSendEvent.register { _, message, receiver, _ ->
            if (message.getTags().isComponentExists("gm") && receiver.hasPermission("chat.gm-util")) {
                val page = Page(
                    "ГМ-меню: ${message.getAuthorEntity()?.name?.string}",
                    "{tp} {invsee}").apply {
                    pasteElement(Button(
                        name = "&6[Телепортация ⚡]&r",
                        runnable = { plr ->
                            val target = message.getAuthorEntity()
                                ?: run { plr.sendMessage("<red>Игрок не найден".asMiniMessage()); return@Button }
                            plr.server.commandManager.executeWithPrefix(plr.commandSource, "tp ${target.name.string}")
                        }
                    ), "tp")
                    pasteElement(Button(
                        name = "&a[Инвентарь ♻]&r",
                        runnable = { plr ->
                            val target = message.getAuthorEntity()
                                ?: run { plr.sendMessage("<red>Игрок не найден".asMiniMessage()); return@Button }
                            plr.server.commandManager.executeWithPrefix(plr.commandSource, "invsee ${target.name.string}")
                        }
                    ), "invsee")
                    build(receiver)
                }
                message.getTagsBuilder()
                    .placeholder("menu", page.getOpenButton("<dark_gray>(⚡)", receiver, false).getText())
            } else {
                message.getTagsBuilder()
                    .placeholder("menu", " ")
            }
            ActionResult.PASS
        }
    }

}