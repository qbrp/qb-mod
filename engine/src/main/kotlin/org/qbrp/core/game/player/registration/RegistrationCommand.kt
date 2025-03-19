package org.qbrp.core.game.player.registration

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.core.game.player.PlayerManager
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAPI
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.networking.messaging.NetworkManager

class RegistrationCommand: ServerModCommand, KoinComponent {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("reg").executes() { context ->
            try {
            val player = context.source.player!!
            val session = PlayerManager.getPlayerData(player.name.string)!!
            session.database.register()
            Engine.getAPI<ChatAPI>()!!.sendMessage(player, ChatMessage(
                SYSTEM_MESSAGE_AUTHOR,
                "<green>Зарегистрирован аккаунт с кодом ${session.account.uuid}.<newline>Код сохранен, включен автоматическй вход в аккаунт.",
            ).apply {
                getTagsBuilder()
                    .component("channel", "default")
            })
            sendSuccessResponse(context, session.account.uuid.toString()) } catch(ex: Exception) {
                ex.printStackTrace()
            }
            1
        })
    }

    private fun sendSuccessResponse(ctx: CommandContext<ServerCommandSource>, code: String) {
        NetworkManager.sendMessage(ctx.source.player!!, Message(
            Messages.REGISTRATION_RESPONSE, StringContent(code)))
    }
}