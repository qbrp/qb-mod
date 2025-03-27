package org.qbrp.engine.chat.ui

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import org.checkerframework.checker.units.qual.h
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.game.player.registration.PlayerRegistrationCallback
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ui.model.Button
import org.qbrp.engine.chat.ui.model.Page
import org.qbrp.engine.chat.ui.tasks.TaskManager
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.utils.format.Format.asMiniMessage

@Autoload(LoadPriority.ADDON)
class ChatUI: ChatAddon("ui") {
    override fun getKoinModule() = module {
        single { TaskManager() }
    }

    override fun load() {
        super.load()
        CommandsRepository.add(listOf(get<TaskManager>()))
    }

}