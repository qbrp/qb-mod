package org.qbrp.engine.chat.ui.tasks

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import org.qbrp.core.game.registry.ServerModCommand
import java.util.UUID

class TaskManager: ServerModCommand {
    private val tasks: MutableMap<UUID, Task> = mutableMapOf()

    fun registerTask(task: Task): UUID {
        val taskId = UUID.randomUUID()
        tasks[taskId] = task
        return taskId
    }

    fun getTaskCommand(uuid: UUID) = "/task $uuid"
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("task")
            .then(argument("uuid", StringArgumentType.word())
                .executes { ctx ->
                    val player = ctx.source.player!!
                    tasks[UUID.fromString(StringArgumentType.getString(ctx, "uuid"))]?.runnable?.invoke(player)
                    1
                }))
    }
}