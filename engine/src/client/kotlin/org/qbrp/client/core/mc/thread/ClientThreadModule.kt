package org.qbrp.client.core.mc.thread

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT, priority = Priorities.HIGHEST)
class ClientThreadModule: EngineModule("client-thread") {
    override fun getKoinModule(): Module = module {
        single<ServerThread> { ClientThread(MinecraftClient.getInstance()) }
    }
}