package org.qbrp.engine.chat.ui

import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ui.tasks.TaskManager
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority

@Autoload(LoadPriority.ADDON)
class ChatUI: ChatAddon("ui") {
    override fun getKoinModule() = module {
        single { TaskManager() }
    }

    override fun onLoad() {
        super.onLoad()
        CommandsRepository.add(listOf(get<TaskManager>()))
    }

}