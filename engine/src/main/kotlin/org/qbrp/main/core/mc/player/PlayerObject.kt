package org.qbrp.main.core.mc.player

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.mc.McObject
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.messages.MessageSender
import org.qbrp.main.engine.players.characters.appearance.Appearance
import org.qbrp.main.engine.players.nicknames.NicknamesModule
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil
import org.qbrp.main.core.utils.format.Format.asMiniMessage

class PlayerObject(
    val entity: ServerPlayerEntity,
    var account: Account,
    lifecycle: Lifecycle<McObject> ,
    override val state: State = State(),
): McObject(lifecycle), Tick<ServerWorld> {
    override val pos: Vec3d
        get() = entity.pos
    val name: String
        get() = entity.name.string

    override fun getTooltip(): String {
        return "$displayName &d (&r ${state.getComponentOrThrow<Appearance>().tooltip} &d)"
    }

    override fun tick(context: ServerWorld) = state.tick(context)

    fun sendNetworkMessage(message: Message) {
        NetworkUtil.sendMessage(entity, message)
    }

    fun onMessageSend(sender: MessageSender, message: ChatMessage): ActionResult {
        state.behaviours.forEach {
            it as PlayerBehaviour
            val result = it.onMessage(sender, message)
            if (result != ActionResult.PASS) return result
        }
        return ActionResult.PASS
    }

    val displayName: String
        get() = state.getComponent<NicknamesModule.NicknameManager>()?.getDisplayName()
            ?: entity.name.string

    val displayNameText: Text
        get() = displayName.asMiniMessage()

    fun executeCommand(command: String) {
        NetworkUtil.sendMessage(entity, Message(Messages.INVOKE_COMMAND, StringContent(command)))
    }
}
