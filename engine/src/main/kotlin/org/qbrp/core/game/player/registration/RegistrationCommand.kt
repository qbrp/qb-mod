package org.qbrp.core.game.player.registration

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.KoinComponent
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.player.ServerPlayerSession
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.database.CoroutinesUtil
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.networking.messaging.NetworkManager
import org.qbrp.system.utils.format.Format.asMiniMessage

class RegistrationCommand: ServerModCommand, KoinComponent {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("reg").executes() { context ->
            try {
                val player = context.source.player!!
                val session = PlayerManager.getPlayerSession(player.name.string)!!
                CoroutinesUtil.runAsyncCommand(context,
                    operation = { session.database.register() },
                    callback = {
                        handleSuccessResponse(session, session.account.uuid.toString())
                    }
                )
            } catch(ex: Exception) {
                ex.printStackTrace()
            }
            1
        })
    }

    private fun handleSuccessResponse(session: ServerPlayerSession, code: String) {
        val message = "Зарегистрирован аккаунт с кодом ${session.account.uuid}.\nКод сохранен, включен автоматическй вход в аккаунт."
        if (Engine.isApiAvailable<ChatAPI>()) {
            Engine.getAPI<ChatAPI>()!!.sendMessage(session.entity, ChatMessage(
                SYSTEM_MESSAGE_AUTHOR, message,).apply {
                getTagsBuilder()
                    .component("channel", "default")
            })
        } else {
            session.entity.sendMessage(message.asMiniMessage())
        }
        NetworkManager.sendMessage(session.entity, Message(
            Messages.REGISTRATION_RESPONSE, StringContent(code)))
    }
}