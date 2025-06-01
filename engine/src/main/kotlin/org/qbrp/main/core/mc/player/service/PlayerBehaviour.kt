package org.qbrp.main.core.mc.player.service

import net.minecraft.util.ActionResult
import org.qbrp.main.core.game.model.components.Behaviour
import org.qbrp.main.core.mc.player.Account
import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.storage.Table
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.core.utils.format.Format.asMiniMessage

open class PlayerBehaviour: Behaviour() {
    companion object {
        const val TICK_RATE = 1
    }

    protected val player: PlayerObject
        get() = requireState().getObject<PlayerObject>()

    protected fun sendMessage(text: String) {
        val chatAPI = Engine.getAPI<ChatAPI>()
        if (chatAPI != null) {
            chatAPI.sendMessage(player.entity, text)
        } else {
            player.entity.sendMessage(text.asMiniMessage())
        }
    }

    open fun onMessage(sender: MessageSender, message: ChatMessage): ActionResult = ActionResult.PASS

    open fun onAccountSave(account: Account, table: Table) = AccountUpdate(emptyList())
}