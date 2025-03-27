package org.qbrp.engine.chat.ui.model

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.engine.chat.ui.tasks.Task
import org.qbrp.engine.chat.ui.tasks.TaskManager
import java.util.UUID

class Button(val name: String,
             val runnable: (ServerPlayerEntity) -> Unit,
             val hover: Pair<String, String>? = "show_text" to "..."): Element, KoinComponent {
    private lateinit var taskId: UUID

    override fun getText(): String {
        val hoverText = if (hover != null) "<hover:${hover.first}:${hover.second}>" else ""
        return "<click:run_command:${get<TaskManager>().getTaskCommand(taskId)}>$hoverText$name<reset>"
    }

    override fun build(player: ServerPlayerEntity) {
        get<TaskManager>().registerTask(Task(player.name.string, runnable)).also {
            taskId = it
        }
    }
}