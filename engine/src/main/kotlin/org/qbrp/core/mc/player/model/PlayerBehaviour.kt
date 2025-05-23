package org.qbrp.core.mc.player.model

import com.mongodb.client.MongoDatabase
import net.minecraft.util.ActionResult
import org.bson.conversions.Bson
import org.koin.core.context.GlobalContext
import org.qbrp.core.game.model.components.Behaviour
import org.qbrp.core.mc.player.Account
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.MessageSender
import org.qbrp.system.utils.format.Format.asMiniMessage

open class PlayerBehaviour: Behaviour() {
    companion object {
        const val TICK_RATE = 1
        val KOIN = GlobalContext.get()
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

    open fun onAccountSave(account: Account, db: MongoDatabase) = AccountUpdate(emptyList())
}