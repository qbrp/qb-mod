package org.qbrp.main.engine.players.model.components

import net.minecraft.util.ActionResult
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.storage.Table
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.players.accounts.AccountUpdate
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject

interface PlayerComponentBehaviour: Component<PlayerState> {
    override var state: PlayerState
    val player: PlayerObject get() = this.state.parent
    val serverPlayer: ServerPlayerObject get() = this.state.parent as ServerPlayerObject

    fun onChatMessage(sender: MessageSender, message: ChatMessage): ActionResult = ActionResult.PASS
    fun onAccountSave(account: Account, table: Table) = AccountUpdate(emptyList())
    fun onKeybind(id: String) {}
}
