package org.qbrp.main.engine.chat.addons.rp

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.chat.ChatAPI
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.engine.chat.core.system.ChatGroup
import kotlin.random.Random

class MessageCommand(val name: String, val group: ChatGroup): CommandRegistryEntry {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal(name)
                .then(CommandManager.argument("action", StringArgumentType.greedyString())
                    .executes { context ->
                        val args = StringArgumentType.getString(context, "action")
                        context.source.player!!.server.execute {
                            sendAction(context.source.player!!, args)
                        }
                        1
                    }
                )
        )
    }

    fun sendAction(player: ServerPlayerEntity, text: String) {
        try {
            val message = ChatMessage(player.name.string, text).apply {
                getTagsBuilder()
                    .placeholder("roll", Random.nextInt(0, 101).toString())
                    .component("group", group.name)
            }
            Engine.getAPI<ChatAPI>()!!.handleMessage(message)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
}