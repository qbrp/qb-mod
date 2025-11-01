package org.qbrp.main.core.fabricmc.server

import net.minecraft.server.MinecraftServer

interface ThreadExecutor {
    fun execute(runnable: Runnable)
}

interface LogicThread : ThreadExecutor {
    fun assertOnServerThread()
}

class MinecraftServerThread(private val server: MinecraftServer): LogicThread {
    override fun execute(runnable: Runnable) {
        server.execute(runnable)
    }

    override fun assertOnServerThread() {
        if (!server.isOnThread) throw IllegalThreadStateException()
    }
}
