package org.qbrp.main.core.utils

import com.mojang.brigadier.context.CommandContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.server.command.ServerCommandSource

object CoroutinesUtil {
    fun <T> runAsyncCommand(
        context: CommandContext<ServerCommandSource>,
        operation: suspend () -> T,
        callback: (T) -> Unit,
        onError: (Exception) -> Unit = { throw it }
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = operation()
                context.source.server.execute {
                    callback(result)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}