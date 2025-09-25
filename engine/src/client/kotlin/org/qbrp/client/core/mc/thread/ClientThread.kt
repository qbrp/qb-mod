package org.qbrp.client.core.mc.thread

import net.minecraft.client.MinecraftClient
import org.qbrp.main.core.mc.server.thread.ServerThread

class ClientThread(private val client: MinecraftClient): ServerThread {
    override fun assertOnServerThread() {
        if (!client.isOnThread) throw IllegalThreadStateException()
    }

    override fun execute(runnable: Runnable) {
        client.execute(runnable)
    }
}