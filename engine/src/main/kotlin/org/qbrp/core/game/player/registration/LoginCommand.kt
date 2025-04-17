package org.qbrp.core.game.player.registration

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
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
import java.util.UUID

class LoginCommand: ServerModCommand {
    val chatAPI = Engine.getAPI<ChatAPI>()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
    CommandManager.literal("login")
        .then(
        CommandManager.argument("code", StringArgumentType.string()) // Добавляем аргумент
            .executes { context ->
                try {
                    val player = context.source.player!!
                    val session = PlayerManager.getPlayerSession(player)
                    val code = StringArgumentType.getString(context, "code") // Получаем значение аргумента

                    if (code == "" || code == "NONE") {
                        handleBlankResult(context); return@executes 1
                    }
                    var uuid = UUID.randomUUID()
                    try {
                        uuid = UUID.fromString(code)
                    } catch (e: IllegalArgumentException) {
                        handleBlankResult(context); return@executes 1
                    }

                    CoroutinesUtil.runAsyncCommand(context,
                        operation = { session.database.login(uuid, session.entity.name.string) },
                        callback = { loginResult ->
                            when (loginResult) {
                                LoginResult.SUCCESS -> handleSuccessResult(context, session, uuid)
                                LoginResult.ALREADY_LINKED -> handleAlreadyRegistered(context)
                                LoginResult.NOT_FOUND -> {
                                    handleNotFoundResult(context, code)
                                    handleBlankResult(context)
                                }
                            }
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                1
            }
        )
        )
    }

    private fun handleBlankResult(ctx: CommandContext<ServerCommandSource>) {
        if (chatAPI != null) {
            chatAPI.sendMessage(ctx.source.player!!,
                ChatMessage(
                    SYSTEM_MESSAGE_AUTHOR,
                    "<red>Отправьте код вашего аккаунта через команду /login <код>."
                )
                    .apply {
                        getTagsBuilder()
                            .component("channel", "registration")
                            .component("static", true)
                    }
            )
        } else {
            ctx.source.player!!.sendMessage("<red>Отправьте код вашего аккаунта через команду /login <код>".asMiniMessage())
        }
    }

    private fun handleNotFoundResult(ctx: CommandContext<ServerCommandSource>, code: String) {
        if (chatAPI != null) {
            chatAPI.sendMessage(ctx.source.player!!, ChatMessage(
                SYSTEM_MESSAGE_AUTHOR,
                "<red>Аккаунт с кодом $code не был найден.",
            ).apply {
                getTagsBuilder()
                    .component("channel", "registration")
                    .component("static", true)
            })
        } else {
            ctx.source.player!!.sendMessage("<red>Аккаунт с кодом $code не был найден.".asMiniMessage())
        }
    }

    private fun handleAlreadyRegistered(ctx: CommandContext<ServerCommandSource>) {
        if (chatAPI != null) {
            chatAPI.sendMessage(ctx.source.player!!, ChatMessage(
                SYSTEM_MESSAGE_AUTHOR,
                "<red>Имя игрока уже привязано к аккаунту.",
            ).apply {
                getTagsBuilder()
                    .component("channel", "registration")
                    .component("static", true)
            })
        } else {
            ctx.source.player!!.sendMessage("<red>Имя игрока уже привязано к аккаунту.".asMiniMessage())
        }
    }

    private fun handleSuccessResult(ctx: CommandContext<ServerCommandSource>, data: ServerPlayerSession, uuid: UUID) {
        if (chatAPI != null) {
            NetworkManager.sendMessage(
                ctx.source.player!!,
                Message(Messages.REGISTRATION_RESPONSE, StringContent(uuid.toString()))
            )
            chatAPI.sendMessage(ctx.source.player!!, ChatMessage(
                SYSTEM_MESSAGE_AUTHOR,
                "<green>Вход в аккаунт выполнен. Приятной игры.",
            ).apply {
                getTagsBuilder()
                    .component("channel", "default")
                    .component("static", false)
            })
        } else {
            ctx.source.player!!.sendMessage("<green>Вход в аккаунт выполнен. Приятной игры.".asMiniMessage())
        }
    }

}