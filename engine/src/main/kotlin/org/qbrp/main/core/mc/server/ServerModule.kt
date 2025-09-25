package org.qbrp.main.core.mc.server

import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.core.mc.server.command.ServerCommandExecutorImpl
import org.qbrp.main.core.mc.server.context.ServerContext
import org.qbrp.main.core.mc.server.thread.MinecraftServerThread
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.require.RequireManager

@Autoload(Priorities.HIGHEST)
class ServerModule() : EngineModule("minecraft-server"), ServerContext {
    override fun getKoinModule(): Module = module {
        single<ServerThread> { MinecraftServerThread(get()) }
        single<ServerCommandExecutor> { ServerCommandExecutorImpl(get(), get(), get()) }
        single<ServerContext> { this@ServerModule }
    }
    companion object {
        const val COMMAND_EXECUTE_CHANNEL = "command_execute"
    }
    override val packetSender: ServerPacketSender by inject()
    override val postRequirePacketListener: RequireManager by inject()
}