package org.qbrp.system.database

import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource

object CoroutinesUtil {
    val commandsCoroutineScope = CoroutineScope(Dispatchers.IO)

    fun <T> runAsyncCommand(
        context: CommandContext<ServerCommandSource>,
        operation: suspend () -> T,
        callback: (T) -> Unit
    ) {
        commandsCoroutineScope.launch {
            val result = operation()
            context.source.server.execute {
                callback(result)
            }
        }
    }
}