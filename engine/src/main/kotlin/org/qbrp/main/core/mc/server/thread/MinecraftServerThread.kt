package org.qbrp.main.core.mc.server.thread

import net.minecraft.server.MinecraftServer

class MinecraftServerThread(private val server: MinecraftServer): ServerThread {
    override fun execute(runnable: Runnable) {
        server.execute(runnable)
    }

    override fun assertOnServerThread() {
        if (!server.isOnThread) throw IllegalThreadStateException()
    }
}